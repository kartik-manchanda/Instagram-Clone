package com.example.instagramcloneappkotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.fragments.PostDetailsFragment
import com.example.instagramcloneappkotlin.fragments.ProfileFragment
import com.example.instagramcloneappkotlin.model.Notification
import com.example.instagramcloneappkotlin.model.Post
import com.example.instagramcloneappkotlin.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(private val mContext:Context,
                          private val mNotification:List<Notification>):
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {




    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){

        var postImage: ImageView
        var profileImage: CircleImageView
        var userName: TextView
        var text:TextView

        init{

            postImage=itemView.findViewById(R.id.notification_post_image)
            profileImage=itemView.findViewById(R.id.notification_profile_img)
            userName=itemView.findViewById(R.id.username_notification)
            text=itemView.findViewById(R.id.comment_notification)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item_layout, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mNotification.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notification=mNotification[position]

        if(notification.getText().equals("started following you")){
            holder.text.text="started following you"
        }
        else if(notification.getText().equals("liked your post")){
            holder.text.text="liked your post"
        }
        else if(notification.getText().contains("commented:")){
            holder.text.text=notification.getText().replace("commented:","commented: ")
        }
        else{
            holder.text.text=notification.getText()
        }




        userInfo(holder.profileImage,holder.userName,notification.getUserId())

        if(notification.isIsPost()){
            holder.postImage.visibility=View.VISIBLE
            getPostImage(holder.postImage,notification.getPostId())
        }else{

            holder.postImage.visibility=View.GONE

        }

        holder.itemView.setOnClickListener {

            if(notification.isIsPost()){

                val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("postId",notification.getPostId()).apply()

                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.fragment_container, PostDetailsFragment())
                    .commit()

            }else{

                val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("profileId",notification.getUserId()).apply()

                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.fragment_container, ProfileFragment())
                    .commit()


            }
        }




    }

    private fun userInfo(imageView: ImageView,userName:TextView,publiherId:String){
        val userRef= FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publiherId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {



                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(imageView)

                    userName.text=user.getUsername()

                }
            }

        })
    }

    private fun getPostImage(imageView: ImageView,postID:String){
        val postRef= FirebaseDatabase.getInstance().reference
            .child("Posts").child(postID)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {



                if(snapshot.exists()){
                    val post=snapshot.getValue<Post>(Post::class.java)


                    Picasso.get().load(post!!.getPostImage()).placeholder(R.drawable.profile)
                        .into(imageView)

                }
            }

        })
    }

}