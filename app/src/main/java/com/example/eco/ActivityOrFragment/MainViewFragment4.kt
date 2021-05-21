package com.example.eco.ActivityOrFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eco.R
import com.example.eco.dataClass.LoginInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view4.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class MainViewFragment4 : Fragment() {

    var pwd = ""
    var donation = ""
    var partcipantGroup = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var email = loadFromInnerStorage("userInfoData.txt")
        Log.d("email" , "${email}")
        if (email != null) {
            setData(email)
        }

        //로그아웃 기능 구현
        logoutBtn_fragment4.setOnClickListener {
            logout()
        }

        // 자신의 설정을 변경하는 페이지로 이동합니다.
        settingAccountBtn_fragment4.setOnClickListener {
            val intent = Intent(activity, SettingAccountActivity::class.java)
//            emailText_fragment4.text = res.email
//            ageText_fragment4.text = setAge(res.age)
            intent.putExtra("name",userNameText_fragment4.text)
            intent.putExtra("pwd",pwd)
            intent.putExtra("doantion",donation)
            intent.putExtra("participant",partcipantGroup)

            startActivity(intent)
        }
    }

    private fun logout() {
        saveToInnerStorage("","userInfoData.txt")
        val intent = Intent(activity, BannerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view4, container, false)
    }

    //로그인 기능을 구현합니다.
    fun setData(email: String){
        CoroutineScope(Dispatchers.Main).launch {

            val api = CoroutineScope(Dispatchers.Default).async {
                // 보낼 데이터 json으로 만들기
                val data = "{\n" +
                        "    \"email\" : \"${email}\"" +
                        "}"

                val media = "application/json; charset=utf-8".toMediaType();
                val body = data.toRequestBody(media)

                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder()
                        .url("https://p7s3gkde6f.execute-api.ap-northeast-2.amazonaws.com/member_get/")
                        .put(body)
                        .build()

                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            // 회원조회 응답
                            val data = response.body!!.string()
                            var rawData2 = data.substring(31,data.length-4)
                            val res = Gson().fromJson(rawData2 , LoginInfo::class.java)
                            emailText_fragment4.text = res.email
                            userNameText_fragment4.text = res.name
                            pwd = res.pswd
                            partcipantGroup = res.p_group
                            donation = res.d_amount
                            Log.d("pwd" , "${pwd}")
                            ageText_fragment4.text = setAge(res.age)
                            DonationPriceText_fragment4.text = res.d_amount
                            if (res.p_group == ""){
                                groupListText_fragment4.text = "아직 참가중인 모임이 없습니다.\n모임에 참여해 환경운동을 해보는건 어떨까요?"
                            }else{
                                groupListText_fragment4.text = res.p_group
                            }
                        }
                    }
                })

            }.await()
        }
    }

    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = (activity)?.openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream?.reader()?.readText()
    }

    fun saveToInnerStorage(text:String, filename:String){
        //내부저장소의 전달된 파일이름의 파일 출력스트림을 가져온다.
        //MODE_APPEND = 파일에 기존의 내용 이후에 붙이는 모드입니다.
        //MODE_PRIVATE 앱 전용으로 만들어 다른 앱에서는 접근 불가, 이미 파일이 있는 경우 기존 파일에 덮어씁니다.
        val fileOutputStream = (activity)?.openFileOutput(filename, Context.MODE_PRIVATE)
        //출력 스트림에 text를 바이트로 전환하여 write한다.
        fileOutputStream?.write(text.toByteArray())
        //파일 출력 스트림을 닫는다.
        fileOutputStream?.close()
    }

    private fun setAge(p2: String): String {
        when(p2){
             "5" -> return "10대"
            "15" -> return "10대"
            "25" -> return "20대"
            "35" -> return "30대"
            "45" -> return "40대"
            "55" -> return "50대"
            "65" -> return "60대"
            "75" -> return "70대"
            "85" -> return "80대"
            "95" -> return "90대"
            "105" -> return "100대"
        }
        return ""
    }
}