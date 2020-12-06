package com.example.instagramcloneappkotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddStoryActivity : AppCompatActivity() {

    private var myUrl=""
    private  var imageUri: Uri?=null

    private  var storageStoryPicRef: StorageReference?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        storageStoryPicRef= FirebaseStorage.getInstance().reference.child("Story Pic")

        CropImage.activity()
                .setAspectRatio(9,16)
                .start(this@AddStoryActivity)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)

            imageUri=result.uri
            uploadStory()
        }
    }

    private fun uploadStory() {

        when{

            imageUri==null -> Toast.makeText(this,"Please select image first", Toast.LENGTH_SHORT).show()


            else->{

                val progressDialog= ProgressDialog(this)
                progressDialog.setTitle("Uploading story")
                progressDialog.setMessage("Please wait, we are uploading your story....")
                progressDialog.show()
                val fileRef=storageStoryPicRef!!.child( System.currentTimeMillis().toString() +".jpg")

                var uploadTask: StorageTask<*>
                uploadTask=fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task->
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

                        val ref= FirebaseDatabase.getInstance().reference.child("Story")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        val storyId=(ref.push().key).toString()
                        val postId=ref.push().key

                        val timeEnd=System.currentTimeMillis() + 86400000 //one day later


                        val storyMap = HashMap<String, Any>()

                        storyMap["userId"] = FirebaseAuth.getInstance().currentUser!!.uid
                        storyMap["timeStart"] =ServerValue.TIMESTAMP
                        storyMap["timeEnd"] = timeEnd
                        storyMap["imageUrl"] = myUrl
                        storyMap["storyId"] =storyId

                        ref.child(storyId).updateChildren(storyMap)


                        Toast.makeText(this, "Story uploaded Successfully", Toast.LENGTH_SHORT).show()
                        val intent=Intent(this@AddStoryActivity,MainActivity::class.java)
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