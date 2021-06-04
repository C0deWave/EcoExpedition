package com.example.eco.ActivityOrFragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.example.eco.ClassRes.URIPathHelper
import com.example.eco.R
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_write_sns.*
import kotlinx.android.synthetic.main.fragment_main_view1.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// SNS에서 글쓰기를 구현하는 부분입니다.
// 제목, 내용, 이미지 업로드
class WriteSnsActivity : AppCompatActivity() {

    val GET_GALLERY_IMAGE = 200;    // 안드로이드에서 이미지를 가져오기 상태 표시 위한 전역 변수
    var selectImageUri: Uri? = null // 선택된 이미지의 Uri

    var writer : String? = ""

    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val formatted = current.format(formatter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_sns)
        sns_init()

        // 이미지 버튼 클릭시
        selectPicture_writeSns.setOnClickListener {
            getImage()
        }

        // 모임 개설하기 버튼
        CreateSnsBtn_writeSns.setOnClickListener {
            val uriPathHelper = URIPathHelper()
            val filePath = selectImageUri?.let { it1 -> uriPathHelper?.getPath(this, it1) }
            Toast.makeText(this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show()
            uploadWithTransferUtility(fileName = "${writer}${formatted}.jpg", file = File(filePath))
            makeSns()
        }
    }

    private fun sns_init() {
        writer = loadFromInnerStorage("userInfoData.txt")
    }

    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream?.reader()?.readText()
    }


    private fun getImage() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        startActivityForResult(intent, GET_GALLERY_IMAGE)
    }

    private fun makeSns() {
        val api = CoroutineScope(Dispatchers.Default).async {
            //데이터 꺼네오기

            // 보낼 데이터 json으로 만들기
            val data = "{" +
                    "\"sns_name\" : \"${writer}${formatted}\"," +
                    "\"sns_pic\" : \"${writer}${formatted}\"," +
                    "\"sns_intro\" : \"${snsIntro_WriteSns.text}\"," +
                    "\"name\" : \"${writer}\"" +
                    "}"
            Log.d("sns 만들기" , "${data}")
            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                .url("https://b7i3wqaxpj.execute-api.ap-northeast-2.amazonaws.com/sns_insert/")
                .put(body)
                .build()
            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, "후기 작성 완료", Toast.LENGTH_SHORT).show()
                        Log.d("후기생성 완료","${response.body!!.string()}")
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            //이미지 뷰를 해당 이미지로 교환합니다.
            selectImageUri = data.data
            selectPicture_writeSns.setImageURI(selectImageUri)
        }
    }

    fun uploadWithTransferUtility(fileName: String, file: File) {

        val credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,
            "ap-northeast-2:8a026960-a1e3-4c06-93b4-22bd8f9eeb97", // 자격 증명 풀 ID
            Regions.AP_NORTHEAST_2 // 리전
        )

        TransferNetworkLossHandler.getInstance(applicationContext)

        val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .defaultBucket("imagehackerton") // 디폴트 버킷 이름.
            .s3Client(AmazonS3Client(credentialsProvider, Region.getRegion(Regions.AP_NORTHEAST_2)))
            .build()
        /* Store the new created Image file path */
        val uploadObserver : TransferObserver
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            tedPermission()
            uploadObserver = transferUtility.upload("imagehackerton/${fileName}", file, CannedAccessControlList.PublicRead)
            return
        }else {
            uploadObserver = transferUtility.upload("imagehackerton/${fileName}", file, CannedAccessControlList.PublicRead)
        }
        // Attach a listener to the observer
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    // Handle a completed upload
                }
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
                val done = (((current.toDouble() / total) * 100.0).toInt())
                Log.d("MYTAG", "UPLOAD - - ID: $id, percent done = $done")
            }

            override fun onError(id: Int, ex: Exception) {
                Log.d("MYTAG", "UPLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
            }
        })

        // If you prefer to long-poll for updates
        if (uploadObserver.state == TransferState.COMPLETED) {
            /* Handle completion */
        }
    }

    // 저장 권한을 요청하는 함수입니다.
    private fun tedPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Toast.makeText(applicationContext, "설정에서 권한을 허가 해주세요.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("서비스 사용을 위해서 몇가지 권한이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 설정할 수 있습니다.")
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }
}