package com.example.instagramcloneappkotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramcloneappkotlin.R
import com.example.instagramcloneappkotlin.adapter.NotificationAdapter
import com.example.instagramcloneappkotlin.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class NotificationFragment : Fragment() {

    private var notificationList:List<Notification>?=null
    private var notificationAdapter:NotificationAdapter?=null

    lateinit var recycler_notifications:RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_notification, container, false)

        recycler_notifications=view.findViewById(R.id.recycler_view_notifications)
        recycler_notifications.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(context)
        recycler_notifications.layoutManager=linearLayoutManager

            notificationList=ArrayList()

        notificationAdapter= NotificationAdapter(requireContext(),notificationList as ArrayList<Notification>)
        recycler_notifications.adapter=notificationAdapter

        readNotifications()

        return view
    }

    private fun readNotifications() {

        val notiRef=FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        notiRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    (notificationList as ArrayList<Notification>).clear()

                    for(snapshot in dataSnapshot.children){
                        val notification=snapshot.getValue(Notification::class.java)
                        (notificationList as ArrayList<Notification>).add(notification!!)
                    }
                    Collections.reverse(notificationList)
                    notificationAdapter!!.notifyDataSetChanged()

                }

            }

        })
    }
}

