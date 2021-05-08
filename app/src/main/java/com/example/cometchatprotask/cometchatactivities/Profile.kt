package com.example.cometchatprotask.cometchatactivities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.FragmentProfileBinding
import com.example.cometchatprotask.databinding.ProfileInfoBinding
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.login.MainActivity

class Profile : Fragment(),View.OnClickListener {
    lateinit var binding : FragmentProfileBinding
    lateinit var subBinding :ProfileInfoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        subBinding = ProfileInfoBinding.bind(binding.root)
        setFields()
        return binding.root
    }

    private fun setFields() {
        val loggedInUser = CometChat.getLoggedInUser()
        subBinding.username.text = loggedInUser.name
        subBinding.status.text = loggedInUser.status
        binding.logout.setOnClickListener(this)
        Glide.with(this).load(loggedInUser.avatar).placeholder(R.drawable.user).into(subBinding.avatar)
    }

    private fun cometChatLogout() : Boolean {
        var logout = false
        CometChat.logout(object : CometChat.CallbackListener<String>(){
            override fun onSuccess(p0: String?) {
                requireContext().toast("Logout successfully")
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
                logout = true
            }

            override fun onError(p0: CometChatException?) {
                logout = false
            }

        })
        return logout
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.logout -> {
                cometChatLogout()
            }
        }
    }
}