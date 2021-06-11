package com.example.eco.ActivityOrFragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.example.eco.R
import com.example.eco.dataClass.Donation
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Response
import java.io.IOException
import java.lang.Exception


class MainViewFragment3 : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // API 네트워크 통신을 위한 예제
        coroutine()


        //시바견 춤추기 기능
//        // 아래 카운트 만큼 춤을 춥니다.
//        var danceCount = 7
        Glide.with(this)
            .load(R.drawable.jjj)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is GifDrawable) {
//                        (resource as GifDrawable).setLoopCount(danceCount)
                    }
                    return false
                }
            })
            .into(sibaImageView_fragment3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view3, container, false)
    }

    // API 네트워크 통신을 위한 예제
    private fun coroutine() {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {
                //네트워크
                getHtmlStr()
            }.await()
        }
    }

    // API 네트워크 통신을 위한 예제
    private fun getHtmlStr(){
        // 1. 클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        // 2. 요청
        val req = Request.Builder().url("https://gula4qefm1.execute-api.ap-northeast-2.amazonaws.com/20210519/donation_get")
                .build()
        // 3. 응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {
                    try{
                        val res = response.body!!.string()
                        var rawData2 = res.substring(1, res.length - 1)
                        val data = Gson().fromJson(rawData2, Donation::class.java)
                        donationPriceTextView_fragment3.text = "₩" + deleteDecimal(data.total_amount)
                        donationCountTextView_fragment3.text = "지금까지 "+deleteDecimal(data.num_of_times) + "개의 모임이"
                    }catch (e:Exception){
                        Log.d("fragment","${e.stackTrace}")
                    }
                }
            }
        })
    }

    private fun deleteDecimal(string: String): String {
        return string.substring(9,string.length-2)
    }
}
