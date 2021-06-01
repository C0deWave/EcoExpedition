package com.example.eco.ActivityOrFragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.eco.ClassRes.URIPathHelper
import com.example.eco.R
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

class MakeAccountActivity : AppCompatActivity() {

    var age = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_account)

        // 뒤로가기 버튼을 눌렀을때
        backButton_makeAccount.setOnClickListener {
            val intent = Intent(applicationContext, BannerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }


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
                            saveToInnerStorage("${name}","userInfoData.txt")
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