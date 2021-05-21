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
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view1, container, false)
    }
}