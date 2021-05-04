package com.example.cometchatprotask.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.cometchatprotask.cometchatactivities.CometChatActivity
import com.example.cometchatprotask.utils.Constants
import com.example.cometchatprotask.handler.toast

class Login() {
    companion object{
        private const val TAG = "Login"
        fun login(context: Context,uid : String?){
           if(CometChat.getLoggedInUser() == null){
               CometChat.login(uid!!, Constants.AUTH_KEY, object : CometChat.CallbackListener<User>() {
                   override fun onSuccess(p0: User?) {
                       Log.d(TAG, "onLoginSuccess: ${p0.toString()}")
                       context.toast("Login Successful")
                       var intent = Intent(context, CometChatActivity::class.java)
                       context.startActivity(intent)
                       (context as Activity).finish()
                   }

                   override fun onError(p0: CometChatException?) {
                       context.toast("Login Failed: ${p0.toString()}")
                   }
               })
           }else{
               context.toast("User already logged in")
               context.startActivity(Intent(context, CometChatActivity::class.java))
           }
        }
    }
}