package com.example.instagramcloneappkotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.R
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

class CommentsAdapter(private val mContext:Context,
                      private val mComment:MutableList<Comment>?) :
RecyclerView.Adapter<CommentsAdapter.ViewHolder>(){

    private var firebaseUser:FirebaseUser?=null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
     return mComment!!.size
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {

        firebaseUser=FirebaseAuth.getInstance().currentUser

        val comment=mComment!![position]
        holder.commentTv.text=comment.getComment()

        getUserInfo(holder.imageProfile,holder.username,comment.getPublisher())

    }




    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){

        var imageProfile:CircleImageView
        var username:TextView
        var commentTv:TextView

        init{

            imageProfile=itemView.findViewById(R.id.user_profile_image_comment)
            username=itemView.findViewById(R.id.user_name_comment)
            commentTv=itemView.findViewById(R.id.comment_comment)
        }


    }

    private fun getUserInfo(imageProfile: CircleImageView, username: TextView, publisher: String) {

        val userRef=FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(publisher)

        userRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    val user=dataSnapshot.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)
                    username.text=user.getUsername()
                }

            }

        })

    }


}