package com.example.instagramcloneappkotlin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.instagramcloneappkotlin.AccountSettingsActivity
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : Fragment() {

    lateinit var profileId:String
    lateinit var firebaseUser:FirebaseUser
    lateinit var total_followers:TextView
    lateinit var total_followings:TextView
    lateinit var pro_image_profile_frag:CircleImageView
    lateinit var profile_fragment_username:TextView
    lateinit var full_name_profile_frag:TextView
    lateinit var bio_profile_frag:TextView

    lateinit var edit_account_settings_button:Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        pro_image_profile_frag=view.findViewById(R.id.pro_image_profile_frag)

        profile_fragment_username=view.findViewById(R.id.profile_fragment_username)
        full_name_profile_frag=view.findViewById(R.id.full_name_profile_frag)
        bio_profile_frag=view.findViewById(R.id.bio_profile_frag)


        total_followings=view.findViewById(R.id.total_followings)

        total_followers=view.findViewById(R.id.total_followers)

        edit_account_settings_button=view.findViewById(R.id.edit_account_settings_button)

        firebaseUser=FirebaseAuth.getInstance().currentUser!!

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if(pref!=null){

            this.profileId= pref.getString("profileId","none")!!

        }

        if(profileId==firebaseUser.uid){
            edit_account_settings_button.text="Edit Profile"

        }
        else if(profileId!=firebaseUser.uid){
            checkFollowAndFollowingButtonStatus()

        }


        edit_account_settings_button.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        getFollowers()
        getFollowings()
        userInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatus(){
        val followingRef= firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")


        }

        if(followingRef!=null){

            followingRef.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()){
                        edit_account_settings_button.text="following"

                    }

                    else{
                        edit_account_settings_button.text="follow"
                    }

                }

            })
        }
    }



    private fun getFollowers(){

        val followersRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")



        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
              if(snapshot.exists()){
                  total_followers.text= snapshot.childrenCount.toString()

              }
            }

        })

    }



    private fun getFollowings(){

        val followingsRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followings")



        followingsRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    total_followings.text= snapshot.childrenCount.toString()

                }
            }

        })

    }

    private fun userInfo(){
        val userRef=FirebaseDatabase.getInstance().getReference()
            .child("Users").child(profileId)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
//                if(context!=null){
//                    return
//                }

                if(snapshot.exists()){
                    val user=snapshot.getValue< User>(User::class.java)

                     Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(pro_image_profile_frag)

                    profile_fragment_username.text=user.getUsername()
                    full_name_profile_frag.text=user.getfullname()
                    bio_profile_frag.text=user.getBio()

                }
            }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }
}

