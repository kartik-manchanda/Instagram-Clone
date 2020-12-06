package com.example.instagramcloneappkotlin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.MainActivity
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.example.instagramcloneappkotlin.model.User

class UserAdapter(
    private var mContext: Context,
    private var mUser: List<User>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {



    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return mUser.size
    }


    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTv.text = user.getUsername()
        holder.fullNameTv.text = user.getfullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile)
            .into(holder.userProfileImage)

        checkFollowingStatus(user.getUID(), holder.followButton)

        holder.itemView.setOnClickListener {
           if(isFragment){
               val pref=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
               pref.putString("profileId",user.getUID())
               pref.apply()

               (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                   .replace(R.id.fragment_container,ProfileFragment()).commit()
           }
            else{
               val intent=Intent(mContext,MainActivity::class.java)
               intent.putExtra("publisherId",user.getUID())
               mContext.startActivity(intent)
           }
        }

        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }

                                }
                            }
                        }

                }
                addNotification(user.getUID())
            } else {

                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }

                                }
                            }
                        }

                }


            }
        }


    }


    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userNameTv: TextView = itemView.findViewById(R.id.user_user_name_search)
        var fullNameTv: TextView = itemView.findViewById(R.id.user_full_name_search)
        var userProfileImage: ImageView = itemView.findViewById(R.id.user_profile_img_search)
        var followButton: Button = itemView.findViewById(R.id.follow_btn_search)

    }

    private fun checkFollowingStatus(uid: String, followButton: Button) {

        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }

        followingRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.child(uid).exists()){
                    followButton.text="following"
                }

                else{
                    followButton.text="follow"

                }

            }

        })

    }

    private fun addNotification(userId:String){

        val notiRef=FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notiMap=HashMap<String,Any>()

        notiMap["userId"]=firebaseUser!!.uid
        notiMap["text"]="started following you"
        notiMap["postId"]=""
        notiMap["isPost"]=false

        notiRef.push().setValue(notiMap)


    }
}