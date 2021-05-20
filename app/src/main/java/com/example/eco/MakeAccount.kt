package com.example.eco

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_make_account.*

class MakeAccount : AppCompatActivity() {

    val GET_GALLERY_IMAGE = 200;    // 안드로이드에서 이미지를 가져오기 상태 표시 위한 전역 변수
    var selectImageUri: Uri? = null // 선택된 이미지의 Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_account)

        // 나이 선택 부분 스피너 어댑터
        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.old,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        // 뒤로가기 버튼을 눌렀을때
        backButton_makeAccount.setOnClickListener {
            val intent = Intent(applicationContext, banner::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        userImageView.setOnClickListener {
            // 이미지 레이아웃 버튼을 눌렀을때
            getImage()
        }

        signInButton_MakeAccount.setOnClickListener {
            if (CheckPassword()){

            }
        }

        passwordText2_MakeAccount.doAfterTextChanged {
            isRightPassword()
        }

        passwordText1_MakeAccount.doAfterTextChanged {
            isRightPassword()
        }
    }

    private fun isRightPassword() {
        if (passwordText1_MakeAccount.text.toString() == passwordText2_MakeAccount.text.toString()){
            if (passwordText1_MakeAccount.text.length > 8){
                guideText_MakeAccount.text = "비밀번호가 일치합니다."
                guideText_MakeAccount.setTextColor(Color.parseColor("#045FB4"))
            }else{
                guideText_MakeAccount.text = "비밀번호를 길게 해주세요."
                guideText_MakeAccount.setTextColor(Color.parseColor("#DF01D7"))
            }
        }else{
            guideText_MakeAccount.text = "비밀번호가 같지 않습니다."
            guideText_MakeAccount.setTextColor(Color.parseColor("#B40404"))
        }
    }

    private fun CheckPassword(): Boolean {

        if (passwordText1_MakeAccount.text.toString() != passwordText2_MakeAccount.text.toString()){
            Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (passwordText1_MakeAccount.text.isEmpty()){
            Toast.makeText(applicationContext, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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
            userImageView.setImageURI(selectImageUri)
        }
    }
}