package com.example.instagramcloneappkotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.adapter.PostAdapter
import com.example.instagramcloneappkotlin.adapter.StoryAdapter
import com.example.instagramcloneappkotlin.model.Post
import com.example.instagramcloneappkotlin.model.Story
import com.example.instagramcloneappkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class HomeFragment : Fragment() {

    lateinit var recycler_view_home: RecyclerView
    lateinit var recycler_view_story: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManager2: LinearLayoutManager

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<String>? = null

    private var storyAdapter: StoryAdapter? = null
    private var storyList:MutableList<Story>?=null

    lateinit var homeProfileImage:CircleImageView
    lateinit var homeUserInfo:TextView
    lateinit var search_home:CardView


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        homeProfileImage=view.findViewById(R.id.homeProfileImage)
        homeUserInfo=view.findViewById(R.id.homeUserInfo)
        search_home=view.findViewById(R.id.search_home)



        recycler_view_home = view.findViewById(R.id.recycler_view_home)
        linearLayoutManager = LinearLayoutManager(context)

        recycler_view_story = view.findViewById(R.id.recycler_view_story)
        linearLayoutManager2 = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recycler_view_home.adapter = postAdapter

        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recycler_view_home.layoutManager = linearLayoutManager
        recycler_view_home.setHasFixedSize(true)


        recycler_view_story.layoutManager = linearLayoutManager2

        storyList = ArrayList()
        storyAdapter = context?.let { StoryAdapter(it, storyList as ArrayList<Story>) }
        recycler_view_story.adapter = storyAdapter

        homeProfileImage.setOnClickListener {

            (context as FragmentActivity).supportFragmentManager
                .beginTransaction().replace(R.id.fragment_container, ProfileFragment())
                .commit()

        }

        search_home.setOnClickListener {
            (context as FragmentActivity).supportFragmentManager
                    .beginTransaction().addToBackStack("HomeFragment").replace(R.id.fragment_container, SearchFragment())
                    .commit()
        }


        checkFollowings()

        userInfo()



        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {

                    (followingList as ArrayList<String>).clear()

                    for (snapShot in dataSnapshot.children) {

                        snapShot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePosts()
                    retrieveStories()


                }

            }
        })


    }

    private fun userInfo(){
        val userRef=FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {



                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                            .into(homeProfileImage)

                    homeUserInfo.text=user.getfullname()

                }
            }

        })
    }

    private fun retrievePosts() {

        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList?.clear()

                for (snapShot in dataSnapshot.children) {
                    val post = snapShot.getValue(Post::class.java)

                    for (userID in (followingList as ArrayList<String>)) {
                        if (post!!.getPublisher() == userID ) {

                            postList!!.add(post)


                        }

                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

        })

    }

    private fun retrieveStories(){

        val storyRef=FirebaseDatabase.getInstance().reference
                .child("Story")

        storyRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               val timeCurrent=System.currentTimeMillis()

                (storyList as ArrayList<Story>).clear()
                (storyList as ArrayList<Story>).add(Story("",0,0,"",FirebaseAuth.getInstance().currentUser!!.uid))

                for(id in followingList!!){
                    var countStory=0
                    var story:Story?=null

                    for(snapshot in dataSnapshot.child(id).children){
                        story=snapshot.getValue(Story::class.java)

                        if(timeCurrent>story!!.getTimeStart() && timeCurrent<story.getTimeEnd()){
                            countStory++
                        }

                    }
                    if(countStory>0){
                        (storyList as ArrayList<Story>).add(story!!)

                    }
                }
                storyAdapter!!.notifyDataSetChanged()
            }

        })
    }
}

