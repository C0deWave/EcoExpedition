package com.example.eco

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_banner.*

class banner : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)

        // 뷰페이저에 어댑터를 할당합니다.
        var adapter : ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager2.bringToFront()
        val page1 = LoginSelectFragment()
        adapter.addFragment(page1, "1")
        val page2 = LoginFragment()
        adapter.addFragment(page2, "1")
        viewPager2.adapter = adapter;
    }

    fun goLoginPager(){
        viewPager2.arrowScroll(View.FOCUS_RIGHT)
    }

    fun goSignPage(){
        val intent = Intent(applicationContext, MakeAccount::class.java)
        startActivity(intent)
    }

    fun backPager(){
        viewPager2.arrowScroll(View.FOCUS_LEFT)
        viewPager2.arrowScroll(View.FOCUS_LEFT)
    }

    fun Login(id: String,password: String){
        Toast.makeText(applicationContext, id+" " + password, Toast.LENGTH_SHORT).show()
    }

}
