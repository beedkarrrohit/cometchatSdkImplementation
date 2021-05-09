package com.example.cometchatprotask.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RelativeLayout
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.cometchat.pro.rtc.model.AudioMode
import com.example.cometchatprotask.cometchatactivities.CallingScreen

class Utils {
    companion object{
        fun startCall(activity: Activity,callView : RelativeLayout,call: Call){
            val callSettings = CallSettings.CallSettingsBuilder(activity,callView).setSessionId(call.sessionId).build()
            CometChat.startCall(callSettings,object: CometChat.OngoingCallListener{
                override fun onUserJoined(p0: User?) {

                }

                override fun onUserLeft(p0: User?) {

                }

                override fun onError(p0: CometChatException?) {

                }

                override fun onCallEnded(p0: Call?) {
                    activity.finish()
                }

                override fun onUserListUpdated(p0: MutableList<User>?) {

                }

                override fun onAudioModesUpdated(p0: MutableList<AudioMode>?) {

                }
            })
        }

        fun initiateCall(context: Context,receiverId : String,receiverType : String?,callType : String?){
            Log.e("TAG", "initiateCall: $receiverId")
            var call = Call(receiverId,receiverType,callType)
            CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
                override fun onSuccess(p0: Call?) {
                    Log.e("TAG", "onSuccessss: ${p0?.sessionId}" )
                    if (p0 != null) startCallIntent(context, p0.callReceiver as User, p0.type, true, p0.sessionId)
                }

                override fun onError(p0: CometChatException?) {

                }

            })
        }

        fun startCallIntent(context: Context,user: User,type : String,isOutgoing:Boolean,sessionId : String){
            var intent = Intent(context,CallingScreen::class.java)
            intent.putExtra("name",user.name)
            intent.putExtra("uid",user.uid)
            intent.putExtra("sessionId",sessionId)
            intent.putExtra("avatar",user.avatar)
            intent.action = type
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if(isOutgoing){
                intent.type = "outgoing"
            }else{
                intent.type = "incoming"
            }

            context.startActivity(intent)
        }


    }
}