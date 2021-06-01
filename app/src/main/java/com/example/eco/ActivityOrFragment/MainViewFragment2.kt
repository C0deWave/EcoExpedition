package com.example.eco.ActivityOrFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eco.R
import com.example.eco.adapter.SnsItemAdapter
import com.example.eco.dataClass.SnsData
import com.example.eco.dataClass.SnsListData
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.lang.Exception


class MainViewFragment2 : Fragment() {

    var grouplist : MutableList<SnsData> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSNSList()

        personal_recyclerView.adapter = SnsItemAdapter(grouplist, activity!!)

        personal_recyclerView.layoutManager = LinearLayoutManager(activity);
        writeBtn_fragment2.setOnClickListener {
            // 화면 변경을 구현하면 됩니다.
            val intent = Intent(context, WriteSnsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view2, container, false)
    }

    private fun getSNSList() {
        var data :SnsListData

        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {

                // 1. SNS 리스트를 호출합니다.
                val client = OkHttpClient.Builder().build()
                val req = Request.Builder()
                        .url("https://ojgtwivhjb.execute-api.ap-northeast-2.amazonaws.com/sns_get_all/")
                        .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { }
                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            //데이터 파싱해서 넣기
                            try {
                                var da = response.body!!.string()
                                var da2 = da.substring(31, da.length - 4)
                                Log.d("파싱데이터", "${da2}")
                                data = Gson().fromJson(da2, SnsListData::class.java)

                                for (datum in data) {
                                    var fav_list = (activity as MainActivity).res.fav_list
                                    var index = fav_list!!.find { it:String -> it == "${datum.sns_name}" }
                                    if (index != null){
                                        datum.isFaveriteClick = true
                                        Log.d("tag","tag")
                                    }
                                    grouplist.add(datum)
                                }
                                personal_recyclerView.adapter = SnsItemAdapter(data,activity!!)
                                personal_recyclerView.layoutManager = LinearLayoutManager(activity)

                            }catch (e : Exception){
                                Log.d("err","${e}")
                            }
                        }
                    }
                })

            }.await()
        }
    }

}