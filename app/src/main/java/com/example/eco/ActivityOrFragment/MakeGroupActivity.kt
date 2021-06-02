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
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail__bulit_in_board.*
import kotlinx.android.synthetic.main.activity_write_board_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MakeGroupActivity : AppCompatActivity() {


    val GET_GALLERY_IMAGE = 200;    // 안드로이드에서 이미지를 가져오기 상태 표시 위한 전역 변수
    var selectImageUri: Uri? = null // 선택된 이미지의 Uri

    var name = ""
    var group_name = ""
    var master_name = ""
    var open_date = ""
    var intro = ""
    var group_pic = ""
    var meeting_date = ""
    var meeting_type = ""
    var meeting_intro = ""
    var participant : java.util.ArrayList<String>? = null
    var dona = ""
    var dona_all = ""
    var loc = ""

    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ISO_DATE
    val formatted = current.format(formatter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board_page)
        initData()

        Log.d("name","${name}")
        Log.d("오늘 날짜","${formatted}")

        // 모임 개설하기 버튼
        makeGroupBtn_writeboard.setOnClickListener {
            val uriPathHelper = URIPathHelper()
            val filePath = selectImageUri?.let { it1 -> uriPathHelper?.getPath(this, it1) }
            Toast.makeText(this, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show()
            uploadWithTransferUtility(fileName = "${groupNameText_writeBoard.text}.jpg", file = File(filePath))
            uploadImage()
        }

        // 이미지 선택하기 버튼
        selectPicture_writePicture.setOnClickListener {
            getImage()
        }

    }

    private fun initData() {
        name = loadFromInnerStorage("userInfoData.txt")!!
        var key = "false"
        key = intent.getStringExtra("Key").toString()
        if (key == "true"){
            group_name = intent.getStringExtra("group_name").toString()
            Log.d("모임수정","${group_name}")
            master_name = intent.getStringExtra("master_name").toString()
            open_date = intent.getStringExtra("open_date").toString()
            intro = intent.getStringExtra("intro").toString()
            group_pic = intent.getStringExtra("group_pic").toString()
            meeting_date = intent.getStringExtra("meeting_date").toString()
            meeting_type = intent.getStringExtra("meeting_type").toString()
            meeting_intro = intent.getStringExtra("meeting_intro").toString()
            participant = intent.getStringArrayListExtra("participant")
            dona = intent.getStringExtra("dona").toString()
            dona_all = intent.getStringExtra("dona_all").toString()
            loc = intent.getStringExtra("loc").toString()

            groupNameText_writeBoard.setText(group_name)
            locText_writeBoard.setText(loc)
            introText_writeBoard.setText(intro)

            groupNameText_writeBoard.isEnabled = false
            locText_writeBoard.isEnabled = false

            // 참가자 구현
            var pList = ""
            participant?.forEach {
                pList += "${it}\n"
            }
            makeGroupBtn_writeboard.text = "모임수정하기"
            //이미지 로딩
            Picasso.with(this).invalidate(Uri.parse(group_pic))
            Picasso.with(this)
                    .load(Uri.parse(group_pic))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(selectPicture_writePicture)
        }
    }


    private fun makeGroup() {
        val api = CoroutineScope(Dispatchers.Default).async {
            //데이터 꺼네오기

            // 보낼 데이터 json으로 만들기
            val data = "{" +
                    "\"group_name\" : \"${groupNameText_writeBoard.text}\"," +
                    "\"master_name\" : \"${name}\"," +
                    "\"open_date\" : \"${formatted}\"," +
                    "\"intro\" : \"${introText_writeBoard.text}\"," +
                    "\"loc\" : \"${locText_writeBoard.text}\"" +
                    "}"
            Log.d("그룹 만들기" , "${data}")
            Log.d("Json","$data")
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
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, "그룹 생성 완료", Toast.LENGTH_SHORT).show()
                        Log.d("그룹생성 완료","${response.body!!.string()}")
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
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


    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream?.reader()?.readText()
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

    private fun uploadImage() {
        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"group_name\" : \"${groupNameText_writeBoard.text}\"," +
                    "    \"group_pic\" : \"${groupNameText_writeBoard.text}\"" +
                    "}"
            Log.d("이미지 업로드","${data}")
            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                    .url("https://ibgz3o3y7h.execute-api.ap-northeast-2.amazonaws.com/group_image_update/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        Log.d("업로드 성공", "${response.body!!.string()}")

                        if (makeGroupBtn_writeboard.text == "모임 개설하기") {
                            makeGroup()
                        }else{
                            ReWriteGroup()
                        }
                    }
                }
            })
        }
    }

    private fun ReWriteGroup() {
        val api = CoroutineScope(Dispatchers.Default).async {
            //데이터 꺼네오기

            // 보낼 데이터 json으로 만들기
            val data = "{" +
                    "\"group_name\" : \"${groupNameText_writeBoard.text}\"," +
                    "\"master_name\" : \"${name}\"," +
                    "\"intro\" : \"${introText_writeBoard.text}\"" +
                    "}"
            Log.d("그룹 만들기" , "${data}")
            Log.d("Json","$data")
            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                    .url("https://5edscp9h75.execute-api.ap-northeast-2.amazonaws.com/group_update")
                    .put(body)
                    .build()
            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, "그룹 생성 완료", Toast.LENGTH_SHORT).show()
                        Log.d("그룹생성 완료","${response.body!!.string()}")
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        }
    }
}