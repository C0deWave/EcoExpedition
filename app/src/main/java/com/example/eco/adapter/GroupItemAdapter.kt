package com.example.eco

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eco.ActivityOrFragment.DetailBulitInBoardActivity
import com.example.eco.dataClass.GroupListDataItem
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bulit_board_item.view.*

class GroupItemAdapter(var data: MutableList<GroupListDataItem>) : RecyclerView.Adapter<BoardItem>() {
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
    fun bindData(boardData: GroupListDataItem) {
        itemView.titleText_BoardItem.text = boardData.group_name
        itemView.loc_boardItem.text = boardData.loc

        //이미지 로딩
        Picasso.with(itemView.context)
                .load(Uri.parse(boardData.group_pic))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(itemView.itemImage)

        itemView.builtInBoard_item.setOnClickListener {
            val intent = Intent(itemView.context, DetailBulitInBoardActivity::class.java)
            intent.putExtra("group_name",boardData.group_name)
            intent.putExtra("master_name",boardData.master_name)
            intent.putExtra("open_date",boardData.open_date)
            intent.putExtra("intro",boardData.intro)
            intent.putExtra("group_pic",boardData.group_pic)
            intent.putExtra("meeting_date",boardData.meeting_date)
            intent.putExtra("meeting_type",boardData.meeting_type)
            intent.putExtra("meeting_intro",boardData.meeting_intro)
            intent.putStringArrayListExtra("participant",boardData.participant)
            intent.putExtra("dona",boardData.dona)
            intent.putExtra("dona_all",boardData.dona_all)
            intent.putExtra("loc",boardData.loc)

            itemView.context.startActivity(intent)
        }
    }
}
