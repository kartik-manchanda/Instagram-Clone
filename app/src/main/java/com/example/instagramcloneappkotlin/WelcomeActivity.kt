package com.example.instagramcloneappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    lateinit var btn_welcome_login: Button
    lateinit var btn_welcome_register:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btn_welcome_login=findViewById(R.id.btn_welcome_login)
        btn_welcome_register=findViewById(R.id.btn_welcome_register)

        btn_welcome_login.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))

        }

        btn_welcome_register.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))

        }




    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser!=null){
            val intent=Intent(this@WelcomeActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }
}