package com.example.eco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eco.R
import com.example.eco.dataClass.SnsData
import kotlinx.android.synthetic.main.sns_item.view.*

class SnsItemAdapter (var data : List<SnsData>) : RecyclerView.Adapter<SnsItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsItem {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.sns_item,parent,false)
        return SnsItem(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SnsItem, position: Int) {
        holder.bindData(data[position])
    }
}

class SnsItem(itemView  : View) : RecyclerView.ViewHolder(itemView){
    fun bindData(snsData: SnsData) {
        itemView.nameText_snsItem.text = snsData.sns_name
        itemView.intro_snsItem.text = snsData.sns_intro
        itemView.favoriteText_snsItem.text = snsData.thumsup
    }
}