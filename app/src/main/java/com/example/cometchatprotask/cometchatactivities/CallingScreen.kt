package com.example.cometchatprotask.cometchatactivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.cometchatprotask.R

import com.example.cometchatprotask.databinding.ActivityCallingScreenBinding

class CallingScreen : AppCompatActivity(),View.OnClickListener {
    lateinit var binding : ActivityCallingScreenBinding
    private val TAG = "CallingScreen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
        initiateCall()
    }
    fun setup(){
        binding.endCall.setOnClickListener(this)
    }
    fun initiateCall(){
        val uid = intent.getStringExtra("rid")
        val calltype = intent.getStringExtra("calltype")
        val usertype = intent.getStringExtra("type")
        val call = Call(uid!!,usertype,calltype)
        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.e(TAG, "onSuccessCall: $p0")
                sessionId = p0?.sessionId
            }

            override fun onError(p0: CometChatException?) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.end_call -> cancelCall()
        }
    }

    private fun cancelCall() {
        Log.e(TAG, "onSuccessCall: $sessionId")
        if (!sessionId.isNullOrEmpty()){
            CometChat.rejectCall(sessionId!!,CometChatConstants.CALL_STATUS_CANCELLED,object: CometChat.CallbackListener<Call>(){
                override fun onSuccess(p0: Call?) {
                    finish()
                }

                override fun onError(p0: CometChatException?) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    companion object{
        private var sessionId : String ? = null
    }
}