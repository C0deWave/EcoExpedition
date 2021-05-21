package com.example.eco

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.setSelectedItemId(R.id.menu1)
        supportFragmentManager.beginTransaction().add(R.id.linearLayout, MainViewFragment1()).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu1 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment1()).commitAllowingStateLoss()
                return true
            }
            R.id.menu2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment2()).commitAllowingStateLoss()
                return true
            }
            R.id.menu3 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment3()).commitAllowingStateLoss()
                return true
            }
            R.id.menu4 -> {
                supportFragmentManager.beginTransaction().replace(R.id.linearLayout, MainViewFragment4()).commitAllowingStateLoss()
                return true
            }

        }
        return false
    }
}