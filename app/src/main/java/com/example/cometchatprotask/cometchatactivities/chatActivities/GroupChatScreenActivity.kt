package com.example.cometchatprotask.cometchatactivities.chatActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.adapters.GroupMessageAdapter
import com.example.cometchatprotask.cometchatactivities.viewModels.GroupChatScreenViewModel
import com.example.cometchatprotask.databinding.ActivityGroupChatScreenBinding
import com.example.cometchatprotask.databinding.CreateTextMessageLayoutBinding
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.utils.Utils

class GroupChatScreenActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var binding : ActivityGroupChatScreenBinding
    lateinit var subBinding : CreateTextMessageLayoutBinding
    lateinit var bundle: Bundle
    lateinit var adapter: GroupMessageAdapter
    lateinit var viewModel : GroupChatScreenViewModel
    private  val TAG = "GroupChatScreenActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatScreenBinding.inflate(layoutInflater)
        subBinding = CreateTextMessageLayoutBinding.bind(binding.root)
        setContentView(binding.root)
        setSupportActionBar(binding.groupChatToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        bundle = intent.extras!!
        viewModel = ViewModelProvider(this).get(GroupChatScreenViewModel::class.java)
        setupFields()
        fetchMessage()
        viewModel.getMessageList().observe(this,{
            adapter.submitList(it)
            if(adapter.itemCount > 5)binding.groupRecy.scrollToPosition(adapter.itemCount -1)
        })
    }

    private fun setupFields() {
        Glide.with(this).load(bundle.getString("icon")).into(binding.groupIcon)
        binding.groupName.text = bundle.getString("gname")
        adapter = GroupMessageAdapter()
        binding.groupRecy.adapter = adapter
        subBinding.sendBtn.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        CometChat.addMessageListener(TAG,object : CometChat.MessageListener(){
            override fun onTextMessageReceived(p0: TextMessage?) {
                if(p0 != null) viewModel.addMessage(p0)
            }

            override fun onMediaMessageReceived(p0: MediaMessage?) {
                super.onMediaMessageReceived(p0)
            }

            override fun onCustomMessageReceived(p0: CustomMessage?) {
                super.onCustomMessageReceived(p0)
            }

            override fun onTypingStarted(p0: TypingIndicator?) {
                super.onTypingStarted(p0)
            }

            override fun onTypingEnded(p0: TypingIndicator?) {
                super.onTypingEnded(p0)
            }

            override fun onMessagesDelivered(p0: MessageReceipt?) {
                super.onMessagesDelivered(p0)
            }

            override fun onMessagesRead(p0: MessageReceipt?) {
                super.onMessagesRead(p0)
            }

            override fun onMessageEdited(p0: BaseMessage?) {
                super.onMessageEdited(p0)
            }

            override fun onMessageDeleted(p0: BaseMessage?) {
                super.onMessageDeleted(p0)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        CometChat.removeMessageListener(TAG)
    }

    private fun fetchMessage(){
        val limit =20
        val guid = bundle.getString("guid")
        viewModel.fetchMessage(guid)
        /*var messageRequest = MessagesRequest.MessagesRequestBuilder().setGUID(bundle.getString("guid")!!).setLimit(limit).build()
        messageRequest.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                Log.e(TAG, "onSuccessGroupMess: $p0" )
                adapter.submitList(p0)
            }

            override fun onError(p0: CometChatException?) {
            }

        })*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId) {
             R.id.leave -> {
                leaveGroup()
            }
            R.id.video_call_group -> cometChatGroupCall(CometChatConstants.CALL_TYPE_VIDEO)
            R.id.group_call -> cometChatGroupCall(CometChatConstants.CALL_TYPE_AUDIO)
        }
        return true
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
                if(checkInput(subBinding.edtMsg.text.toString())){
                    sendMessage(subBinding.edtMsg.text.toString())
                    subBinding.edtMsg.text = null
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
                if(p0 !=null) viewModel.addMessage(p0)
            }

            override fun onError(p0: CometChatException?) {
                Log.e(TAG, "onError: failed to send $p0")
            }

        })
    }
    private fun checkInput(message:String):Boolean{
        return !(TextUtils.isEmpty(message))
    }

    private fun cometChatGroupCall(callType : String?){
        val groupId = bundle.getString("guid")
        if(groupId != null) Utils.initiateCall(this,groupId,CometChatConstants.RECEIVER_TYPE_GROUP,callType)
    }


}