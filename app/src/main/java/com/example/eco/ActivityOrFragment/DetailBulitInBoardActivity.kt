package com.example.eco.ActivityOrFragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.eco.R
import com.example.eco.dataClass.DustInfo
import com.example.eco.dataClass.LoginInfo
import com.example.eco.dataClass.ObserveCenterData
import com.example.eco.dataClass.TmLocation
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_detail__bulit_in_board.*
import kotlinx.android.synthetic.main.activity_make_account.*
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
import okhttp3.Response
import java.io.IOException

class DetailBulitInBoardActivity : AppCompatActivity() {
    var url2 = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=gGhynriCFrtswJiVVWDI9T3Q%2B1Q%2FftEzDkISmGi7jWjL0knS%2BXWeI2MIOzWNxxvycoI%2FlDd1%2BDmGp7Az4FZeZA%3D%3D&returnType=json&numOfRows=100&pageNo=1&stationName="
    var url3 = "&dataTerm=DAILY&ver=1.0"
    var locationListener : LocationListener? = null
    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStop() {
        super.onStop()
        // 리스너를 제거해야 합니다.
        locationListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail__bulit_in_board)

        groupName_detailBoard.text = intent.getStringExtra("group_name")
        masterName_detailBoard.text = intent.getStringExtra("master_name")
        openDate_detailBoard.text = intent.getStringExtra("open_date")
        intro_detailBoard.text = intent.getStringExtra("intro")

        meetingDate_detailBoard.text = intent.getStringArrayListExtra("meeting_date").toString()
        participant_detailBoard.text = intent.getStringArrayListExtra("participant").toString()

        participantbtn_detailBoard.setOnClickListener {
            addPartcipant()
        }

        //위도 경도 값이 퍼미션이 허가되지 않았을 때
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            tedPermission()
            getTmLocation()
            return
        }else{
            getTmLocation()
        }
    }

    private fun addPartcipant() {
        //1. 내 참가 그룹에 그룹명을 추가합니다.
        //2. 그룹 리스트에 내 이름을 추가합니다.
        addGroupInfo()
    }

    private fun addGroupInfo() {
        CoroutineScope(Dispatchers.Main).launch {

            val api = CoroutineScope(Dispatchers.Default).async {
                // 보낼 데이터 json으로 만들기
                val data = "{\n" +
                        "    \"email\" : \"${loadFromInnerStorage("userInfoData.txt")}\"" +
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
                            updateGroupData(res)
                        }
                    }
                })

            }.await()
        }
    }

    private fun updateGroupData(res: LoginInfo) {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {

                val pgroup = res.p_group + ","+ groupName_detailBoard.text
                // 보낼 데이터 json으로 만들기
                val data = "{\n" +
                        "    \"email\" : \"${res.email}\",\n" +
                        "    \"name\" : \"${res.name}\",\n" +
                        "    \"pswd\" : \"${res.pswd}\",\n" +
                        "    \"age\" : \"${res.age}\",\n" +
                        "    \"d_amount\" : \"0\",\n" +
                        "    \"pic\" : \"\",\n" +
                        "    \"p_group\" : \"${pgroup}\"\n" +
                        "}"

                val media = "application/json; charset=utf-8".toMediaType();
                val body = data.toRequestBody(media)

                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder()
                        .url("https://3zz6r8wnma.execute-api.ap-northeast-2.amazonaws.com/member_insert")
                        .post(body)
                        .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { }
                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(applicationContext, "그룹가입성공", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }.await()
        }
    }


    private fun getTmLocation() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                location?.let {
                    val position = LatLng(it.latitude, it.longitude)
                    var url = "https://dapi.kakao.com/v2/local/geo/transcoord.json?"+"x=${position.longitude}&y=${position.latitude}&input_coord=WGS84&output_coord=TM"
                    Log.d("주소","${url}")
                    getTM(url)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            tedPermission()
            return
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000,
                1f,
            locationListener as LocationListener
        )
    }

    private fun getTM(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {
                //네트워크
                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder()
                    .url(url)
                    .addHeader("Authorization","KakaoAK 01294cd7d666c0e342e1ca5a5b7f9d0e")
                    .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { }
                    override fun onResponse(call: Call, response: Response) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val data = Gson().fromJson(response.body!!.string() , TmLocation::class.java)
                            //기본 위도 경도 값을 가져옵니다.
                            Log.d("좌표값","${data.documents[0].x}  ${data.documents[0].y}")
                            val api2 = CoroutineScope(Dispatchers.Default).async {
                                //네트워크
                                // 받아온 값을 기준으로 측정소와 먼지 데이터를 구합니다.
                                // 미세먼지의 상태를 알려주는 기능 구현
                                getObserveCenter( data.documents[0].x, data.documents[0].y)
                            }.await()
                        }
                    }
                })
            }.await()
        }
    }

    // API 네트워크 통신을 위한 예제
    private fun getObserveCenter(x: Double, y: Double) {
        CoroutineScope(Dispatchers.Main).launch {
            val api = CoroutineScope(Dispatchers.Default).async {
                //네트워크
                var firsturl1 = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey=gGhynriCFrtswJiVVWDI9T3Q%2B1Q%2FftEzDkISmGi7jWjL0knS%2BXWeI2MIOzWNxxvycoI%2FlDd1%2BDmGp7Az4FZeZA%3D%3D&returnType=json&"
                var firsturl2 = "tmX=${x}&tmY=${y}"

                Log.d("측정소url",firsturl1+firsturl2)
                // 1. 클라이언트 만들기
                val client = OkHttpClient.Builder().build()
                // 2. 요청
                val req = Request.Builder().url(firsturl1 + firsturl2)
                    .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { }
                    override fun onResponse(call: Call, response: Response) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val data = Gson().fromJson(response.body!!.string() , ObserveCenterData::class.java)
                            // 측정소 이름을 가져옵니다.
                            // dustTextView_detaailBuiltin.text = data.response.body.items[0].stationName
                            //측정소 이름을 기준으로 미세먼지 농도를 측정합니다.
                            val ENCODED_HREF = java.net.URLEncoder.encode(data.response.body.items[0].stationName, "utf-8")
                            observeCenterTextView_detailBoard.text = data.response.body.items[0].stationName
                            url2 = url2 + ENCODED_HREF + url3
                            val api2 = CoroutineScope(Dispatchers.Default).async {
                                //네트워크
                                getDust()
                            }.await()
                        }
                    }
                })
            }.await()
        }
    }

    // 알아낸 측정소를 기준으로 해서 측정소의 미세먼지 값을 호출합니다.
    private fun getDust() {
        // 1. 클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        // 2. 요청
        val req = Request.Builder().url(url2)
                .build()
        // 3. 응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                CoroutineScope(Dispatchers.Main).launch {
                    val data = Gson().fromJson(response.body!!.string() , DustInfo::class.java)
                    dustTextView_detaailBuiltin.text = dustGrade(data.response.body.items[0].pm10Grade)
                }
            }
        })
    }

    // 위치 권한을 요청하는 함수입니다.
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
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check()
    }

    // 미세먼지 단계에 따른 단계 반환
    private fun dustGrade(pm10Grade: String): CharSequence? {
        when (pm10Grade) {
            "1" -> return "좋음"
            "2" -> return "보통"
            "3" -> return "나쁨"
            "4" -> return "매우나쁨"
            else -> {
                return "데이터 없음"
            }
        }
    }

    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        //내부 저장소의 전달된 이름의 파일입력 스트림을 가져온다.
        val fileInputStream = openFileInput(filename)
        //파일의 저장된 내용을 읽어 String형태로 가져온다.
        return fileInputStream?.reader()?.readText()
    }
}