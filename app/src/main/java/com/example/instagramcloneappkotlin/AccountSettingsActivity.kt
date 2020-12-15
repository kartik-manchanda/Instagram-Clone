package com.example.instagramcloneappkotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView

class AccountSettingsActivity : AppCompatActivity() {

    lateinit var firebaseUser: FirebaseUser
    private var checker=""

    lateinit var logout_btn:Button
    lateinit var profile_image_view_profile_frag:CircleImageView
    lateinit var full_name_profile_frag:EditText
    lateinit var user_name_profile_frag:EditText
    lateinit var bio_profile_frag:EditText

    lateinit var save_info_profile_btn:ImageView
    lateinit var close_profile_btn:ImageView

    lateinit var change_image_text_btn:TextView

    private var myUrl=""
    private  var imageUri:Uri?=null

    private  var storageProfilePicRef:StorageReference?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser=FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef=FirebaseStorage.getInstance().reference.child("Profile Pic")

        profile_image_view_profile_frag=findViewById(R.id.profile_image_view_profile_frag)
        full_name_profile_frag=findViewById(R.id.full_name_profile_frag)
        user_name_profile_frag=findViewById(R.id.user_name_profile_frag)
        bio_profile_frag=findViewById(R.id.bio_profile_frag)
        logout_btn=findViewById(R.id.logout_btn)
        change_image_text_btn=findViewById(R.id.change_image_text_btn)

        save_info_profile_btn=findViewById(R.id.save_info_profile_btn)
        close_profile_btn=findViewById(R.id.close_profile_btn)

        close_profile_btn.setOnClickListener {
           finish()
        }




        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent= Intent(this@AccountSettingsActivity,WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_image_text_btn.setOnClickListener {
            checker="clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        save_info_profile_btn.setOnClickListener {
            if(checker=="clicked"){

                uploadImageAndUpdateInfo()

            }else{

                updateUserInfoOnly()

            }


        }

        userInfo()
    }

    private fun uploadImageAndUpdateInfo(){



        when{




            imageUri==null -> Toast.makeText(this,"Please select image first",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty((full_name_profile_frag.text.toString()=="").toString()) -> Toast.makeText(this,"Please write full name",Toast.LENGTH_SHORT).show()

            user_name_profile_frag.text.toString()=="" -> Toast.makeText(this,"Please write user name",Toast.LENGTH_SHORT).show()

            bio_profile_frag.text.toString()=="" -> Toast.makeText(this,"Please write  bio",Toast.LENGTH_SHORT).show()


            else->{
                val progressDialog=ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile")
                progressDialog.show()

                val fileRef=storageProfilePicRef!!.child(firebaseUser!!.uid +".jpg")

                var uploadTask:StorageTask<*>
                uploadTask=fileRef.putFile(imageUri!!)


                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{task->
                    if(!task.isSuccessful){
                        task.exception?.let{
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri>  {task->

                    if(task.isSuccessful){
                        val downloadUrl=task.result
                        myUrl=downloadUrl.toString()

                        val ref=FirebaseDatabase.getInstance().reference.child("Users")


                        val userMap = HashMap<String, Any>()

                        userMap["fullName"] = full_name_profile_frag.text.toString().toLowerCase()
                        userMap["userName"] = user_name_profile_frag.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)


                        Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show()
                        val intent=Intent(this@AccountSettingsActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }else{
                        progressDialog.dismiss()
                    }



                })

            }





        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==Activity.RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)

            imageUri=result.uri

            profile_image_view_profile_frag.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly(){

        when {
            TextUtils.isEmpty((full_name_profile_frag.text.toString()=="").toString()) -> {
                Toast.makeText(this,"Please write full name",Toast.LENGTH_SHORT).show()
            }
            user_name_profile_frag.text.toString()=="" -> {
                Toast.makeText(this,"Please write user name",Toast.LENGTH_SHORT).show()
            }
            bio_profile_frag.text.toString()=="" -> {
                Toast.makeText(this,"Please write  bio",Toast.LENGTH_SHORT).show()
            }
            else -> {

                val userRef= FirebaseDatabase.getInstance().reference
                    .child("Users")

                val userMap = HashMap<String, Any>()

                userMap["fullName"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["userName"] = user_name_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show()
                val intent=Intent(this@AccountSettingsActivity,MainActivity::class.java)
                startActivity(intent)
                finish()


            }
        }



    }



    private fun userInfo(){
        val userRef= FirebaseDatabase.getInstance().getReference()
            .child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {



                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_view_profile_frag)

                    user_name_profile_frag.setText(user.getUsername())
                    full_name_profile_frag.setText(user.getfullname())
                    bio_profile_frag.setText(user.getBio())

                }
            }

        })
    }
}