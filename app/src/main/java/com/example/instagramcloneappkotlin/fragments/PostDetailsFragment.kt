package com.example.instagramcloneappkotlin.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.adapter.PostAdapter
import com.example.instagramcloneappkotlin.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailsFragment : Fragment() {

    lateinit var recyclerView:RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager

    private var postAdapter:PostAdapter?=null
    private var postList:MutableList<Post>?=null
    private var postId:String=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_details, container, false)

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)

        if(pref!=null){
            postId= pref.getString("postId","none").toString()
        }

        recyclerView=view.findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(context)
        recyclerView.layoutManager=linearLayoutManager

        postList=ArrayList()

        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        retrievePosts()



        return view
    }


    private fun retrievePosts() {

        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                .child(postId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList?.clear()
                val post = dataSnapshot.getValue(Post::class.java)

                postList!!.add(post!!)
                postAdapter!!.notifyDataSetChanged()


            }

        })

    }
}



