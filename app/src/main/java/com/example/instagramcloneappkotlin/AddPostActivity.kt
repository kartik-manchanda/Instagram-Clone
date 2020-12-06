package com.example.instagramcloneappkotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {

    private var myUrl=""
    private  var imageUri: Uri?=null

    private  var storagePostPicRef: StorageReference?=null

    lateinit var save_new_post_btn:ImageView

    lateinit var imagePost:ImageView
    lateinit var descriptionPost:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        save_new_post_btn=findViewById(R.id.save_new_post_btn)
        imagePost=findViewById(R.id.image_post)
        descriptionPost=findViewById(R.id.description_post)

        storagePostPicRef= FirebaseStorage.getInstance().reference.child("Posts Pic")

        save_new_post_btn.setOnClickListener {
            uploadImage()
        }
        CropImage.activity()
                .setAspectRatio(4,5)
                .start(this@AddPostActivity)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)

            imageUri=result.uri

            imagePost.setImageURI(imageUri)
        }
    }



    private fun uploadImage(){
        when{

            imageUri==null -> Toast.makeText(this,"Please select image first", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(descriptionPost.text.toString())->Toast.makeText(this,"say something",Toast.LENGTH_SHORT).show()

            else->{

                val progressDialog= ProgressDialog(this)
                progressDialog.setTitle("Uploading new Post")
                progressDialog.setMessage("Please wait, we are uploading your post....")
                progressDialog.show()
                val fileRef=storagePostPicRef!!.child( System.currentTimeMillis().toString() +".jpg")

                var uploadTask: StorageTask<*>
                uploadTask=fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{ task->
                    if(!task.isSuccessful){
                        task.exception?.let{
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri>  { task->

                    if(task.isSuccessful){
                        val downloadUrl=task.result
                        myUrl=downloadUrl.toString()

                        val ref= FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId=ref.push().key


                        val postMap = HashMap<String, Any>()

                        postMap["postId"] = postId!!
                        postMap["description"] =descriptionPost.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = myUrl

                        ref.child(postId).updateChildren(postMap)


                        Toast.makeText(this, "Post uploaded Successfully", Toast.LENGTH_SHORT).show()
                        val intent=Intent(this@AddPostActivity,MainActivity::class.java)
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
}