package com.example.instagramcloneappkotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    lateinit var signUp_link:TextView
    lateinit var login_btn: Button
    lateinit var signInEmail: EditText
    lateinit var signInPassword: EditText

    lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        login_btn = findViewById(R.id.login_btn)

        signInEmail = findViewById(R.id.email_signIn)
        signInPassword = findViewById(R.id.password_signIn)

        signUp_link=findViewById(R.id.signUp_link)

        signUp_link.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        login_btn.setOnClickListener {
            loginUser()
        }


    }

    private fun loginUser(){

        val email = signInEmail.text.toString()
        val password = signInPassword.text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "email is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "password is required",
                Toast.LENGTH_SHORT
            ).show()

            else->{

                progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("signing In")
                progressDialog.setMessage("Please wait....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth:FirebaseAuth=FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task->
                        if(task.isSuccessful){
                            progressDialog.dismiss()

                            val intent=Intent(this@SignInActivity,MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                        else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }

            }
        }


    }

//    override fun onStart() {
//        super.onStart()
//
//        if(FirebaseAuth.getInstance().currentUser!=null){
//            val intent=Intent(this@SignInActivity,MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()
//        }
//
//    }
}