package com.example.eco.ActivityOrFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eco.BulitinBoardItemAdapter
import com.example.eco.R
import com.example.eco.dataClass.BoardData
import kotlinx.android.synthetic.main.fragment_main_view1.*


class MainViewFragment1 : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var listArray:MutableList<BoardData> = mutableListOf(BoardData("ss","aa",
                "2002-01-03","d","", arrayListOf("1","2일"),"ssaa", arrayListOf("참가자1","참가자2")))

        companyBoardRecyclerView_fragment1.adapter = BulitinBoardItemAdapter(listArray)
        companyBoardRecyclerView_fragment1.layoutManager = LinearLayoutManager(activity);

        floatingActionButton_fragment1.setOnClickListener {
            val intent = Intent(context, WriteBoardPageActivity::class.java)
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