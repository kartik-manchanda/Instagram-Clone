package com.example.instagramcloneappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.adapter.CommentsAdapter
import com.example.instagramcloneappkotlin.model.Comment
import com.example.instagramcloneappkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentsActivity : AppCompatActivity() {

    private var postId=""
    private var publisherId=""

    lateinit var profile_image_comment:CircleImageView
    lateinit var post_image_comment:ImageView
    lateinit var add_comment:EditText
    lateinit var post_comment:TextView
    lateinit var recycler_view:RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
     var commentsAdapter: CommentsAdapter?=null
    private var commentList:MutableList<Comment>?=null

    private var firebaseUser:FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        profile_image_comment=findViewById(R.id.profile_image_comment)
        add_comment=findViewById(R.id.add_comment)
        post_comment=findViewById(R.id.post_comment)
        recycler_view=findViewById(R.id.recycler_comments)
        post_image_comment=findViewById(R.id.post_image_comment)


        linearLayoutManager= LinearLayoutManager(this)
        linearLayoutManager.reverseLayout=true
        recycler_view.layoutManager=linearLayoutManager

        val intent=getIntent()

        postId= intent.getStringExtra("postId").toString()
        publisherId= intent.getStringExtra("publisherId").toString()

        firebaseUser=FirebaseAuth.getInstance().currentUser

        commentList=ArrayList()
        commentsAdapter= CommentsAdapter(this,commentList)
        recycler_view.adapter=commentsAdapter

        userInfo()
        readComments()
        getPostImage()

        post_comment.setOnClickListener {
            if(add_comment.text.toString()==""){
                Toast.makeText(this@CommentsActivity,"Cant post an empty comment",Toast.LENGTH_SHORT).show()

            }else{
                addComment()
            }
        }




    }


    private fun userInfo(){
        val userRef= FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {



                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_comment)

                }
            }

        })
    }


    private fun getPostImage(){
        val postRef= FirebaseDatabase.getInstance().reference
                .child("Posts").child(postId).child("postImage")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {



                if(snapshot.exists()){
                    val image=snapshot.value.toString()


                    Picasso.get().load(image).placeholder(R.drawable.profile)
                            .into(post_image_comment)

                }
            }

        })
    }

    private fun addComment(){
        val commentsRef=FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId)

        val commentsMap=HashMap<String,Any>()

        commentsMap["comment"]=add_comment.text.toString()
        commentsMap["publisher"]=firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addNotification()

        add_comment.text.clear()
    }

    private fun readComments(){
        val commentsRef=FirebaseDatabase.getInstance()
                .reference.child("Comments")
                .child(postId)

        commentsRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    commentList!!.clear()

                    for(snapshot in dataSnapshot.children){
                        val comment=snapshot.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentsAdapter!!.notifyDataSetChanged()

                }

            }

        })
    }

    private fun addNotification(){

        val notiRef=FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(publisherId)

        val notiMap=HashMap<String,Any>()

        notiMap["userId"]=firebaseUser!!.uid
        notiMap["text"]="commented: " + add_comment.text.toString()
        notiMap["postId"]=postId
        notiMap["isPost"]=true

        notiRef.push().setValue(notiMap)


    }
}