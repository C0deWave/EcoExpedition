package com.example.eco

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_banner.*
import kotlinx.android.synthetic.main.activity_make_account.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class banner : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (loadFromInnerStorage("userInfoData.txt") != null && loadFromInnerStorage("userInfoData.txt") != ""){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        setContentView(R.layout.activity_banner)

        // 뷰페이저에 어댑터를 할당합니다.
        var adapter : ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager2.bringToFront()
        val page1 = LoginSelectFragment()
        adapter.addFragment(page1, "1")
        val page2 = LoginFragment()
        adapter.addFragment(page2, "1")
        viewPager2.adapter = adapter;
    }

    fun goLoginPager(){
        viewPager2.arrowScroll(View.FOCUS_RIGHT)
    }

    fun goSignPage(){
        val intent = Intent(applicationContext, MakeAccount::class.java)
        startActivity(intent)
    }

    fun backPager(){
        viewPager2.arrowScroll(View.FOCUS_LEFT)
        viewPager2.arrowScroll(View.FOCUS_LEFT)
    }

    //로그인 기능을 구현합니다.
    fun Login(id: String,password: String){
        CoroutineScope(Dispatchers.Main).launch {

            val api = CoroutineScope(Dispatchers.Default).async {
                // 보낼 데이터 json으로 만들기
                val data = "{\n" +
                        "    \"email\" : \"${id}\"" +
                        "}"

                val media = "application/json; charset=utf-8".toMediaType();
                val body = data.toRequestBody(media)

                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder()
                        .url("https://p7s3gkde6f.execute-api.ap-northeast-2.amazonaws.com/member_get/")
                        .put(body)
                        .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            // 회원조회 응답
                            val data = response.body!!.string()
                            Log.d("epdlxj","${data}")
                            var rawData2 = data.substring(31,data.length-4)
                            val res = Gson().fromJson(rawData2 , LoginInfo::class.java)
                            if (res.pswd.equals(password)){
                                saveToInnerStorage("${id}","userInfoData.txt")
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                })

            }.await()
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

    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream?.reader()?.readText()
    }
}
