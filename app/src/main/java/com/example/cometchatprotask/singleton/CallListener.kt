package com.example.cometchatprotask.singleton

import android.content.Context
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.cometchatprotask.cometchatactivities.CallingScreen
import com.example.cometchatprotask.utils.Utils

object CallListener {
    fun addCallListener(TAG:String,context: Context){
        CometChat.addCallListener(TAG,object: CometChat.CallListener(){
            override fun onIncomingCallReceived(p0: Call?) {
                if(p0 != null){
                    if(p0.receiverType == CometChatConstants.RECEIVER_TYPE_USER){
                        Utils.startCallIntent(context,p0.callInitiator as User,p0.type,false,p0.sessionId)
                    }else if(p0.receiverType == CometChatConstants.RECEIVER_TYPE_GROUP){
                        Utils.startGroupCallIntent(context,p0.receiver as Group,p0.type,false,p0.sessionId)
                    }
                }
            }

            override fun onOutgoingCallAccepted(p0: Call?) {
                if(p0 != null) Utils.startCall(CallingScreen.activity!!,CallingScreen.view!!,p0)
            }

            override fun onOutgoingCallRejected(p0: Call?) {
                if(p0 != null)
                    if(CallingScreen.activity !=  null) CallingScreen.activity!!.finish()
            }

            override fun onIncomingCallCancelled(p0: Call?) {
                if(p0 != null){
                    if(CallingScreen.activity !=  null) CallingScreen.activity!!.finish()
                }
            }

        })
    }

    fun removeCallListener(s: String){
        CometChat.removeCallListener(s)
    }
}