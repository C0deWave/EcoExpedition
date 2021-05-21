package com.example.eco

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_make_account.*
import kotlinx.android.synthetic.main.activity_setting_account.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class settingAccount : AppCompatActivity() {

    val GET_GALLERY_IMAGE = 200;    // 안드로이드에서 이미지를 가져오기 상태 표시 위한 전역 변수
    var selectImageUri: Uri? = null // 선택된 이미지의 Uri
    var age = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_account)
        val pwd = intent.getStringExtra("pwd")
        nameText_settingAccount.setText(intent.getStringExtra("name"))
        pwd1Text_settingAccount.setText(pwd)
        pwd2Text_settingAccount.setText(pwd)

        // 스피너를 할당합니다.
        ArrayAdapter.createFromResource(
                this,
                R.array.old,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
            var listener = SpinnerListener()
            spinner2.onItemSelectedListener = listener
        }

        userImageView_settingAccount.setOnClickListener {
            getImage()
        }

        changeUserInfo_settingAccount.setOnClickListener {
            if (CheckPassword()){
                changeAccount()
            }
        }
    }

    private fun changeAccount() {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {
                val email = loadFromInnerStorage("userInfoData.txt")
                val password = pwd1Text_settingAccount.text
                val name = nameText_settingAccount.text

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
                        .url("https://9rtv5m5787.execute-api.ap-northeast-2.amazonaws.com/member_update")
                        .put(body)
                        .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(applicationContext, "계정변경완료!!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                    }
                })

            }.await()
        }

    }

    fun loadFromInnerStorage(filename: String): String? {
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream?.reader()?.readText()
    }

    private fun CheckPassword(): Boolean {

        if (pwd1Text_settingAccount.text.toString() != pwd2Text_settingAccount.text.toString()){
            Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pwd1Text_settingAccount.text.isEmpty()){
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
            userImageView_settingAccount.setImageURI(selectImageUri)
        }
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
            1 -> age = "15"
            2 -> age = "25"
            3 -> age = "35"
            4 -> age = "45"
            5 -> age = "55"
            6 -> age = "65"
            7 -> age = "75"
            8 -> age = "85"
            9 -> age = "95"
            10 -> age = "105"
        }
    }
}