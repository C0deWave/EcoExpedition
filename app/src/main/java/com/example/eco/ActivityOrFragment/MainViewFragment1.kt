package com.example.eco.ActivityOrFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eco.GroupItemAdapter
import com.example.eco.R
import com.example.eco.dataClass.GroupListData
import com.example.eco.dataClass.GroupListDataItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view1.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


class MainViewFragment1 : Fragment() {
    var listArray:MutableList<GroupListDataItem> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getGroupList()

        floatingActionButton_fragment1.setOnClickListener {
            val intent = Intent(context, WriteBoardPageActivity::class.java)
            startActivity(intent)
        }


        //지역 검색 버튼을 눌렀을 때
        searchBtn_fragment1.setOnClickListener {
            val temp = listArray.filter { it: GroupListDataItem -> it.loc.startsWith("${searchText_fragment1.text}") }
            Log.d("","${listArray}")
            companyBoardRecyclerView_fragment1.adapter = GroupItemAdapter(temp as MutableList<GroupListDataItem>)
            companyBoardRecyclerView_fragment1.layoutManager = LinearLayoutManager(activity);
            }
        }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view1, container, false)
    }

    fun getGroupList() {
        val api = CoroutineScope(Dispatchers.Default).async {
            val client = OkHttpClient.Builder().build()
            val req = Request.Builder()
                         .url("https://2km7a0qw6j.execute-api.ap-northeast-2.amazonaws.com/group_get_all/")
                         .build()
            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            var da = response.body!!.string()
                            bindAdapter(da)
                        } catch (e: Exception) {
                            Log.d("err", "${e.stackTrace}")
                        }
                    }
                }
            })
        }
    }

    private fun bindAdapter(da: String) {
        var da2 = da.substring(31, da.length - 4)
        var data = Gson().fromJson(da2, GroupListData::class.java)
        for (datum in data) {
            listArray.add(datum)
        }
        companyBoardRecyclerView_fragment1.adapter = GroupItemAdapter(listArray)
        companyBoardRecyclerView_fragment1.layoutManager = LinearLayoutManager(activity);
    }
}