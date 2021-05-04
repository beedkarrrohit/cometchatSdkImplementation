package com.example.cometchatprotask.application

import android.app.Application
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.utils.Constants.Companion.appID
import com.example.cometchatprotask.utils.Constants.Companion.appSettings

class Initializer : Application() {

    override fun onCreate() {
        super.onCreate()
        CometChat.init(this,appID,appSettings,object: CometChat.CallbackListener<String>(){
            override fun onSuccess(p0: String?) {
                applicationContext.toast("CometChatActivity Initialized Successfully")
            }

            override fun onError(p0: CometChatException?) {
                applicationContext.toast("Initialization Failed with exception: ${p0?.message}")
            }

        })
    }
}