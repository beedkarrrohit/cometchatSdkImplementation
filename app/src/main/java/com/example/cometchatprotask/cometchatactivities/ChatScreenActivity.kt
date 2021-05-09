package com.example.cometchatprotask.cometchatactivities

import android.content.Intent
import android.inputmethodservice.KeyboardView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.adapters.UserChatScreenAdapter
import com.example.cometchatprotask.databinding.ActivityChatScreenBinding
import com.example.cometchatprotask.databinding.CreateTextMessageLayoutBinding
import com.example.cometchatprotask.utils.Utils
import kotlinx.coroutines.coroutineScope
import java.util.*

class ChatScreenActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var chatScreenBinding: ActivityChatScreenBinding
    lateinit var subBinding : CreateTextMessageLayoutBinding
    private  val TAG = "ChatScreenActivity"
    lateinit var bundle : Bundle
    lateinit var chatAdapter : UserChatScreenAdapter
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
        setupFields()
        fetchMessage()
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
        chatScreenBinding.chatscreenRecycler.addOnLayoutChangeListener(object : View.OnLayoutChangeListener{
            override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                if(p4 < p8){
                    //chatScreenBinding.chatscreenRecycler.smoothScrollToPosition(chatAdapter.itemCount-1)
                }
            }
        })
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
        CometChat.sendMessage(baseMessage as TextMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(p0: TextMessage?) {
                var baseMessage = p0 as BaseMessage
                list.add(baseMessage)
                Log.e(TAG, "BaseMessage $baseMessage")
                var li: MutableList<BaseMessage> = ArrayList(chatAdapter.currentList)
                li.add(baseMessage)
                chatAdapter.submitList(li)
                if(chatAdapter.itemCount - 1 - (chatScreenBinding.chatscreenRecycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() < 5) scrollToBottom()
            }
            override fun onError(p0: CometChatException?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        addMessageListener()
        addUserListener()
        var v1 = CometChat.isFeatureEnabled("core.chat.one-on-one.enabled",object: CometChat.CallbackListener<Boolean>(){
            override fun onSuccess(p0: Boolean?) {

            }

            override fun onError(p0: CometChatException?) {
                TODO("Not yet implemented")
            }

        })
        Log.e(TAG, "onResume: $v1")
    }

    fun fetchMessage(){
        var messagesRequest: MessagesRequest?
        val limit = 30
        val uid = bundle.getString("ruserid")
        messagesRequest= MessagesRequest.MessagesRequestBuilder().setUID(uid!!).setLimit(limit).build()
        messagesRequest?.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                Log.e(TAG, "submitListN: $p0")
                chatAdapter.submitList(p0 as MutableList<BaseMessage>)
                //scrollToBottom()
            }

            override fun onError(p0: CometChatException?) {
                TODO("Not yet implemented")
            }

        })
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
            R.id.call -> cometChatCall()
        }
        return true
    }

    fun cometChatCall(){
        var receiverId = bundle.getString("ruserid")
        /*val intent = Intent(this,CallingScreen::class.java)
        intent.putExtra("rid",recieverId)
        intent.putExtra("type",CometChatConstants.RECEIVER_TYPE_USER)
        intent.putExtra("calltype",CometChatConstants.CALL_TYPE_AUDIO)
        startActivity(intent)*/
        if(receiverId != null) Utils.initiateCall(this,receiverId,CometChatConstants.RECEIVER_TYPE_USER,CometChatConstants.CALL_TYPE_AUDIO)
    }

    private fun addMessageListener() {
        CometChat.addMessageListener(TAG,object: CometChat.MessageListener(){
            override fun onTextMessageReceived(p0: TextMessage?) {
                super.onTextMessageReceived(p0)
                var list = ArrayList(chatAdapter.currentList)
                list.add(p0)
                chatAdapter.submitList(list)
            }

            override fun onMediaMessageReceived(p0: MediaMessage?) {
                super.onMediaMessageReceived(p0)
            }

            override fun onCustomMessageReceived(p0: CustomMessage?) {
                super.onCustomMessageReceived(p0)
            }

            override fun onTypingStarted(p0: TypingIndicator?) {
                super.onTypingStarted(p0)
                if(p0 != null){
                    setTypingMessage(p0,true)
                }
            }

            override fun onTypingEnded(p0: TypingIndicator?) {
                super.onTypingEnded(p0)
                if(p0 != null){
                    setTypingMessage(p0,false)
                }

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

    private fun addUserListener(){
        CometChat.addUserListener(TAG, object : CometChat.UserListener() {
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
        CometChat.removeMessageListener(TAG)
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
                CometChat.startTyping(TypingIndicator(id!!,CometChatConstants.RECEIVER_TYPE_USER))
            }
        }else{
            if(bundle.getString("type") == CometChatConstants.CONVERSATION_TYPE_USER){
                CometChat.endTyping(TypingIndicator(id!!,CometChatConstants.RECEIVER_TYPE_USER))
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

    override fun onPause() {
        super.onPause()
        removemessageListener()
        CometChat.removeUserListener(TAG)
        sendTypingMessage(false)
    }
}