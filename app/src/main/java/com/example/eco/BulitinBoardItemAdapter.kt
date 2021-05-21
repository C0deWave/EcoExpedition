package com.example.eco

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bulit_board_item.view.*

class BulitinBoardItemAdapter(var data : List<BoardData>) : RecyclerView.Adapter<BoardItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardItem {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.bulit_board_item,parent,false)
        return BoardItem(view)
    }

    override fun onBindViewHolder(holder: BoardItem, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class BoardItem(itemView  : View) : RecyclerView.ViewHolder(itemView){
    fun bindData(boardData: BoardData) {
        itemView.titleText_BoardItem.text = boardData.group_name


        itemView.builtInBoard_item.setOnClickListener {
            val intent = Intent(itemView.context, detail_BulitInBoard::class.java)
            intent.putExtra("group_name",boardData.group_name)
            intent.putExtra("master_name",boardData.master_name)
            intent.putExtra("open_date",boardData.open_date)
            intent.putExtra("intro",boardData.intro)
            intent.putExtra("group_pic",boardData.group_pic)
            intent.putStringArrayListExtra("meeting_date",boardData.meeting_date)
            intent.putStringArrayListExtra("participant",boardData.participant)
            intent.putExtra("group_id",boardData.group_id)
            itemView.context.startActivity(intent)
        }
    }
}
