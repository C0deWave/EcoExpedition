package com.example.eco

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_make_account.*
import kotlinx.android.synthetic.main.activity_write_board_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class WriteBoardPage : AppCompatActivity() {


    val GET_GALLERY_IMAGE = 200;    // 안드로이드에서 이미지를 가져오기 상태 표시 위한 전역 변수
    var selectImageUri: Uri? = null // 선택된 이미지의 Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board_page)

        // 모임 개설하기 버튼
        makeGroupBtn_writeboard.setOnClickListener {
            makeGroup()
        }

        // 이미지 선택하기 버튼
        selectPicture_writePicture.setOnClickListener {
            getImage()
        }
    }


    private fun makeGroup() {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {
                //데이터 꺼네오기

                // 보낼 데이터 json으로 만들기
                val data = "{" +
                        "\"group_name\" : \"${groupNameText_writeBoard.text}\"," +
                        "\"master_name\" : \"${masterName_writeBoard.text}\"," +
                        "\"open_date\" : \"2002-01-03\"," +
                        "\"intro\" : \"${introText_writeBoard.text}\"," +
                        "\"group_pic\" : \"\"," +
                        "\"meeting_date\" : \"123\"," +
                        "\"participant\" : \"aa,vv,cc\"," +
                        "\"group_id\" : \"${groupNameText_writeBoard.text.toString() + masterName_writeBoard.text}\"" +
                        "}"

                val media = "application/json; charset=utf-8".toMediaType();
                val body = data.toRequestBody(media)

                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder()
                        .url("https://lw9jec7zmb.execute-api.ap-northeast-2.amazonaws.com/group_insert")
                        .post(body)
                        .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(applicationContext, "그룹 생성 완료", Toast.LENGTH_SHORT).show()
                            Log.d("그룹명","${groupNameText_writeBoard.text.toString() + masterName_writeBoard.text}")
                            Log.d("반환값","${response.body!!.string()}")
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })

            }.await()
        }

    }

    private fun getImage() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        startActivityForResult(intent, GET_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            //이미지 뷰를 해당 이미지로 교환합니다.
            selectImageUri = data.data!!
            selectPicture_writePicture.setImageURI(selectImageUri)
        }
    }
}