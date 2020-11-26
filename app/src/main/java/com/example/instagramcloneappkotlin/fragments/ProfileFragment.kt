package com.example.instagramcloneappkotlin.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.instagramcloneappkotlin.AccountSettingsActivity
import com.example.instagramcloneappkotlin.R



class ProfileFragment : Fragment() {

    lateinit var edit_account_settings_button:Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        edit_account_settings_button=view.findViewById(R.id.edit_account_settings_button)
        edit_account_settings_button.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }
        return view
    }
}

