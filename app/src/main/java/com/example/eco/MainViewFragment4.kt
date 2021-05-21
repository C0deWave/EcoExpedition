package com.example.eco

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_view3.*
import kotlinx.android.synthetic.main.fragment_main_view4.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class MainViewFragment4 : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var email = loadFromInnerStorage("userInfoData.txt")
        Log.d("email" , "${email}")

        //로그아웃 기능 구현
        logoutBtn_fragment4.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        saveToInnerStorage("","userInfoData.txt")
        val intent = Intent(activity,banner::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view4, container, false)
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
}