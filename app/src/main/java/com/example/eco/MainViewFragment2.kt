package com.example.eco

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main_view2.*


class MainViewFragment2 : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var listArray:MutableList<BoardData> = mutableListOf(BoardData("그룹이름 : wnaud","주인이름",
                "개설일","소개글","사진", arrayListOf("1일","2일"),"그룹 키값", arrayListOf("참가자1","참가자2")))

        personal_recyclerView.adapter = BulitinBoardItemAdapter(listArray)
        personal_recyclerView.layoutManager = LinearLayoutManager(activity);
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