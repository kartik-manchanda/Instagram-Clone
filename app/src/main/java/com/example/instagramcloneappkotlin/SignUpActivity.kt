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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    lateinit var progressDialog:ProgressDialog

    lateinit var signIn_link: TextView
    lateinit var register_btn: Button
    lateinit var registerFullName: EditText
    lateinit var registerUserName: EditText
    lateinit var registerEmail: EditText
    lateinit var registerPassword: EditText

    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signIn_link = findViewById(R.id.signIn_link)
        register_btn = findViewById(R.id.register_btn)
        registerFullName = findViewById(R.id.fullName_signUp)
        registerUserName = findViewById(R.id.userName_signUp)
        registerEmail = findViewById(R.id.email_signUp)
        registerPassword = findViewById(R.id.password_signUp)


        signIn_link.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        register_btn.setOnClickListener {
            createAccount()
        }


    }

    private fun createAccount() {
        val fullName = registerFullName.text.toString()
        val userName = registerUserName.text.toString()
        val email = registerEmail.text.toString()
        val password = registerPassword.text.toString()

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(
                this,
                "full name is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(
                this,
                "user name is required",
                Toast.LENGTH_SHORT
            ).show()
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

            else -> {

                 progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("signUp")
                progressDialog.setMessage("Please wait....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()


                mAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserInfo(fullName, userName, email,progressDialog)


                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }

            }


        }

    }

    private fun saveUserInfo(fullName: String, username: String, email: String, progressDialog:ProgressDialog) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()

        userMap["uid"] = currentUserId
        userMap["fullName"] = fullName.toLowerCase()
        userMap["userName"] = username.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Hey"
        userMap["image"]="https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/Gnome-stock_person.svg/1024px-Gnome-stock_person.svg.png"
            usersRef.child(currentUserId).setValue(userMap)
                .addOnCompleteListener {task->
                    if(task.isSuccessful){
                        progressDialog.dismiss()
                        Toast.makeText(this, "Account has been created Successfully", Toast.LENGTH_SHORT).show()


                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(currentUserId)
                                .child("Following").child(currentUserId)
                                .setValue(true)




                        val intent=Intent(this@SignUpActivity,MainActivity::class.java)
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