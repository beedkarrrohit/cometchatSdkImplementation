package com.example.cometchatprotask.cometchatactivities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.core.CometChat
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.FragmentProfileBinding
import com.example.cometchatprotask.databinding.ProfileInfoBinding

class Profile : Fragment() {
    lateinit var binding : FragmentProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = FragmentProfileBinding.inflate(layoutInflater)
        setFields()
        return binding.root
    }

    private fun setFields() {
        val loggedInUser = CometChat.getLoggedInUser()
        binding.layout.username.text = loggedInUser.name
        binding.layout.status.text = loggedInUser.status
    }
}