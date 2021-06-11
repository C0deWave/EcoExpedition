package com.example.eco.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eco.R
import com.example.eco.dataClass.UserInfo
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_info_item.view.*

class UserInfoAdapter (var data: MutableList<UserInfo>) : RecyclerView.Adapter<UserInfoItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoItem {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.user_info_item,parent,false)
        return UserInfoItem(view)
    }

    override fun onBindViewHolder(holder: UserInfoItem, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class UserInfoItem(itemView  : View) : RecyclerView.ViewHolder(itemView){
    fun bindData(userData: UserInfo) {

        itemView.userName_userInfoItem.text = userData.name

        //이미지 로딩
        Picasso.with(itemView.context)
                .load(Uri.parse(userData.pic))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(itemView.userImage_userInfoItem)
    }
}