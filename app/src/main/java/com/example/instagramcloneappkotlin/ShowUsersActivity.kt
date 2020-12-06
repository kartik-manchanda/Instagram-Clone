package com.example.instagramcloneappkotlin

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.adapter.UserAdapter
import com.example.instagramcloneappkotlin.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowUsersActivity : AppCompatActivity() {

    var id:String=""
    var title:String=""

    var userAdapter:UserAdapter?=null
    var userList:List<User>?=null
    var idList:List<String>?=null

    lateinit var toolbar:Toolbar
    lateinit var recycler:RecyclerView


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        recycler=findViewById(R.id.recycler)

        val intent=intent
        id= intent.getStringExtra("id").toString()
        title= intent.getStringExtra("title").toString()


        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recycler.setHasFixedSize(true)
        recycler.layoutManager=LinearLayoutManager(this)
        userList=ArrayList()
        userAdapter= UserAdapter(this,userList as ArrayList<User>,false)
        recycler.adapter=userAdapter

        idList=ArrayList()

        when(title){
            "likes" -> getLikes()
            "following" ->getFollowing()
            "followers"-> getFollowers()
            "views"->getViews()
        }

    }

    private fun getViews() {

        val ref= FirebaseDatabase.getInstance().reference
            .child("Story").child(id)
            .child(intent.getStringExtra("storyId")!!)
            .child("views")



        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                (idList as ArrayList<String>).clear()

                for(snapshot in dataSnapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUsers()
            }

        })





    }

    private fun getFollowers() {


        val followersRef= FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Followers")



        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                (idList as ArrayList<String>).clear()

                for(snapshot in dataSnapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUsers()
            }

        })


    }

    private fun getFollowing() {

        val followingsRef= FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Following")



        followingsRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                (idList as ArrayList<String>).clear()

                for(snapshot in dataSnapshot.children){
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUsers()

            }

        })

    }

    private fun getLikes() {

        val likesRef= FirebaseDatabase.getInstance().reference
            .child("Likes").child(id)


        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    (idList as ArrayList<String>).clear()

                    for(snapshot in dataSnapshot.children){
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUsers()
                }

            }

        })

    }

    private fun showUsers(){

        val usersRef= FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                (userList as ArrayList<User>).clear()

                for (snapshot in dataSnapshot.children)
                {
                    val user=snapshot.getValue((User::class.java))

                    for(id in idList!!){

                        if(user!!.getUID()==id){
                            (userList as ArrayList<User>).add(user)
                        }

                    }

                }

                userAdapter?.notifyDataSetChanged()
            }


        })

    }
}