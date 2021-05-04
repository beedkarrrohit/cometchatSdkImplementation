package com.example.cometchatprotask.cometchatactivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.CustomMessage
import com.cometchat.pro.models.MediaMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.adapters.GroupMessageAdapter
import com.example.cometchatprotask.databinding.ActivityGroupChatScreenBinding
import com.example.cometchatprotask.handler.toast

class GroupChatScreenActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var binding : ActivityGroupChatScreenBinding
    lateinit var bundle: Bundle
    lateinit var adapter: GroupMessageAdapter
    private  val TAG = "GroupChatScreenActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.groupChatToolbar)
        bundle = intent.extras!!
        setupFields()
    }

    private fun setupFields() {
        Glide.with(this).load(bundle.getString("icon")).into(binding.groupIcon)
        binding.groupName.text = bundle.getString("gname")
        adapter = GroupMessageAdapter()
        binding.groupRecy.adapter = adapter
        binding.createMessage.sendBtn.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        fetchMessage()
        CometChat.addMessageListener(TAG,object : CometChat.MessageListener(){
            override fun onTextMessageReceived(p0: TextMessage?) {
                var list = ArrayList(adapter.currentList)
                list.add(p0)
                adapter.submitList(list)
            }

            override fun onMediaMessageReceived(p0: MediaMessage?) {
                super.onMediaMessageReceived(p0)
            }

            override fun onCustomMessageReceived(p0: CustomMessage?) {
                super.onCustomMessageReceived(p0)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        CometChat.removeMessageListener(TAG)
    }

    private fun fetchMessage(){
        val limit =20
        var messageRequest = MessagesRequest.MessagesRequestBuilder().setGUID(bundle.getString("guid")!!).setLimit(limit).build()
        messageRequest.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                Log.e(TAG, "onSuccessGroupMess: $p0" )
                adapter.submitList(p0)
            }

            override fun onError(p0: CometChatException?) {
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        R.id.leave ->{
            leaveGroup()
        }
        else ->{
            super.onOptionsItemSelected(item)
        }
    }

    private fun leaveGroup() : Boolean{
        var left = false
        val GUID =bundle.getString("guid")
        CometChat.leaveGroup(GUID!!,object : CometChat.CallbackListener<String>(){
            override fun onSuccess(p0: String?) {
                this@GroupChatScreenActivity.toast("You left this Group $p0")
                finish()
            }

            override fun onError(p0: CometChatException?) {
                this@GroupChatScreenActivity.toast("Unable to leave the group $p0")
            }

        })
        return left
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_btn -> {
                if(checkInput(binding.createMessage.edtMsg.text.toString())){
                    sendMessage(binding.createMessage.edtMsg.text.toString())
                    binding.createMessage.edtMsg.text = null
                }
            }
        }
    }

    private fun sendMessage(message: String){
        var GUID = bundle.getString("guid")
        var receiverType = CometChatConstants.RECEIVER_TYPE_GROUP
        var textMessage = TextMessage(GUID!!,message,receiverType)
        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(p0: TextMessage?) {
                var list = ArrayList(adapter.currentList)
                list.add(p0)
                adapter.submitList(list)
            }

            override fun onError(p0: CometChatException?) {
                Log.e(TAG, "onError: failed to send $p0")
            }

        })
    }
    private fun checkInput(message:String):Boolean{
        return !(TextUtils.isEmpty(message))
    }

}