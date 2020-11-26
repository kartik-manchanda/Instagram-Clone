package com.example.instagramcloneappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.instagramcloneappkotlin.R
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsActivity : AppCompatActivity() {

    lateinit var logout_btn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        logout_btn=findViewById(R.id.logout_btn)
        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent= Intent(this@AccountSettingsActivity,SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}