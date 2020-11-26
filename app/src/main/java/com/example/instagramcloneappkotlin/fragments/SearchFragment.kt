package com.example.instagramcloneappkotlin.fragments

import com.example.instagramcloneappkotlin.adapter.UserAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.instagramcloneappkotlin.model.User


class SearchFragment : Fragment() {

    private var recyclerView:RecyclerView?=null
    private var userAdapter:UserAdapter?=null
    private var mUser:MutableList<User>?=null

    lateinit var search_edit_text:EditText



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView=view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)
        search_edit_text=view.findViewById(R.id.search_edit_text)

        mUser=ArrayList()
        userAdapter=context?.let { UserAdapter(it,mUser as ArrayList<User>,true) }
        recyclerView?.adapter=userAdapter

        search_edit_text.addTextChangedListener(object :TextWatcher

        {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(search_edit_text.text.toString()=="")
                {

                }
                else
                {
                    recyclerView?.visibility=View.VISIBLE

                    retrieveUsers()
                    searchUser(p0.toString().toLowerCase())

                }
            }


        })




        return view
    }

    private fun searchUser(input:String){

        val query= FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("fullName")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mUser?.clear()

                for (snapshot in dataSnapshot.children)
                {
                    val user=snapshot.getValue((User::class.java))
                    if(user!=null){
                        mUser?.add(user)
                    }
                }

                userAdapter?.notifyDataSetChanged()

            }

        })




    }



    private fun retrieveUsers(){

        val usersRef= FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(search_edit_text.text.toString()==""){
                    mUser?.clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val user=snapshot.getValue((User::class.java))
                        if(user!=null){
                            mUser?.add(user)
                        }
                    }

                    userAdapter?.notifyDataSetChanged()

                }
            }

        })

    }
}

