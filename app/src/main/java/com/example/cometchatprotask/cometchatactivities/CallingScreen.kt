package com.example.cometchatprotask.cometchatactivities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.cometchatprotask.R

import com.example.cometchatprotask.databinding.ActivityCallingScreenBinding
import com.example.cometchatprotask.utils.Utils

class CallingScreen : AppCompatActivity(),View.OnClickListener {
    lateinit var binding : ActivityCallingScreenBinding
    private val TAG = "CallingScreen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        binding = ActivityCallingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        view = binding.mainView
        setUpIntent()
        setup()
        setupView()
        //initiateCall()
    }
    fun setup(){
        binding.endCall.setOnClickListener(this)
        binding.acceptCall.setOnClickListener(this)
        binding.rejectCall.setOnClickListener(this)
        if (!Utils.hasPermissions(this, Manifest.permission.RECORD_AUDIO) && !Utils.hasPermissions(this, Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA), REQUEST_PERMISSION)
        }
    }

    fun setUpIntent(){
        if(intent.hasExtra("sessionId")) sessionId = intent.getStringExtra("sessionId")
        if(intent.hasExtra("avatar")) avatar = intent.getStringExtra("avatar")
        if(intent.hasExtra("uid")) uid = intent.getStringExtra("uid")
        if(intent.hasExtra("name")) name = intent.getStringExtra("name")
    }

    fun setupView() {
        val isVideoCall = intent.action == CometChatConstants.CALL_TYPE_VIDEO
        val isIncoming = intent.type == "incoming"
        if(isIncoming){
            binding.endCall.visibility = View.GONE
            binding.acceptCall.visibility = View.VISIBLE
            binding.rejectCall.visibility = View.VISIBLE
            binding.callingUsername.text = name
            Glide.with(this).load(avatar).placeholder(R.drawable.user).into(binding.callingAvatar)
            if(isVideoCall)binding.callTypeTitle.text = "incoming video call" else binding.callTypeTitle.text = "incoming Audio call"
        }else{
            binding.callTypeTitle.text = "Calling..."
            binding.endCall.visibility = View.VISIBLE
            binding.acceptCall.visibility = View.GONE
            binding.rejectCall.visibility = View.GONE
            binding.callingUsername.text = name
            Glide.with(this).load(avatar).placeholder(R.drawable.user).into(binding.callingAvatar)
        }
    }

    /*fun initiateCall(){
        val uid = intent.getStringExtra("rid")
        val calltype = intent.getStringExtra("calltype")
        val usertype = intent.getStringExtra("type")
        val call = Call(uid!!,usertype,calltype)
        CometChat.initiateCall(call, object : CometChat.CallbackListener<Call>() {
            override fun onSuccess(p0: Call?) {
                Log.e(TAG, "onSuccessCall: $p0")
            }

            override fun onError(p0: CometChatException?) {

            }

        })
    }*/

    override fun onClick(p0: View?) {
        Log.e(TAG, "onClickk: $sessionId", )
        when(p0?.id){
            R.id.end_call -> rejectCall(sessionId!!,CometChatConstants.CALL_STATUS_CANCELLED)
            R.id.reject_call -> {
                rejectCall(sessionId!!,CometChatConstants.CALL_STATUS_REJECTED)
                finish()
            }
            R.id.accept_call -> acceptCall(view!!,sessionId!!)
        }
    }


    private fun acceptCall(view : RelativeLayout, sessionId: String){
        CometChat.acceptCall(sessionId,object: CometChat.CallbackListener<Call>(){
            override fun onSuccess(p0: Call?) {
                if(p0 != null) Utils.startCall(this@CallingScreen,view,p0)
            }

            override fun onError(p0: CometChatException?) {

            }

        })
    }

    private fun rejectCall(sessionId : String, status : String) {
        Log.e(TAG, "onSuccessCall: $sessionId")
        if (!sessionId.isNullOrEmpty()){
            CometChat.rejectCall(sessionId!!,status,object: CometChat.CallbackListener<Call>(){
                override fun onSuccess(p0: Call?) {
                    finish()
                }

                override fun onError(p0: CometChatException?) {

                }

            })
        }
    }
    companion object{
        @SuppressLint("StaticFieldLeak")
        var activity : Activity? =null
        var view : RelativeLayout? = null
        private var sessionId : String? = null
        private  var name : String? = null
        private var avatar : String? = null
        private var uid : String? = null
        private const val REQUEST_PERMISSION = 1
    }
}