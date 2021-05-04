package com.example.cometchatprotask.splashactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.cometchat.pro.core.CometChat
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.CometChatActivity
import com.example.cometchatprotask.login.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            if(CometChat.getLoggedInUser() == null){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, CometChatActivity::class.java))
                finish()
            }
        },1000)
    }
}