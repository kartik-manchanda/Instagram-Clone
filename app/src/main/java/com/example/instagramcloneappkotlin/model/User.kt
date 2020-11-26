package com.example.instagramcloneappkotlin.model

class User {
    private var userName:String=""
    private var fullName:String=""
    private var bio:String=""
    private var image:String=""
    private var uid:String=""

    constructor()

    constructor(userName:String,fullName:String,bio:String,image:String,uid:String){

        this.userName=userName
        this.fullName=fullName
        this.bio=bio
        this.image=image
        this.uid=uid
    }

    fun getUsername():String
    {
        return userName
    }

    fun setUsername(userName: String){
        this.userName=userName
    }

    fun getfullname():String
    {
        return fullName
    }

    fun setfullname(fullName: String){
        this.fullName=fullName
    }

    fun getBio():String
    {
        return bio
    }

    fun setBio(bio: String){
        this.bio=bio
    }

    fun getImage():String
    {
        return image
    }

    fun setImage(image: String){
        this.image=image
    }

    fun getUID():String
    {
        return uid
    }

    fun setUID(uid: String){
        this.uid=uid
    }


}