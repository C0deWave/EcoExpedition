package com.example.eco.ActivityOrFragment

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eco.GroupItemAdapter
import com.example.eco.R
import com.example.eco.adapter.UserInfoAdapter
import com.example.eco.dataClass.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail__bulit_in_board.*
import kotlinx.android.synthetic.main.fragment_main_view1.*
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
import java.util.*


class DetailBulitInBoardActivity : AppCompatActivity() {

    var locationListener : LocationListener? = null

    var name : String? = ""
    var group_name = ""
    var master_name = ""
    var open_date = ""
    var intro = ""
    var group_pic = ""
    var meeting_date = ""
    var meeting_type = ""
    var meeting_intro = ""
    var participant : ArrayList<String>? = null
    var dona = ""
    var dona_all = ""
    var loc = ""
    var pListInfo : MutableList<UserInfo> = mutableListOf()
    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStop() {
        super.onStop()
        locationListener = null
    }

    // 1. 현재 위치를 가져옵니다.
    // 2. 현재 위치를 TM좌표로 바꾸는 api를 호출합니다.
    // 3. TM 좌표를 기준으로 주변의 측정소의 위치를 알아냅니다.
    // 4. 측정소 이름을 기준으로 미세먼지 농도를 제공합니다.
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("name", "${name}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail__bulit_in_board)

        // 초기 설정을 합니다.
        datainit()

        //권한에 따라 레이아웃을 수정합니다.
        settingLayout()

        // 미팅만들기 버튼
        meetingMakeBtn_detailBoard.setOnClickListener {
            showDialog()
        }

        //후원기능 추가하기
        donateBtn_detailBoard.setOnClickListener {
            if (donaText_detailBoard.text != "0원"){
                groupDonation()
            }else{
                Toast.makeText(applicationContext, "후원할 돈이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //그룹 수정버튼
        rewriteBtn_detailBoard.setOnClickListener {
            goRewritePage()
        }

        //참가하기 버튼을 눌렀을때
       participantbtn_detailBoard.setOnClickListener {
           if (participantbtn_detailBoard.text == "그룹가입") {
               addPartcipant()
           }else if (participantbtn_detailBoard.text == "그룹탈퇴"){
               deleteParticipant()
           }else{
               deleteGroup()
           }
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

    private fun groupDonation() {
        // 보낼 데이터 json으로 만들기
        val data = "{ " +
                "    \"group_name\" : \"${group_name}\"," +
                "    \"dona\" : ${dona}," +
                "    \"dona_all\" : ${dona_all}" +
                "}"
        Log.d("json", "$data")
        val media = "application/json; charset=utf-8".toMediaType();
        val body = data.toRequestBody(media)

        // 1. 클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        // 2. 요청
        val req = Request.Builder()
                .url("https://l1tm80coq1.execute-api.ap-northeast-2.amazonaws.com/put_donation/donation_REST")
                .put(body)
                .build()

        // 3. 응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { }
            override fun onResponse(call: Call, response: Response) {
                // 응답이 오면 메인스레드에서 처리를 진행한다.
                CoroutineScope(Dispatchers.Main).launch {
                    // 회원조회 응답
                    try {
                        val data = response.body!!.string()
                        Log.d("기부완료", data)
                        donaAllText_detailBoard.text = "지금까지 우리가 후원한 금액은 "+(dona.toInt() + dona_all.toInt()).toString() + "원 이에요"
                        donaText_detailBoard.text = "앞으로 우리가 후원할 금액은 0원 이에요"
                        dona = "0"
                    } catch (e: Exception) {
                        Log.d("fragment4", "${e.stackTrace}")
                    }
                }
            }
        })
    }


    private fun deleteGroup() {
        val api2 = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"name\" : \"${name}\"," +
                    "    \"group_name\" : \"${group_name}\"" +
                    "}"
            Log.d("json", data)
            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                    .url("https://f8nl26nr58.execute-api.ap-northeast-2.amazonaws.com/when_member_out_group/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        try {
                            val data = response.body!!.string()
                            Log.d("탈퇴하기", data)
                        } catch (e: Exception) {
                            Log.d("fragment4", "${e.stackTrace}")
                        }
                    }
                }
            })
        }

        val api = CoroutineScope(Dispatchers.Default).async {
        // 보낼 데이터 json으로 만들기
        val data = "{ " +
                "    \"group_name\" : \"${group_name}\"" +
                "}"
        Log.d("json", data)
        val media = "application/json; charset=utf-8".toMediaType();
        val body = data.toRequestBody(media)

        // 1. 클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        // 2. 요청
        val req = Request.Builder()
                .url("https://7c22da6q54.execute-api.ap-northeast-2.amazonaws.com/group_delete")
                .put(body)
                .build()

        // 3. 응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { }
            override fun onResponse(call: Call, response: Response) {
                // 응답이 오면 메인스레드에서 처리를 진행한다.
                CoroutineScope(Dispatchers.Main).launch {
                    // 회원조회 응답
                    try {
                        val data = response.body!!.string()
                        Log.d("그룹해체", data)
                        goMain()
                    } catch (e: Exception) {
                        Log.d("fragment4", "${e.stackTrace}")
                    }
                }
            }
        })
    }
    }

    private fun deleteParticipant() {
        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"name\" : \"${name}\"," +
                    "    \"group_name\" : \"${group_name}\"" +
                    "}"
            Log.d("json", data)
            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                    .url("https://f8nl26nr58.execute-api.ap-northeast-2.amazonaws.com/when_member_out_group/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        try {
                            val data = response.body!!.string()
                            Log.d("탈퇴하기", data)
                            goMain()
                        } catch (e: Exception) {
                            Log.d("fragment4", "${e.stackTrace}")
                        }
                    }
                }
            })
        }
    }

    private fun goRewritePage() {
        val intent = Intent(this, MakeGroupActivity::class.java)
        intent.putExtra("Key","true")
        intent.putExtra("group_name", group_name)
        intent.putExtra("master_name",master_name)
        intent.putExtra("open_date",open_date)
        intent.putExtra("intro",intro)
        intent.putExtra("group_pic",group_pic)
        intent.putExtra("meeting_date",meeting_date)
        intent.putExtra("meeting_type",meeting_type)
        intent.putExtra("meeting_intro",meeting_intro)
        intent.putStringArrayListExtra("participant",participant)
        intent.putExtra("dona",dona)
        intent.putExtra("dona_all",dona_all)
        intent.putExtra("loc",loc)
        startActivity(intent)
    }

    private fun settingLayout() {
        if (name == master_name){
            // 그룹장
            return
        }else if(participant?.find{ it == name } == null){
            //외부인
            (donateBtn_detailBoard.parent as ViewGroup).removeView(donateBtn_detailBoard)
            (meetingMakeBtn_detailBoard.parent as ViewGroup).removeView(meetingMakeBtn_detailBoard)
            (rewriteBtn_detailBoard.parent as ViewGroup).removeView(rewriteBtn_detailBoard)
            participantbtn_detailBoard.text = "그룹가입"
        }else{
            //그룹원
            (donateBtn_detailBoard.parent as ViewGroup).removeView(donateBtn_detailBoard)
            (meetingMakeBtn_detailBoard.parent as ViewGroup).removeView(meetingMakeBtn_detailBoard)
            (rewriteBtn_detailBoard.parent as ViewGroup).removeView(rewriteBtn_detailBoard)
            participantbtn_detailBoard.text = "그룹탈퇴"
        }
    }

    fun showDialog() {
        val oItems = arrayOf("온라인", "오프라인")

        var oDialog = AlertDialog.Builder(this);

        oDialog.setTitle("미팅 타입을 지정해 주세요.")
        oDialog.setSingleChoiceItems(oItems, 0, DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
            Log.d("미팅 타입", "${oItems[i]}")
            meeting_type = oItems[i]
        }
        )
        oDialog.setPositiveButton("선택", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
            showDatePicker()
        }
        )
        oDialog.setCancelable(true)
        oDialog.show();
    }

    private fun showDatePicker() {
        var c = Calendar.getInstance();
        var nYear = c.get(Calendar.YEAR);
        var nMon = c.get(Calendar.MONTH);
        var nDay = c.get(Calendar.DAY_OF_MONTH);

        Toast.makeText(this, "미팅 일자를 선택해 주세요.", Toast.LENGTH_SHORT).show();

        var mDateSetListener = DatePickerDialog.OnDateSetListener() { datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
                var strDate = "";
                strDate += (i1+1).toString()+"월 "
                strDate += (i2).toString()+"일 "

                Log.d("미팅 일자", "$strDate")
                meeting_date = strDate
                showTimePicker()
        };

        var oDialog = DatePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mDateSetListener, nYear, nMon, nDay);
        oDialog.show();
    }

    private fun showTimePicker() {
        var mTimeSetListener = TimePickerDialog.OnTimeSetListener() { timePicker: TimePicker, i: Int, i1: Int ->
            Log.d("시간", "$i:$i1")
            meeting_date += "${i}:${i1}"
            Log.d("시간 추가", meeting_date)
            showContextDialog()
        }
        Toast.makeText(this, "미팅 시간을 정해주세요.", Toast.LENGTH_SHORT).show()
        var oDialog = TimePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog, mTimeSetListener, 0, 0, false);
        oDialog.show();
    }

    private fun showContextDialog() {

        var view = EditText(this)
        var oDialog = AlertDialog.Builder(this);

        oDialog.setTitle("미팅 내용을 입력해주세요.")
        oDialog.setView(view)
        oDialog.setCancelable(true)
        oDialog.setPositiveButton("확인", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
            Log.d("text", "${view.text}")
            meeting_intro = view.text.toString()
            //최종 확인하기
            showCheckDialog()
        }
        )
        oDialog.show();
    }

    private fun showCheckDialog() {

        var oDialog = AlertDialog.Builder(this);

        oDialog.setTitle("해당 일정으로 하시겠습니까?")
        oDialog.setCancelable(true)
        oDialog.setPositiveButton("확인", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
            Log.d("", "일정 업데이트 하기")
            makeMeeting()
        })
        oDialog.setNegativeButton("취소", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(this, "취소했습니다.", Toast.LENGTH_SHORT).show()
        })
        oDialog.show();
    }

    private fun makeMeeting() {
        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"group_name\" : \"${group_name}\"," +
                    "    \"meeting_date\" : \"${meeting_date}\"," +
                    "    \"meeting_type\" : \"${meeting_type}\"," +
                    "    \"meeting_intro\" : \"${meeting_intro}\"" +
                    "}"
            Log.d("미팅 설정 Json", "${data}")

            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            val req = Request.Builder()
                    .url("https://f9b1cbcys2.execute-api.ap-northeast-2.amazonaws.com/meeting_update/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        try {
                            val data = response.body!!.string()
                            Log.d("미팅만들기", "${data}")
                            meetingDate_detailBoard.text = meeting_date
                            meetingType_detailBoard.text = meeting_type
                            meetingIntroText_detailBoard.text = meeting_intro
                        } catch (e: Exception) {
                            Log.d("fragment4", "${e.stackTrace}")
                        }
                    }
                }
            })
        }

        val api2 = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"group_name\" : \"${group_name}\"," +
                    "    \"dona\" : \"${participant?.size?.times(1000)}\"" +
                    "}"
            Log.d("후원예정금액", "${participant?.size?.times(1000)}")

            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            val req = Request.Builder()
                    .url("https://9wqj24cl0k.execute-api.ap-northeast-2.amazonaws.com/group_dona_update/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        try {
                            val data = response.body!!.string()
                            Log.d("후원금 추가", "${data}")
                            dona = ((dona.toInt() + participant?.size?.times(1000)!!).toString())
                            donaText_detailBoard.text = "앞으로 우리가 후원할 금액은 "+ dona + "원 이에요"
                        } catch (e: Exception) {
                            Log.d("fragment4", "${e.stackTrace}")
                        }
                    }
                }
            })
        }
    }

    // 데이터를 가져와서 할당합니다.
    private fun datainit() {
        name = loadFromInnerStorage("userInfoData.txt")

        group_name = intent.getStringExtra("group_name")!!
        master_name = intent.getStringExtra("master_name")!!
        open_date = intent.getStringExtra("open_date")!!
        intro = intent.getStringExtra("intro")!!
        group_pic = intent.getStringExtra("group_pic")!!
        meeting_date = intent.getStringExtra("meeting_date")!!
        meeting_type = intent.getStringExtra("meeting_type")!!
        meeting_intro = intent.getStringExtra("meeting_intro")!!
        participant = intent.getStringArrayListExtra("participant")
        dona = intent.getStringExtra("dona")!!
        dona_all = intent.getStringExtra("dona_all")!!
        loc = intent.getStringExtra("loc")!!

        groupName_detailBoard.text = group_name
        masterName_detailBoard.text = "모임장 : " + master_name
        intro_detailBoard.text = intro
        meetingDate_detailBoard.text = meeting_date
        meetingType_detailBoard.text = meeting_type+" 정모"
        meetingIntroText_detailBoard.text = meeting_intro
        donaAllText_detailBoard.text = "지금까지 우리가 후원한 금액은 "+ dona_all + "원 이에요"
        donaText_detailBoard.text = "앞으로 우리가 후원할 금액은 "+ dona + "원 이에요"
        locText_detailBoard.text = loc

        addPartcipantAdapter(participant as ArrayList<String>)

        //이미지 로딩
        Picasso.with(this).invalidate(Uri.parse(group_pic))
        Picasso.with(this)
                .load(Uri.parse(group_pic))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(groupPicture_detailBoard)
    }

    private fun addPartcipantAdapter(participant: ArrayList<String>) {
        for (datum in participant) {
            val api = CoroutineScope(Dispatchers.Default).async {
                // 보낼 데이터 json으로 만들기
                val data = "{\n" +
                        "    \"name\" : \"${datum}\"" +
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
                    override fun onFailure(call: Call, e: IOException) {}
                    override fun onResponse(call: Call, response: Response) {
                        // 응답이 오면 메인스레드에서 처리를 진행한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            // 회원조회 응답
                            try {
                                val data = response.body!!.string()
                                Log.d("응답", "${data}")
                                var rawData2 = data.substring(31, data.length - 4)
                                val res = Gson().fromJson(rawData2, UserInfo::class.java)
                                pListInfo!!.add(res)
                                Log.d("pListInfo","${pListInfo}")
                                participantView_detailBoard.adapter = UserInfoAdapter(pListInfo)
                                participantView_detailBoard.layoutManager = LinearLayoutManager(applicationContext);
                            } catch (e: java.lang.Exception) {
                                Log.d("fragment4", "${e}")
                            }
                        }
                    }
                })
            }

        }
    }

    private fun addPartcipant() {
        val api = CoroutineScope(Dispatchers.Default).async {
            // 보낼 데이터 json으로 만들기
            val data = "{\n" +
                    "    \"name\" : \"${name}\"," +
                    "    \"group_name\" : \"${group_name}\"" +
                    "}"
            Log.d("json", "$data")
            val media = "application/json; charset=utf-8".toMediaType();
            val body = data.toRequestBody(media)

            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            // 2. 요청
            val req = Request.Builder()
                    .url("https://uobq7uyhte.execute-api.ap-northeast-2.amazonaws.com/when_member_join_group/")
                    .put(body)
                    .build()

            // 3. 응답
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { }
                override fun onResponse(call: Call, response: Response) {
                    // 응답이 오면 메인스레드에서 처리를 진행한다.
                    CoroutineScope(Dispatchers.Main).launch {
                        // 회원조회 응답
                        try {
                            val data = response.body!!.string()
                            Log.d("참가하기", "$data")
                            goMain()
                        } catch (e: Exception) {
                            Log.d("fragment4", "${e.stackTrace}")
                        }
                    }
                }
            })
        }
    }

    private fun getTmLocation() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                location?.let {
                    val position = LatLng(it.latitude, it.longitude)
                    var url = "https://dapi.kakao.com/v2/local/geo/transcoord.json?"+"x=${position.longitude}&y=${position.latitude}&input_coord=WGS84&output_coord=TM"
                    Log.d("주소", "${url}")
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1f, locationListener as LocationListener)
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
                    .addHeader("Authorization", "KakaoAK 01294cd7d666c0e342e1ca5a5b7f9d0e")
                    .build()
                // 3. 응답
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}
                    override fun onResponse(call: Call, response: Response) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val data = Gson().fromJson(response.body!!.string(), TmLocation::class.java)
                            //기본 위도 경도 값을 가져옵니다.
                            Log.d("좌표값", "${data.documents[0].x}  ${data.documents[0].y}")
                            val api2 = CoroutineScope(Dispatchers.Default).async {
                                // 받아온 값을 기준으로 측정소와 먼지 데이터를 구합니다.
                                // 미세먼지의 상태를 알려주는 기능 구현
                                getObserveCenter(data.documents[0].x, data.documents[0].y)
                            }.await()
                        }
                    }
                })
            }.await()
        }
    }

    // API 네트워크 통신을 위한 예제
    private fun getObserveCenter(x: Double, y: Double) {
        val api = CoroutineScope(Dispatchers.Default).async {
            //네트워크
            var firsturl1 = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/" +
                    "getNearbyMsrstnList?serviceKey=gGhynriCFrtswJiVVWDI9T3Q%2B1Q%2FftEzDkISmGi7jWjL0knS%2BXWeI2MIOzWNxxvycoI%2FlDd1%2BDmGp7Az4FZeZA%3D%3D" +
                    "&returnType=json&"
            var firsturl2 = "tmX=${x}&tmY=${y}"

            Log.d("측정소url", firsturl1 + firsturl2)
            // 1. 클라이언트 만들기
            val client = OkHttpClient.Builder().build()
            val req = Request.Builder().url(firsturl1 + firsturl2)
                .build()

            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val data = Gson().fromJson(response.body!!.string(), ObserveCenterData::class.java)
                        // 측정소 이름을 가져옵니다.
                        //측정소 이름을 기준으로 미세먼지 농도를 측정합니다.
                        try {
                            val ENCODED_HREF = java.net.URLEncoder.encode(data.response.body.items[0].stationName, "utf-8")
                            getDust(ENCODED_HREF)
                        }catch (e : java.lang.Exception){
                            Log.d("","${e.stackTrace}")
                        }
                    }
                }
            })
        }
    }

    // 알아낸 측정소를 기준으로 해서 측정소의 미세먼지 값을 호출합니다.
    private fun getDust(ENCODED_HREF: String) {
        var url2 = "http://apis.data.go.kr/B552584/" +
                "ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?" +
                "serviceKey=gGhynriCFrtswJiVVWDI9T3Q%2B1Q%2FftEzDkISmGi7jWjL0knS%2BXWeI2MIOzWNxxvycoI%2FlDd1%2BDmGp7Az4FZeZA%3D%3D" +
                "&returnType=json&numOfRows=100&pageNo=1&stationName="
        var url3 = "&dataTerm=DAILY&ver=1.0"

        url2 = url2 + ENCODED_HREF + url3
        // 1. 클라이언트 만들기
        val client = OkHttpClient.Builder().build()
        val req = Request.Builder().url(url2)
                .build()
        val api2 = CoroutineScope(Dispatchers.Default).async {
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val data = Gson().fromJson(response.body!!.string(), DustInfo::class.java)
                        dustTextView_detaailBuiltin.text = dustGrade(data.response.body.items[0].pm10Grade)
                    }
                }
            })
        }
    }


    // 미세먼지 단계에 따른 단계 반환
    private fun dustGrade(pm10Grade: String): CharSequence? {
        when (pm10Grade) {
            "1" -> return "오늘의 미세먼지는 좋아요!"
            "2" -> return "오늘의 미세먼지는 평범해요."
            "3" -> return "오늘의 미세먼지는 나빠요."
            "4" -> return "오늘의 미세먼지는 아주 나빠요."
            else -> {
                return ""
            }
        }
    }

    fun goMain(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    //내부 저장소 파일의 텍스트를 불러온다.
    fun loadFromInnerStorage(filename: String): String? {
        val fileInputStream = openFileInput(filename)
        return fileInputStream?.reader()?.readText()
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
}