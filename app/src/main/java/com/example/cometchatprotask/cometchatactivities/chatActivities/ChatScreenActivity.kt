package com.example.cometchatprotask.cometchatactivities.chatActivities

import android.content.Intent
import android.Manifest.permission
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.*
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.adapters.UserChatScreenAdapter
import com.example.cometchatprotask.cometchatactivities.bottomSheetFragment.AttachBottomSheet
import com.example.cometchatprotask.cometchatactivities.viewModels.ChatScreenViewModel
import com.example.cometchatprotask.databinding.ActivityChatScreenBinding
import com.example.cometchatprotask.databinding.CreateTextMessageLayoutBinding
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.utils.Utils
import java.io.File
import java.util.*

class ChatScreenActivity : AppCompatActivity(),View.OnClickListener,AttachBottomSheet.BottomSheetListener {
    lateinit var chatScreenBinding: ActivityChatScreenBinding
    lateinit var subBinding : CreateTextMessageLayoutBinding
    private  val TAG = "ChatScreenActivity"
    lateinit var bundle : Bundle
    lateinit var chatAdapter : UserChatScreenAdapter
    lateinit var viewModel : ChatScreenViewModel
    val list : MutableList<BaseMessage> = ArrayList()
    lateinit var attachBottomSheet: AttachBottomSheet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatScreenBinding= ActivityChatScreenBinding.inflate(layoutInflater)
        subBinding= CreateTextMessageLayoutBinding.bind(chatScreenBinding.root)
        setContentView(chatScreenBinding.root)
        setSupportActionBar(chatScreenBinding.chatScreenToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        bundle = intent.extras!!
        attachBottomSheet = AttachBottomSheet()
        viewModel = ViewModelProvider(this).get(ChatScreenViewModel::class.java)
        setupFields()
        fetchMessage()
        viewModel.getMessageList().observe(this, {
            //Log.e(TAG, "onCreateListObserver1: ${it[it.size-1]}" )
            chatAdapter.submitList(it.toMutableList())
            chatAdapter.notifyDataSetChanged()
            if(chatAdapter.itemCount > 5) chatScreenBinding.chatscreenRecycler.scrollToPosition(chatAdapter.itemCount -1)
            //Log.e(TAG, "onCreateListObserver2: ${it[it.size-1]}" )
        })
    }

    private fun setupFields(){
        chatScreenBinding.toolbarUserName.text = bundle.getString("name")
        setEditTextListener()
        chatScreenBinding.toolbarStatus.text = bundle.getString("status")
        Glide.with(this).load(bundle.getString("avatar"))
                .centerCrop().placeholder(R.drawable.user).into(chatScreenBinding.avatar)
        subBinding.apply {
            sendBtn.setOnClickListener(this@ChatScreenActivity)
            buttonAttach.setOnClickListener(this@ChatScreenActivity)
        }
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
            R.id.button_attach ->{
                if(!Utils.hasPermissions(this,permission.READ_EXTERNAL_STORAGE) && !Utils.hasPermissions(this,permission.WRITE_EXTERNAL_STORAGE)){
                    requestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE,permission.READ_EXTERNAL_STORAGE),100)
                }else{
                    attachBottomSheet.show(supportFragmentManager,TAG)
                }

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

    override fun onButtonClicked(s: Int) {
        when(s){
            R.id.send_image ->{
                this.toast("send Image")
                val selectImageIntent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(selectImageIntent,1)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            val selectedImage = data.data
            Log.e(TAG, "onActivityResult: $selectedImage")
            val cursor = selectedImage?.let { contentResolver.query(it,null,null,null,null) }
            cursor!!.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val filepath = cursor.getString(index)
            Log.e(TAG, "onActivityResult: path $filepath")
            var file = File(filepath)
            Log.e(TAG, "onActivityResult: $file", )
            sendMediaMessage(file,CometChatConstants.MESSAGE_TYPE_IMAGE)

        }
    }

    private fun sendMediaMessage(file : File,messageType : String){
        Log.e(TAG, "sendImageMessage: $file", )
        val receiver_uid = bundle.getString("ruserid")
        val receiver_type = CometChatConstants.RECEIVER_TYPE_USER
        var mediaMessage = MediaMessage(receiver_uid,file,messageType,receiver_type)
        sendMediaMessage(mediaMessage, object : CallbackListener<MediaMessage>() {
            override fun onSuccess(p0: MediaMessage?) {
                Log.e(TAG, "onSuccess: p0", )
                if (p0 != null) {
                    var baseMessage = p0 as BaseMessage
                    viewModel.addMessage(baseMessage)
                }
            }

            override fun onError(p0: CometChatException?) {
                Log.e(TAG, "onError: ${p0?.message}", )
            }

        })
    }
}