package com.example.eco

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail__bulit_in_board.*

class detail_BulitInBoard : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail__bulit_in_board)

        groupName_detailBoard.text = intent.getStringExtra("group_name")
        masterName_detailBoard.text = intent.getStringExtra("master_name")
        openDate_detailBoard.text = intent.getStringExtra("open_date")
        intro_detailBoard.text = intent.getStringExtra("intro")
        //이미지 생략 // 이거 맞지???

        meetingDate_detailBoard.text = intent.getStringArrayListExtra("meeting_date").toString()
        participant_detailBoard.text = intent.getStringArrayListExtra("participant").toString()
    // 그룹 아이
    //intent.getStringExtra("group_id")
    }
}