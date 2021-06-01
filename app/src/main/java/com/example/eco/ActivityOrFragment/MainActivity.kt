package com.example.eco.ActivityOrFragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.eco.R
import com.example.eco.dataClass.UserInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var res : UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.setSelectedItemId(R.id.menu1)
        supportFragmentManager.beginTransaction().add(R.id.linearLayout, MainViewFragment1()).commit()
    }

    override fun onResume() {
        super.onResume()
        Log.d("초기 설정중","")

        val name =  loadFromInnerStorage("userInfoData.txt")
        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{" +
                    "    \"name\" : \"${name}\"" +
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
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
//                        try {
                        val data = response.body!!.string()
                        var rawData2 = data.substring(31, data.length - 4)
                        Log.d("data", "${rawData2}")
                        res = Gson().fromJson(rawData2, UserInfo::class.java)
                        Log.d("초기 설정 완료", "${rawData2.toString()}")

//                        }catch (e : Exception){
//                            Log.d("fragment4","${e}")
//                        }
                    }
                }
            })

        }
    }

    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream?.reader()?.readText()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu1 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment1()).commitAllowingStateLoss()
                return true
            }
            R.id.menu2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment2()).commitAllowingStateLoss()
                return true
            }
            R.id.menu3 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment3()).commitAllowingStateLoss()
                return true
            }
            R.id.menu4 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment4()).commitAllowingStateLoss()
                return true
            }

        }
        return false
    }
}