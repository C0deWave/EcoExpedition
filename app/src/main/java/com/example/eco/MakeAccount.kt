package com.example.eco

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_make_account.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class MakeAccount : AppCompatActivity() {

    val GET_GALLERY_IMAGE = 200;    // 안드로이드에서 이미지를 가져오기 상태 표시 위한 전역 변수
    var selectImageUri: Uri? = null // 선택된 이미지의 Uri
    var age = ""


    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String):String{
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream.reader().readText()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_account)

        // 나이 선택 부분 스피너 어댑터
        //val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.old,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            var listener = SpinnerListener()
            spinner.onItemSelectedListener = listener

        }
        // 뒤로가기 버튼을 눌렀을때
        backButton_makeAccount.setOnClickListener {
            val intent = Intent(applicationContext, banner::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // 이미지 변경기능
        userImageView.setOnClickListener { getImage() }

        // 회원가입 버튼
        signInButton_MakeAccount.setOnClickListener {
            if (CheckPassword()){
                makeAccount()
            }
        }

        // 동적으로 비밀번호 확인하는 기능
        passwordText2_MakeAccount.doAfterTextChanged { isRightPassword() }
        passwordText1_MakeAccount.doAfterTextChanged { isRightPassword() }
    }

    inner class SpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { // p2가 사용자가 선택한 곳의 인덱스
            Log.d("지정한 나이", p2.toString())
            setAge(p2)
        }
    }

    private fun setAge(p2: Int) {
        when(p2){
            0 -> age = "5"
            0 -> age = "15"
            0 -> age = "25"
            0 -> age = "35"
            0 -> age = "45"
            0 -> age = "55"
            0 -> age = "65"
            0 -> age = "75"
            0 -> age = "85"
            0 -> age = "95"
            0 -> age = "105"
        }
    }

    private fun makeAccount() {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {
                //데이터 꺼네오기
                val email = editTextTextPersonName.text
                val password = passwordText2_MakeAccount.text
                val name = editTextTextPersonName4.text

                // 보낼 데이터 json으로 만들기
                val data = "{\n" +
                        "    \"email\" : \"${email}\",\n" +
                        "    \"name\" : \"${name}\",\n" +
                        "    \"pswd\" : \"${password}\",\n" +
                        "    \"age\" : \"${age}\",\n" +
                        "    \"d_amount\" : \"0\",\n" +
                        "    \"pic\" : \"\",\n" +
                        "    \"p_group\" : \"\"\n" +
                        "}"

                val media = "application/json; charset=utf-8".toMediaType();
                val body = data.toRequestBody(media)

                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder()
                        .url("https://3zz6r8wnma.execute-api.ap-northeast-2.amazonaws.com/member_insert")
                        .post(body)
                        .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(applicationContext, "회원가입성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            saveToInnerStorage("${email}","userInfoData.txt")
                            startActivity(intent)
                        }
                    }
                })

            }.await()
        }

    }

    private fun isRightPassword() {
        if (passwordText1_MakeAccount.text.toString() == passwordText2_MakeAccount.text.toString()){
            if (passwordText1_MakeAccount.text.length > 8){
                guideText_MakeAccount.text = "비밀번호가 일치합니다."
                guideText_MakeAccount.setTextColor(Color.parseColor("#045FB4"))
            }else{
                guideText_MakeAccount.text = "비밀번호를 길게 해주세요."
                guideText_MakeAccount.setTextColor(Color.parseColor("#DF01D7"))
            }
        }else{
            guideText_MakeAccount.text = "비밀번호가 같지 않습니다."
            guideText_MakeAccount.setTextColor(Color.parseColor("#B40404"))
        }
    }

    private fun CheckPassword(): Boolean {

        if (passwordText1_MakeAccount.text.toString() != passwordText2_MakeAccount.text.toString()){
            Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (passwordText1_MakeAccount.text.isEmpty()){
            Toast.makeText(applicationContext, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun getImage() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        startActivityForResult(intent, GET_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            //이미지 뷰를 해당 이미지로 교환합니다.
            selectImageUri = data.data!!
            userImageView.setImageURI(selectImageUri)
        }
    }


    fun saveToInnerStorage(text:String, filename:String){
        //내부저장소의 전달된 파일이름의 파일 출력스트림을 가져온다.
        //MODE_APPEND = 파일에 기존의 내용 이후에 붙이는 모드입니다.
        //MODE_PRIVATE 앱 전용으로 만들어 다른 앱에서는 접근 불가, 이미 파일이 있는 경우 기존 파일에 덮어씁니다.
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
        //출력 스트림에 text를 바이트로 전환하여 write한다.
        fileOutputStream.write(text.toByteArray())
        //파일 출력 스트림을 닫는다.
        fileOutputStream.close()
    }


}