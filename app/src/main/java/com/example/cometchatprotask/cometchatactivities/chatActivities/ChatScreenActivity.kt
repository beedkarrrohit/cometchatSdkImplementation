package com.example.cometchatprotask.cometchatactivities.chatActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.*
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.adapters.UserChatScreenAdapter
import com.example.cometchatprotask.cometchatactivities.viewModels.ChatScreenViewModel
import com.example.cometchatprotask.databinding.ActivityChatScreenBinding
import com.example.cometchatprotask.databinding.CreateTextMessageLayoutBinding
import com.example.cometchatprotask.utils.Utils
import java.util.*

class ChatScreenActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var chatScreenBinding: ActivityChatScreenBinding
    lateinit var subBinding : CreateTextMessageLayoutBinding
    private  val TAG = "ChatScreenActivity"
    lateinit var bundle : Bundle
    lateinit var chatAdapter : UserChatScreenAdapter
    lateinit var viewModel : ChatScreenViewModel
    val list : MutableList<BaseMessage> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatScreenBinding= ActivityChatScreenBinding.inflate(layoutInflater)
        subBinding= CreateTextMessageLayoutBinding.bind(chatScreenBinding.root)
        setContentView(chatScreenBinding.root)
        setSupportActionBar(chatScreenBinding.chatScreenToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        bundle = intent.extras!!
        viewModel = ViewModelProvider(this).get(ChatScreenViewModel::class.java)
        setupFields()
        fetchMessage()
        viewModel.getMessageList().observe(this, {
            Log.e(TAG, "onCreateListObserver1: ${it[it.size-1]}" )
            chatAdapter.submitList(it.toMutableList())
            chatAdapter.notifyDataSetChanged()
            if(chatAdapter.itemCount > 5) chatScreenBinding.chatscreenRecycler.scrollToPosition(chatAdapter.itemCount -1)
            Log.e(TAG, "onCreateListObserver2: ${it[it.size-1]}" )
        })
    }

    private fun setupFields(){
        chatScreenBinding.toolbarUserName.text = bundle.getString("name")
        setEditTextListener()
        chatScreenBinding.toolbarStatus.text = bundle.getString("status")
        Glide.with(this).load(bundle.getString("avatar"))
                .centerCrop().placeholder(R.drawable.user).into(chatScreenBinding.avatar)
        subBinding.sendBtn.setOnClickListener(this)
        chatAdapter = UserChatScreenAdapter()
        chatScreenBinding.chatscreenRecycler.adapter = chatAdapter

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_btn -> {
                val userid = bundle.getString("ruserid")
                val textMessage = TextMessage(userid!!,subBinding.edtMsg.text.toString(),CometChatConstants.RECEIVER_TYPE_USER)
                subBinding.edtMsg.text = null
                sendMessage(textMessage)
            }
        }
    }

    fun sendMessage(baseMessage: BaseMessage){
        sendMessage(baseMessage as TextMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(p0: TextMessage?) {
                var baseMessage = p0 as BaseMessage
                viewModel.addMessage(baseMessage)
                //if(chatAdapter.itemCount - 1 - (chatScreenBinding.chatscreenRecycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() < 5) scrollToBottom()
            }
            override fun onError(p0: CometChatException?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        addMessageListener()
        addUserListener()
    }

    fun fetchMessage(){
        val uid = bundle.getString("ruserid")
        viewModel.fetchMessage(uid)
    }

    private fun scrollToBottom(){
        Log.e(TAG, "scrollToBottom: called" )
        if(chatAdapter.itemCount > 0){
            Log.e(TAG, "scrollToBottom: if called" )
            chatScreenBinding.chatscreenRecycler.smoothScrollToPosition(chatAdapter.itemCount -1)
            Log.e(TAG, "scrollToBottom: if called 2" )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.one_on_one_chat_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.call -> cometChatCall(CometChatConstants.CALL_TYPE_AUDIO)
            R.id.video_call -> cometChatCall(CometChatConstants.CALL_TYPE_VIDEO)
        }
        return true
    }

    fun cometChatCall(calltype : String){
        var receiverId = bundle.getString("ruserid")
        if(receiverId != null) Utils.initiateCall(this,receiverId,CometChatConstants.RECEIVER_TYPE_USER,calltype)
    }

    private fun addMessageListener() {
        addMessageListener(TAG, object : CometChat.MessageListener() {
            override fun onTextMessageReceived(p0: TextMessage?) {
               if (p0 != null)
                   viewModel.addMessage(p0)
            }

            override fun onMediaMessageReceived(p0: MediaMessage?) {
                super.onMediaMessageReceived(p0)
            }

            override fun onCustomMessageReceived(p0: CustomMessage?) {
                super.onCustomMessageReceived(p0)
            }

            override fun onTypingStarted(p0: TypingIndicator?) {
                super.onTypingStarted(p0)
                if (p0 != null) {
                    setTypingMessage(p0, true)
                }
            }

            override fun onTypingEnded(p0: TypingIndicator?) {
                super.onTypingEnded(p0)
                if (p0 != null) {
                    setTypingMessage(p0, false)
                }

            }

            override fun onMessagesDelivered(p0: MessageReceipt?) {
                super.onMessagesDelivered(p0)
                Log.e(TAG, "onMessagesDelivered: $p0", )
                if (p0 != null) {
                    viewModel.setDeliveryReceipt(p0)
                }
            }

            override fun onMessagesRead(p0: MessageReceipt?) {
                super.onMessagesRead(p0)
                Log.e(TAG, "onMessagesDeliveredRead: $p0", )
                if (p0 != null) viewModel.setReadReceipts(p0)
            }

            override fun onMessageEdited(p0: BaseMessage?) {
                super.onMessageEdited(p0)
            }

            override fun onMessageDeleted(p0: BaseMessage?) {
                super.onMessageDeleted(p0)
            }
        })
    }

    private fun addUserListener(){
        addUserListener(TAG, object : CometChat.UserListener() {
            override fun onUserOnline(p0: User?) {
                super.onUserOnline(p0)
                Log.e(TAG, "onUserOnline: ${p0?.status}")
                chatScreenBinding.toolbarStatus.text = p0?.status
            }

            override fun onUserOffline(p0: User?) {
                super.onUserOffline(p0)
                Log.e(TAG, "onUserOffline: ${p0?.status}")
                chatScreenBinding.toolbarStatus.text = p0?.status
            }
        })
    }

    private fun removemessageListener(){
        removeMessageListener(TAG)
    }

    private fun setEditTextListener(){
        subBinding.edtMsg.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.isNotEmpty()) sendTypingMessage(true) else sendTypingMessage(false)
            }

            override fun afterTextChanged(p0: Editable?) {
                val timer = Timer()
                timer.schedule(object:TimerTask(){
                    override fun run() {
                        sendTypingMessage(false)
                    }

                },2000)
            }

        })
    }

    private fun sendTypingMessage(isTyping : Boolean){
        var id = bundle.getString("ruserid")
        if(isTyping){
            if(bundle.getString("type") == CometChatConstants.CONVERSATION_TYPE_USER){
                startTyping(TypingIndicator(id!!,CometChatConstants.RECEIVER_TYPE_USER))
            }
        }else{
            if(bundle.getString("type") == CometChatConstants.CONVERSATION_TYPE_USER){
                endTyping(TypingIndicator(id!!,CometChatConstants.RECEIVER_TYPE_USER))
            }
        }
    }

    private fun setTypingMessage(typingIndicator: TypingIndicator,isShow: Boolean){
        val rid = bundle.getString("ruserid")
        if(typingIndicator.receiverType.equals(CometChatConstants.RECEIVER_TYPE_USER,ignoreCase = true)){
            if(rid != null && rid.equals(typingIndicator.sender.uid,ignoreCase = true)){
                if(isShow){
                    chatScreenBinding.toolbarStatus.text = "Typing...."
                }else{
                    chatScreenBinding.toolbarStatus.text = bundle.getString("status")
                }
            }
        }
    }
    private fun setDeliveryReceipt(messageReceipt: MessageReceipt){
        val list = chatAdapter.currentList
        Log.e(TAG, "onMessagesDeliveredRRR: ${list[list.size-1]}", )
        for (i in list.indices.reversed()) {
            val baseMessage = list[i]
            if (baseMessage.deliveredAt == 0L) {
                val index = list.indexOf(baseMessage)
                Log.e(TAG, "onMessagesDelivered: ${list[index].deliveredAt}", )
                list[index].deliveredAt = messageReceipt.deliveredAt
                Log.e(TAG, "onMessagesDelivered: ${list[index].deliveredAt}", )
            }
        }
        Log.e(TAG, "onMessagesDeliveredRRR: ${list[list.size-1]}", )
        chatAdapter.submitList(list)
    }

    private fun setReadReceipt(messageReceipt: MessageReceipt){
        val list= chatAdapter.currentList
        for(i in list.indices.reversed()){
            val baseMessage = list[i]
            if(baseMessage.readAt == 0L){
                val index = list.indexOf(baseMessage)
                list[index].readAt = messageReceipt.readAt
            }
        }
    }

    override fun onPause() {
        super.onPause()
        removemessageListener()
        removeUserListener(TAG)
        sendTypingMessage(false)
    }
}