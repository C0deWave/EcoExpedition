package com.example.eco.ActivityOrFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eco.R
import com.example.eco.adapter.ViewPagerAdapter
import com.example.eco.dataClass.UserInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_banner.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception

class BannerActivity : AppCompatActivity() {


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
        val intent = Intent(applicationContext, MakeAccountActivity::class.java)
        startActivity(intent)
    }

    fun backPager(){
        viewPager2.arrowScroll(View.FOCUS_LEFT)
        viewPager2.arrowScroll(View.FOCUS_LEFT)
    }

    //로그인 기능을 구현합니다.
    fun Login(id: String,password: String){

        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"name\" : \"${id}\"" +
                    "}"

            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            //api 요청
            val client = OkHttpClient.Builder().build()
            val req = Request.Builder()
                    .url("https://p7s3gkde6f.execute-api.ap-northeast-2.amazonaws.com/member_get/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        val data = response.body!!.string()
                        loginProcess(data,id,password)
                    }
                }
            })
        }
    }

   fun loginProcess(data: String,id: String,password: String) {
       Log.d("111","${data}")
        var rawData2 = data.substring(31,data.length-4)
       Log.d("user","${rawData2}")
        val res = Gson().fromJson(rawData2 , UserInfo::class.java)
        if (res.pswd.equals(password)){
            saveToInnerStorage("${id}","userInfoData.txt")
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun saveToInnerStorage(text:String, filename:String){
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        try {
            val fileInputStream :  FileInputStream? = openFileInput(filename)
            if (fileInputStream == null){
                Log.d("파일스트림","null")
                return ""
            }else{
                return fileInputStream?.reader()?.readText()
            }
        }catch (e : Exception){
            Log.d("err","${e.stackTrace}")
        }
        return ""
    }
}
