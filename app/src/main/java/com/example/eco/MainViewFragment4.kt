package com.example.eco

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class MainViewFragment4 : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // API 네트워크 통신을 위한 예제
        coroutine()
    }

    // API 네트워크 통신을 위한 예제
    private fun coroutine() {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {
                //네트워크
                getHtmlStr()
            }.await()
        }
    }

    // API 네트워크 통신을 위한 예제
    private fun getHtmlStr(){
        val da = "{\"amount\" : 10000}"
        val media = "application/json; charset=utf-8".toMediaType();
        val body = da.toRequestBody(media)

        // 1. 클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        // 2. 요청
        val req = Request.Builder().url("https://l1tm80coq1.execute-api.ap-northeast-2.amazonaws.com/put_donation/donation_REST")
                .put(body)
                .build()
        // 3. 응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("",response.body!!.string())
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view4, container, false)
    }


}