package com.example.eco

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_make_account.*
import kotlinx.android.synthetic.main.activity_write_board_page.*

class WriteBoardPage : AppCompatActivity() {


    val GET_GALLERY_IMAGE = 200;    // 안드로이드에서 이미지를 가져오기 상태 표시 위한 전역 변수
    var selectImageUri: Uri? = null // 선택된 이미지의 Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board_page)

        selectPicture_writePicture.setOnClickListener {
            getImage()
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