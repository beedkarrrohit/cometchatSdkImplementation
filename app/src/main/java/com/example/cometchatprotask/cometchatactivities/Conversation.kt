package com.example.cometchatprotask.cometchatactivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat.*
import com.cometchat.pro.core.ConversationsRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.helpers.CometChatHelper
import com.cometchat.pro.models.*
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.User
import com.example.cometchatprotask.cometchatactivities.adapters.ConversationListAdapter
import com.example.cometchatprotask.cometchatactivities.adapters.OnClickInterface
import com.example.cometchatprotask.cometchatactivities.chatActivities.ChatScreenActivity
import com.example.cometchatprotask.cometchatactivities.chatActivities.GroupChatScreenActivity
import com.example.cometchatprotask.cometchatactivities.viewModels.ConversationViewModel
import com.example.cometchatprotask.databinding.FragmentConversationBinding
import com.example.cometchatprotask.databinding.RecyclerBinding


class Conversation : Fragment(),OnClickInterface {
    lateinit var binding : FragmentConversationBinding
    lateinit var subBinding : RecyclerBinding
    lateinit var adapter : ConversationListAdapter
    private val TAG = "Conversation"
    lateinit var viewModel : ConversationViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentConversationBinding.inflate(layoutInflater)
        subBinding = RecyclerBinding.bind(binding.root)
        viewModel = ViewModelProvider(this).get(ConversationViewModel::class.java)
        adapter = ConversationListAdapter(this)
        subBinding.recycler.adapter = adapter
        getConversationList()
        viewModel.getList().observe(viewLifecycleOwner,{
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        Log.e(TAG, "selfId: $uid" )
        return binding.root
    }

    private fun getConversationList(){
        var conversationsRequest : ConversationsRequest? = null
        var limit = 30
        conversationsRequest = ConversationsRequest.ConversationsRequestBuilder().setLimit(limit).build()
        conversationsRequest?.fetchNext(object : CallbackListener<List<Conversation>>() {
            override fun onSuccess(p0: List<Conversation>?) {
                if (p0 != null) {
                    viewModel.getConversations(p0)
                    Log.e(TAG, "onSuccess: $p0", )
                } else {
                    binding.noCon.visibility = View.VISIBLE
                }
            }
            override fun onError(p0: CometChatException?) {
            }

        })
    }
    override fun onItemClick(position: Int) {
        var conversation = adapter.currentList[position]
        Log.e(TAG, "onItemClick: $conversation" )
        var intent: Intent
        if(conversation.conversationType == CometChatConstants.CONVERSATION_TYPE_USER){
            intent = Intent(activity, ChatScreenActivity::class.java)
        }else{
            intent=Intent(activity, GroupChatScreenActivity::class.java)
        }
        intent.putExtras(createIntentBundle(conversation))
        startActivity(intent)
    }

    fun createIntentBundle(conversation: Conversation):Bundle{
        val bundle = Bundle()
        when(conversation.conversationType){
            CometChatConstants.CONVERSATION_TYPE_USER ->{
                var user = conversation.conversationWith as User
                bundle.putString("name",user.name)
                bundle.putString("status",user.status)
                bundle.putString("avatar",user.avatar)
                bundle.putString("ruserid",user.uid)
                bundle.putString("selfuid", uid.uid)
                bundle.putString("type",conversation.conversationType)
            }
            CometChatConstants.CONVERSATION_TYPE_GROUP-> {
                var group = conversation.conversationWith as Group
                bundle.putString("gname",group.name)
                bundle.putString("guid",group.guid)
                bundle.putString("icon",group.icon)
                bundle.putInt("members",group.membersCount)
                bundle.putString("selfuid", uid.uid)
                bundle.putString("type",conversation.conversationType)
            }
        }
        return bundle
    }

    companion object{
        var uid: User = getLoggedInUser()
    }

    override fun onResume() {
        super.onResume()
        getConversationList()
        conversationListener()
    }

    override fun onPause() {
        super.onPause()
        removeConversationListener()
    }

    override fun onStart() {
        super.onStart()
        getConversationList()
    }

    private fun conversationListener(){
        addMessageListener(TAG,object: MessageListener(){
            override fun onTextMessageReceived(p0: TextMessage?) {
                if(p0!=null)updateConversationList(p0)
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
        addGroupListener(TAG,object : GroupListener(){
            override fun onGroupMemberJoined(p0: Action?, p1: User?, p2: Group?) {
                super.onGroupMemberJoined(p0, p1, p2)
            }

            override fun onGroupMemberLeft(p0: Action?, p1: User?, p2: Group?) {
                super.onGroupMemberLeft(p0, p1, p2)
            }

            override fun onGroupMemberKicked(p0: Action?, p1: User?, p2: User?, p3: Group?) {
                super.onGroupMemberKicked(p0, p1, p2, p3)
            }

            override fun onGroupMemberBanned(p0: Action?, p1: User?, p2: User?, p3: Group?) {
                super.onGroupMemberBanned(p0, p1, p2, p3)
            }

            override fun onGroupMemberUnbanned(p0: Action?, p1: User?, p2: User?, p3: Group?) {
                super.onGroupMemberUnbanned(p0, p1, p2, p3)
            }

            override fun onGroupMemberScopeChanged(
                p0: Action?,
                p1: User?,
                p2: User?,
                p3: String?,
                p4: String?,
                p5: Group?
            ) {
                super.onGroupMemberScopeChanged(p0, p1, p2, p3, p4, p5)
            }

            override fun onMemberAddedToGroup(p0: Action?, p1: User?, p2: User?, p3: Group?) {
                super.onMemberAddedToGroup(p0, p1, p2, p3)
            }
        })
    }

    private fun updateConversationList(message : BaseMessage) {
        val conversation = CometChatHelper.getConversationFromMessage(message)
        viewModel.update(conversation)
    }

    private fun removeConversationListener(){
        removeMessageListener(TAG)
        removeGroupListener(TAG)
    }
}