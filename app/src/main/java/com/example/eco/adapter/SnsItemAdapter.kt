package com.example.eco.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.eco.ActivityOrFragment.MainActivity
import com.example.eco.R
import com.example.eco.dataClass.SnsData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bulit_board_item.view.*
import kotlinx.android.synthetic.main.sns_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException

class SnsItemAdapter(var data: List<SnsData>, coroutineScope: FragmentActivity) : RecyclerView.Adapter<SnsItem>() {
    var coroutineScope = coroutineScope
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsItem {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.sns_item,parent,false)
        val coroutineScope = coroutineScope
        return SnsItem(view, coroutineScope!!)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SnsItem, position: Int) {
        holder.bindData(data[position])
    }
}

class SnsItem(itemView: View, coroutineScope: FragmentActivity) : RecyclerView.ViewHolder(itemView){
    val coroutineScope = coroutineScope

    fun bindData(snsData: SnsData) {

        //이미지 로딩
        try{
        Picasso.with(itemView.context)
                .load(Uri.parse(snsData.sns_pic))
                .into(itemView.imageView_snsItem)
        }catch (e : Exception){
            Log.d("SnsItemAdapter","${e}")
        }

        itemView.nameText_snsItem.text = snsData.sns_name
        itemView.intro_snsItem.text = snsData.sns_intro
        itemView.favoriteText_snsItem.text = snsData.thumsup
        if (snsData.isFaveriteClick == true) {
            itemView.faveriteBtn_snsItem.setImageResource(R.drawable.ic_baseline_favorite_red)
        }
        itemView.faveriteBtn_snsItem.setOnClickListener {
            if (snsData.isFaveriteClick == true){
                snsData.isFaveriteClick = false
                snsData.thumsup = (snsData.thumsup.toInt() - 1).toString()
                itemView.faveriteBtn_snsItem.setImageResource(R.drawable.ic_baseline_favorite)
                itemView.favoriteText_snsItem.text = snsData.thumsup
                updateFavorite(snsData)
                addfavList(snsData,coroutineScope)
            }else{
                snsData.isFaveriteClick = true
                snsData.thumsup = (snsData.thumsup.toInt() + 1).toString()
                itemView.favoriteText_snsItem.text = snsData.thumsup
                itemView.faveriteBtn_snsItem.setImageResource(R.drawable.ic_baseline_favorite_red)
                updateFavorite(snsData)
                addfavList(snsData,coroutineScope)
            }
        }
    }

    private fun addfavList(snsData: SnsData, coroutineScope: FragmentActivity) {
        val name = ((coroutineScope) as MainActivity).res!!.name
        val list = ((coroutineScope) as MainActivity).res!!.fav_list
        if (list != null) {
            var index = list.find { it:String -> it.toString() == "${snsData.sns_name}" }
            if (index == null){
                list.add("${snsData.sns_name}")
                Log.d("list++","${list}")
                Log.d("index","${index}")
            }else{
                list.remove("${snsData.sns_name}")
                Log.d("list--","${list}")
                Log.d("index","${index}")
            }
        }

        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val jsonArray = JSONArray(list)
            val data = "{\n" +
                    "    \"name\" : \"${name}\"," +
                    "    \"fav_list\" : ${jsonArray}" +
                    "}"
            Log.d("fav","${data}")
            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                    .url("https://wc4vc4o727.execute-api.ap-northeast-2.amazonaws.com/member_fav_update/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        try {
                            val data = response.body!!.string()

                        }catch (e : Exception){
                            Log.d("fragment4","${e.stackTrace}")
                        }
                    }
                }
            })

        }
    }


    private fun updateFavorite(snsData: SnsData) {
        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"sns_name\" : \"${snsData.sns_name}\"," +
                    "    \"thumsup\" : \"${snsData.thumsup}\"" +
                    "}"

            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                    .url("https://7a893q62wb.execute-api.ap-northeast-2.amazonaws.com/sns_thumsup/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        try {
                            val data = response.body!!.string()

                        }catch (e : Exception){
                            Log.d("fragment4","${e.stackTrace}")
                        }
                    }
                }
            })

        }
    }
}