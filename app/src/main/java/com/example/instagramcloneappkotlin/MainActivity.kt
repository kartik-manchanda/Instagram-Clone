package com.example.instagramcloneappkotlin

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.fragments.HomeFragment
import com.example.instagramcloneappkotlin.fragments.NotificationFragment
import com.example.instagramcloneappkotlin.fragments.ProfileFragment
import com.example.instagramcloneappkotlin.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    internal var selectedFragment:Fragment?=null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                moveToFragment(HomeFragment())
                item.setIcon(R.drawable.ic_home_nav_filled)
                return@OnNavigationItemSelectedListener true

            }
//            R.id.navigation_search -> {
//
//                moveToFragment(SearchFragment())
//                return@OnNavigationItemSelectedListener true
//
//            }
            R.id.navigation_add -> {

                item.isChecked = false
                startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notification -> {

                moveToFragment(NotificationFragment())
                item.setIcon(R.drawable.notification_filled)
                return@OnNavigationItemSelectedListener true

            }
//            R.id.navigation_profile -> {
//
//                moveToFragment(ProfileFragment())
//                return@OnNavigationItemSelectedListener true
//
//            }
        }



        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

   moveToFragment(HomeFragment())

    }

    private fun moveToFragment(fragment:Fragment){
        val fragmentTrans=supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container,fragment)
        fragmentTrans.commit()

    }
}