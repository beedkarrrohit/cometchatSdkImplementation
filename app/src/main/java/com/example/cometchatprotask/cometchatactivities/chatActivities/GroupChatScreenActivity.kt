package com.example.cometchatprotask.cometchatactivities.chatActivities

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.cometchatprotask.cometchatactivities.bottomSheetFragment.AttachBottomSheet
import com.example.cometchatprotask.cometchatactivities.viewModels.GroupChatScreenViewModel
import com.example.cometchatprotask.databinding.ActivityGroupChatScreenBinding
import com.example.cometchatprotask.databinding.CreateTextMessageLayoutBinding
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.utils.Utils
import java.io.File

class GroupChatScreenActivity : AppCompatActivity(),View.OnClickListener,AttachBottomSheet.BottomSheetListener {
    lateinit var binding : ActivityGroupChatScreenBinding
    lateinit var subBinding : CreateTextMessageLayoutBinding
    lateinit var bundle: Bundle
    lateinit var adapter: GroupMessageAdapter
    lateinit var viewModel : GroupChatScreenViewModel
    private  val TAG = "GroupChatScreenActivity"
    lateinit var attachBottomSheet: AttachBottomSheet
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
        attachBottomSheet = AttachBottomSheet()
        setupFields()
        fetchMessage()
        viewModel.getMessageList().observe(this,{
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
            if(adapter.itemCount > 5)binding.groupRecy.scrollToPosition(adapter.itemCount -1)
        })
    }

    private fun setupFields() {
        Glide.with(this).load(bundle.getString("icon")).into(binding.groupIcon)
        binding.groupName.text = bundle.getString("gname")
        adapter = GroupMessageAdapter()
        binding.groupRecy.adapter = adapter
        subBinding.apply {
            sendBtn.setOnClickListener(this@GroupChatScreenActivity)
            buttonAttach.setOnClickListener(this@GroupChatScreenActivity)
        }

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
            R.id.button_attach -> {
                if(!Utils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE) && !Utils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),100)
                    }
                }else{
                    attachBottomSheet.show(supportFragmentManager,TAG)
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

    override fun onButtonClicked(id: Int) {
        when(id){
            R.id.send_image ->{
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent,100)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            100 -> {
                if(resultCode == RESULT_OK && data != null){
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
        }
    }

    private fun sendMediaMessage(file : File,messageType : String){
        Log.e(TAG, "sendImageMessage: $file", )
        val receiver_uid = bundle.getString("guid")
        val receiver_type = CometChatConstants.RECEIVER_TYPE_GROUP
        var mediaMessage = MediaMessage(receiver_uid,file,messageType,receiver_type)
        CometChat.sendMediaMessage(mediaMessage, object : CometChat.CallbackListener<MediaMessage>() {
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