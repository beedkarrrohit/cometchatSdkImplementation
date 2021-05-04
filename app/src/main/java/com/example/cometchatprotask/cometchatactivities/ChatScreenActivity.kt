package com.example.cometchatprotask.cometchatactivities

import android.inputmethodservice.KeyboardView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toolbar
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
import com.example.cometchatprotask.cometchatactivities.adapters.UserChatScreenAdapter
import com.example.cometchatprotask.databinding.ActivityChatScreenBinding
import java.util.ArrayList

class ChatScreenActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var chatScreenBinding: ActivityChatScreenBinding
    private  val TAG = "ChatScreenActivity"
    lateinit var bundle : Bundle
    lateinit var chatAdapter : UserChatScreenAdapter
    val list : MutableList<BaseMessage> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatScreenBinding= ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(chatScreenBinding.root)
        setSupportActionBar(chatScreenBinding.chatScreenToolbar)
        bundle = intent.extras!!
        setupFields()
        fetchMessage()
    }

    private fun setupFields(){
        chatScreenBinding.toolbarUserName.text = bundle.getString("name")
        chatScreenBinding.toolbarStatus.text = bundle.getString("status")
        chatScreenBinding.useid.text = bundle.getString("ruserid")
        Glide.with(this).load(bundle.getString("avatar"))
                .centerCrop().placeholder(R.drawable.user).into(chatScreenBinding.avatar)
        chatScreenBinding.createMessage.sendBtn.setOnClickListener(this)
        chatAdapter = UserChatScreenAdapter()
        chatScreenBinding.chatscreenRecycler.adapter = chatAdapter
        chatScreenBinding.chatscreenRecycler.addOnLayoutChangeListener(object : View.OnLayoutChangeListener{
            override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                if(p4 < p8){
                    chatScreenBinding.chatscreenRecycler.smoothScrollToPosition(chatAdapter.itemCount-1)
                }
            }

        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_btn -> {
                val userid = bundle.getString("ruserid")
                val textMessage = TextMessage(userid!!,chatScreenBinding.createMessage.edtMsg.text.toString(),CometChatConstants.RECEIVER_TYPE_USER)
                chatScreenBinding.createMessage.edtMsg.text = null
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
                scrollToBottom()
            }
            override fun onError(p0: CometChatException?) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        CometChat.addMessageListener(TAG,object:CometChat.MessageListener(){
            override fun onTextMessageReceived(p0: TextMessage?) {
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
        })
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
                scrollToBottom()
            }

            override fun onError(p0: CometChatException?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun scrollToBottom(){
        if(chatAdapter != null && chatAdapter.itemCount > 0){
            chatScreenBinding.chatscreenRecycler.scrollToPosition(chatAdapter.itemCount -1)
        }
    }
}