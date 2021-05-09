package com.example.cometchatprotask.cometchatactivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.ConversationsRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.cometchatprotask.cometchatactivities.adapters.ConversationListAdapter
import com.example.cometchatprotask.cometchatactivities.adapters.OnClickInterface
import com.example.cometchatprotask.databinding.FragmentConversationBinding
import com.example.cometchatprotask.databinding.RecyclerBinding


class Conversation : Fragment(),OnClickInterface {
    lateinit var binding : FragmentConversationBinding
    lateinit var subBinding : RecyclerBinding
    lateinit var adapter : ConversationListAdapter
    private val TAG = "Conversation"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentConversationBinding.inflate(layoutInflater)
        subBinding = RecyclerBinding.bind(binding.root)
        adapter = ConversationListAdapter(this)
        subBinding.recycler.adapter = adapter
        getConversationList()
        Log.e(TAG, "selfId: $uid" )
        return binding.root
    }

    private fun getConversationList(){
        var conversationsRequest : ConversationsRequest? = null
        var limit = 30
        conversationsRequest = ConversationsRequest.ConversationsRequestBuilder().setLimit(limit).build()
        conversationsRequest?.fetchNext(object : CometChat.CallbackListener<List<Conversation>>() {
            override fun onSuccess(p0: List<Conversation>?) {
                if (p0 != null) {
                    Log.e(TAG, "onSuccess: $p0", )
                    binding.noCon.visibility = View.GONE
                    adapter.submitList(p0)
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
            intent=Intent(activity,GroupChatScreenActivity::class.java)
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
        var uid: User = CometChat.getLoggedInUser()
    }

    override fun onResume() {
        super.onResume()
        getConversationList()
    }
}