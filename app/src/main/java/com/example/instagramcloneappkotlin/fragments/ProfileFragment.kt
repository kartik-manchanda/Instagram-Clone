package com.example.instagramcloneappkotlin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.AccountSettingsActivity
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.ShowUsersActivity
import com.example.instagramcloneappkotlin.adapter.MyImagesAdapter
import com.example.instagramcloneappkotlin.model.Post
import com.example.instagramcloneappkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    lateinit var profileId:String
    lateinit var firebaseUser:FirebaseUser
    lateinit var total_followers:TextView
    lateinit var total_followings:TextView
    lateinit var total_posts:TextView
    lateinit var pro_image_profile_frag:CircleImageView
    lateinit var profile_fragment_username:TextView
    lateinit var full_name_profile_frag:TextView
    lateinit var bio_profile_frag:TextView
    lateinit var edit_account_settings_button:Button
    lateinit var images_grid_view_button:ImageButton
    lateinit var images_save_btn:ImageButton

     var postList:List<Post>?=null
    var myImagesAdapter:MyImagesAdapter?=null

    var postListSaved:List<Post>?=null
    var myImagesAdapterSavedImg:MyImagesAdapter?=null
    var mySavesImg:List<String>?=null


     lateinit var recycler_grid_view:RecyclerView
    lateinit var recycler_grid_view_saved:RecyclerView

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManager2: LinearLayoutManager

    lateinit var followers_ll:LinearLayout
    lateinit var followings_ll:LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        followers_ll=view.findViewById(R.id.followers_ll)
        followings_ll=view.findViewById(R.id.followings_ll)

        pro_image_profile_frag=view.findViewById(R.id.pro_image_profile_frag)
        profile_fragment_username=view.findViewById(R.id.profile_fragment_username)
        full_name_profile_frag=view.findViewById(R.id.full_name_profile_frag)
        bio_profile_frag=view.findViewById(R.id.bio_profile_frag)
        edit_account_settings_button=view.findViewById(R.id.edit_account_settings_button)
        images_grid_view_button=view.findViewById(R.id.images_grid_view_button)
        images_save_btn=view.findViewById(R.id.images_save_btn)

        recycler_grid_view=view.findViewById(R.id.recycler_grid_view)
        recycler_grid_view_saved=view.findViewById(R.id.recycler_grid_view_saved_images)


        total_followings=view.findViewById(R.id.total_followings)
        total_posts=view.findViewById(R.id.total_posts)
        total_followers=view.findViewById(R.id.total_followers)



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

        //recycler for uploaded images
        recycler_grid_view.setHasFixedSize(true)
        linearLayoutManager=GridLayoutManager(context,3)
        recycler_grid_view.layoutManager=linearLayoutManager

        postList=ArrayList()
        myImagesAdapter=context?.let { MyImagesAdapter( it, postList as ArrayList<Post>) }
        recycler_grid_view.adapter=myImagesAdapter



        //recycler for saved images
        recycler_grid_view_saved.setHasFixedSize(true)
        linearLayoutManager2=GridLayoutManager(context,3)
        recycler_grid_view_saved.layoutManager=linearLayoutManager2

        postListSaved=ArrayList()
        myImagesAdapterSavedImg=context?.let { MyImagesAdapter( it, postListSaved as ArrayList<Post>) }
        recycler_grid_view_saved.adapter=myImagesAdapterSavedImg

        recycler_grid_view.setOnClickListener {
            recycler_grid_view_saved.visibility=View.GONE
            recycler_grid_view.visibility=View.VISIBLE
        }
        recycler_grid_view_saved.visibility=View.GONE
        recycler_grid_view.visibility=View.VISIBLE

        images_grid_view_button.setOnClickListener {
            recycler_grid_view_saved.visibility=View.GONE
            recycler_grid_view.visibility=View.VISIBLE
        }

        images_save_btn.setOnClickListener {
            recycler_grid_view_saved.visibility=View.VISIBLE
            recycler_grid_view.visibility=View.GONE
        }

        followers_ll.setOnClickListener {
            val intent=Intent(context,ShowUsersActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","followers")
            startActivity(intent)
        }

        followings_ll.setOnClickListener {
            val intent=Intent(context,ShowUsersActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","following")
            startActivity(intent)

        }










        edit_account_settings_button.setOnClickListener {
            val getButtonText=edit_account_settings_button.text.toString()

            when{
                getButtonText=="Edit Profile"-> startActivity(Intent(context, AccountSettingsActivity::class.java))
                getButtonText=="follow"->{
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)

                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)

                    }
                    addNotification()

                }

                getButtonText=="following"->{
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()

                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()

                    }

                }
            }

        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalnumberofPosts()
        mySaves()

        return view
    }

    private fun myPhotos(){
        val postRef=FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    ( postList as ArrayList<Post>).clear()

                    for(snapshot in dataSnapshot.children){

                        val post=snapshot.getValue(Post::class.java)
                        if(post!!.getPublisher().equals(profileId)){

                            ( postList as ArrayList<Post>).add(post)

                        }
                        Collections.reverse(postList)
                        myImagesAdapter!!.notifyDataSetChanged()
                    }
                }

            }

        })
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
                .child("Following")



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

    private fun getTotalnumberofPosts(){
        val postRef=FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    var postCounter=0

                    for(snapshot in dataSnapshot.children){
                        val post=snapshot.getValue(Post::class.java)

                        if(post!!.getPublisher()==profileId){
                            postCounter++
                        }
                    }
                    total_posts.text= postCounter.toString()
                }
            }

        })

    }

    private fun mySaves(){
        mySavesImg=ArrayList()

        val savedRef=FirebaseDatabase.getInstance()
            .reference.child("Saves")
            .child(firebaseUser!!.uid)

        savedRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    for (snapshot in dataSnapshot.children){
                        (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedImagesData()
                }

            }

        })

    }

    private fun readSavedImagesData(){
        val postsRef=FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    (postListSaved as ArrayList<Post>).clear()
                    for(snapshot in dataSnapshot.children){
                        val post=snapshot.getValue(Post::class.java)
                        for(key in mySavesImg!!){

                            if(post!!.getPostId()==key){
                                (postListSaved as ArrayList<Post>).add(post)
                            }
                        }

                    }
                    myImagesAdapterSavedImg!!.notifyDataSetChanged()
                }

            }

        })
    }

    private fun addNotification(){

        val notiRef=FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(profileId)

        val notiMap=HashMap<String,Any>()

        notiMap["userId"]=firebaseUser!!.uid
        notiMap["text"]="started following you"
        notiMap["postId"]=""
        notiMap["isPost"]=false

        notiRef.push().setValue(notiMap)


    }
}

