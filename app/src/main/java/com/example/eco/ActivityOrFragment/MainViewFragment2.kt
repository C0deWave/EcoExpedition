package com.example.eco.ActivityOrFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eco.BulitinBoardItemAdapter
import com.example.eco.R
import com.example.eco.dataClass.BoardData
import com.example.eco.dataClass.GroupListData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


class MainViewFragment2 : Fragment() {

    var grouplist : MutableList<BoardData> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getGroupList()

        personal_recyclerView.adapter = BulitinBoardItemAdapter(grouplist)
        personal_recyclerView.layoutManager = LinearLayoutManager(activity);
    }

    private fun getGroupList() {

        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {

                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder()
                        .url("https://2km7a0qw6j.execute-api.ap-northeast-2.amazonaws.com/group_get_all/")
                        .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            //데이터 파싱해서 넣기
                            var da = response.body!!.string()
                            var da2 = da.substring(31, da.length-4)
                            Log.d("파싱데이터","${da2}")
                            val data = Gson().fromJson(da2, GroupListData::class.java)
                            data.forEach { groupListDataItem ->
                                grouplist.add(BoardData(group_name = groupListDataItem.group_name,
                                        master_name = groupListDataItem.master_name,
                                        open_date = groupListDataItem.open_date,
                                        intro = groupListDataItem.intro,
                                        group_pic = groupListDataItem?.group_pic,
                                        meeting_date = groupListDataItem.meeting_date,
                                        participant = groupListDataItem.participant.toString()
                                        ))
                            }
                            personal_recyclerView.adapter = BulitinBoardItemAdapter(grouplist)
                            personal_recyclerView.layoutManager = LinearLayoutManager(activity);
                        }
                    }
                })

            }.await()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view2, container, false)
    }

}