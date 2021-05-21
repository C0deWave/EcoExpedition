package com.example.eco

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view1.*
import kotlinx.android.synthetic.main.fragment_main_view2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Response
import java.io.IOException


class MainViewFragment1 : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var listArray:MutableList<BoardData> = mutableListOf(BoardData("그룹이름 : wnaud","주인이름",
                "개설일","소개글","사진", arrayListOf("1일","2일"),"그룹 키값", arrayListOf("참가자1","참가자2")))

        companyBoardRecyclerView_fragment1.adapter = BulitinBoardItemAdapter(listArray)
        companyBoardRecyclerView_fragment1.layoutManager = LinearLayoutManager(activity);

        floatingActionButton_fragment1.setOnClickListener {
            val intent = Intent(context, WriteBoardPage::class.java)
            startActivity(intent)
        }
        // API 네트워크 통신을 위한 예제
//        coroutine()
    }

    // API 네트워크 통신을 위한 예제
//    private fun coroutine() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val api = CoroutineScope(Dispatchers.Default).async {
//                //네트워크
//                getHtmlStr()
//            }.await()
//        }
//    }

//    // API 네트워크 통신을 위한 예제
//    private fun getHtmlStr(){
//        // 1. 클라이언트 만들기
//        val client = OkHttpClient.Builder().build()
//        // 2. 요청
//        val req = Request.Builder().url("http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey=gGhynriCFrtswJiVVWDI9T3Q%2B1Q%2FftEzDkISmGi7jWjL0knS%2BXWeI2MIOzWNxxvycoI%2FlDd1%2BDmGp7Az4FZeZA%3D%3D&returnType=json&tmX=244148.546388&tmY=412423.75772&ver=1.0")
//                .build()
//        // 3. 응답
//        client.newCall(req).enqueue(object : Callback{
//            override fun onFailure(call: Call, e: IOException) {
//
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    val data = Gson().fromJson(response.body!!.string() , ObserveCenterData::class.java)
//                    testText.text = data.response.body.items[0].addr
//                }
//            }
//        })
//    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view1, container, false)
    }
}