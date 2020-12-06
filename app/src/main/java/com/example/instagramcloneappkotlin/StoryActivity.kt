package com.example.instagramcloneappkotlin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.instagramcloneappkotlin.adapter.StoryAdapter
import com.example.instagramcloneappkotlin.model.Story
import com.example.instagramcloneappkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import jp.shts.android.storiesprogressview.StoriesProgressView

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    var currentUserId:String=""
    var userId:String=""

    var counter=0
    var pressTime=0L
    var limit=500L

    lateinit var  seen_number:TextView
    lateinit var story_profile_image:CircleImageView
    lateinit var story_username:TextView
    lateinit var image_story:ImageView
    lateinit var layout_seen:LinearLayout
    lateinit var story_delete:TextView

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener=View.OnTouchListener { view, motionEvent->

        when(motionEvent.action){
            MotionEvent.ACTION_DOWN->
            {
                pressTime=System.currentTimeMillis()
                storiesProgressView!!.pause()
                return@OnTouchListener false

            }
            MotionEvent.ACTION_UP->
            {

                val now=System.currentTimeMillis()
                storiesProgressView!!.resume()
                return@OnTouchListener limit<now-pressTime

            }

        }

        false
    }


    var imagesList:List<String>?=null
    var storyIdsList:List<String>?=null

    var storiesProgressView:StoriesProgressView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        seen_number=findViewById(R.id.seen_number)
        story_profile_image=findViewById(R.id.story_profile_image)
        story_username=findViewById(R.id.story_username)
        storiesProgressView=findViewById(R.id.stories_progress)
        image_story=findViewById(R.id.image_story)
        layout_seen=findViewById(R.id.layout_seen)
        story_delete=findViewById(R.id.story_delete)

        currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        userId= intent.getStringExtra("userId").toString()
//        counter= intent.getStringExtra("userId").toString()

        layout_seen.visibility= View.GONE
        story_delete.visibility=View.GONE

        if(userId==currentUserId){

            layout_seen.visibility= View.VISIBLE
            story_delete.visibility=View.VISIBLE

        }

        getStories(userId)
        userInfo(userId)

        val reverse:View=findViewById(R.id.reverse)
        reverse.setOnClickListener {
            storiesProgressView!!.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        val skip:View=findViewById(R.id.skip)
        reverse.setOnClickListener {
            storiesProgressView!!.skip() }
        skip .setOnTouchListener(onTouchListener)

        seen_number.setOnClickListener {
            val intent= Intent(this@StoryActivity,ShowUsersActivity::class.java)
            intent.putExtra("id",userId)
            intent.putExtra("storyId",storyIdsList!![counter])
            intent.putExtra("title","views")
            startActivity(intent)
        }

        story_delete.setOnClickListener {

            val ref=FirebaseDatabase.getInstance().reference
                .child("Story")
                .child(userId)
                .child(storyIdsList!![counter])

            ref.removeValue().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Deleted Successfully",Toast.LENGTH_SHORT).show()
                }
            }

        }


    }

    private fun seenNumber(storyId:String){
        val ref=FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId).
            child(storyId)
            .child("views")

        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {



            }

            override fun onDataChange(snapshot: DataSnapshot) {

                seen_number.text="" +snapshot.childrenCount

            }

        })
    }

    private fun addViewToStory(storyId: String){

        FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId).
            child(storyId)
            .child("views")
            .child(currentUserId)
            .setValue(true)


    }

    private fun userInfo( userId:String){
        val userRef= FirebaseDatabase.getInstance().getReference()
            .child("Users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {



                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(story_profile_image)

                    story_username.text=user.getUsername()


                }
            }

        })
    }

    private fun getStories(userId:String){
        imagesList=ArrayList()
        storyIdsList=ArrayList()

        val ref=FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId)

        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (imagesList as ArrayList<String>).clear()
                (storyIdsList as ArrayList<String>).clear()

                for(snapshot in dataSnapshot.children){
                    val story:Story?=snapshot.getValue<Story>(Story::class.java)
                    val timeCurrent=System.currentTimeMillis()

                    if(timeCurrent>story!!.getTimeStart()&&timeCurrent<story.getTimeEnd()){

                        (imagesList as ArrayList<String>).add(story.getImageUrl())
                        (storyIdsList as ArrayList<String>).add(story.getStoryId())
                    }
                }
                storiesProgressView!!.setStoriesCount( (imagesList as ArrayList<String>).size)
                storiesProgressView!!.setStoryDuration(6000L)
                storiesProgressView!!.setStoriesListener(this@StoryActivity)
                storiesProgressView!!.startStories(counter)
                Picasso.get().load(imagesList!!.get(counter)).placeholder(R.drawable.profile).into(image_story)

                addViewToStory(storyIdsList!!.get(counter))
                seenNumber(storyIdsList!!.get(counter))
            }

        })

    }

    override fun onComplete() {
        finish()

    }

    override fun onPrev() {

        if(counter-1<0) return
        Picasso.get().load(imagesList!![--counter]).placeholder(R.drawable.profile).into(image_story)
        seenNumber(storyIdsList!![counter])


    }

    override fun onNext() {
        Picasso.get().load(imagesList!![++counter]).placeholder(R.drawable.profile).into(image_story)
        addViewToStory(storyIdsList!![counter])
        seenNumber(storyIdsList!![counter])

    }

    override fun onDestroy() {
        super.onDestroy()
        storiesProgressView!!.destroy()
    }

    override fun onResume() {
        super.onResume()
        storiesProgressView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView!!.pause()
    }
}