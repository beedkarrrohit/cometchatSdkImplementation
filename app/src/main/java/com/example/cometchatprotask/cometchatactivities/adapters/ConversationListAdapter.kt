package com.example.cometchatprotask.cometchatactivities.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.models.*
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.FragmentConversationBinding
import com.example.cometchatprotask.databinding.RecyclerItemRowBinding

class ConversationListAdapter(private val onClickInterface: OnClickInterface) : ListAdapter<Conversation,ConversationListAdapter.ConversationViewHolder>(comparator){
    class ConversationViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val TAG = "ConversationListAdapter"

        companion object{
            lateinit var binding:RecyclerItemRowBinding
            fun create(parent: ViewGroup): ConversationViewHolder{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_row,parent,false)
                binding = RecyclerItemRowBinding.bind(view)
                return ConversationViewHolder(view)
            }
        }
        fun bind(conversation: Conversation,onClickInterface: OnClickInterface){
            Log.d(TAG, "bind: $conversation")
            when(conversation.conversationType){
                CometChatConstants.CONVERSATION_TYPE_GROUP ->{
                    if(conversation.lastMessage != null){
                        when(conversation.lastMessage.category){
                            CometChatConstants.CATEGORY_ACTION ->{
                                when(conversation.lastMessage.type){
                                    CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER->{binding.status.text = (conversation.lastMessage as Action).message}
                                    CometChatConstants.ActionKeys.ACTION_TYPE_MESSAGE->{}
                                }
                            }CometChatConstants.CATEGORY_MESSAGE -> {
                            when(conversation.lastMessage.type){
                                CometChatConstants.MESSAGE_TYPE_TEXT ->{binding.status.text =(conversation.lastMessage as TextMessage).text}
                                CometChatConstants.MESSAGE_TYPE_AUDIO ->{}
                                CometChatConstants.MESSAGE_TYPE_IMAGE ->{}
                                CometChatConstants.MESSAGE_TYPE_VIDEO->{}
                                CometChatConstants.MESSAGE_TYPE_FILE->{}
                            }
                        }CometChatConstants.CATEGORY_CALL ->{
                            when(conversation.lastMessage.type){
                                CometChatConstants.CALL_TYPE_AUDIO->{}
                                CometChatConstants.CALL_TYPE_VIDEO->{}
                            }
                        }CometChatConstants.CATEGORY_CUSTOM->{}
                        }
                    }else{
                        binding.status.text = "Tap to start the conversation"
                    }
                    var group = conversation.conversationWith as Group
                    binding.usersName.text = group.name
                    Glide.with(itemView).load(group.icon).placeholder(R.drawable.user).into(binding.avatar)
                }

                CometChatConstants.CONVERSATION_TYPE_USER ->{
                    when(conversation.lastMessage.category){
                        CometChatConstants.CATEGORY_MESSAGE -> {
                            when(conversation.lastMessage.type){
                                CometChatConstants.MESSAGE_TYPE_TEXT ->{binding.status.text =(conversation.lastMessage as TextMessage).text}
                                CometChatConstants.MESSAGE_TYPE_AUDIO ->{}
                                CometChatConstants.MESSAGE_TYPE_IMAGE ->{}
                                CometChatConstants.MESSAGE_TYPE_VIDEO->{}
                                CometChatConstants.MESSAGE_TYPE_FILE->{}
                            }
                        }CometChatConstants.CATEGORY_CALL ->{
                        when(conversation.lastMessage.type){
                            CometChatConstants.CALL_TYPE_AUDIO->{binding.status.text =(conversation.lastMessage as Call).callStatus}
                            CometChatConstants.CALL_TYPE_VIDEO->{}
                        }
                    }CometChatConstants.CATEGORY_CUSTOM->{}
                    }
                    binding.usersName.text =(conversation.conversationWith as User).name
                    Glide.with(itemView).load((conversation.conversationWith as User).avatar).placeholder(R.drawable.user)
                        .into(binding.avatar)
                }
            }

            binding.itemRow.setOnClickListener {
                onClickInterface.onItemClick(adapterPosition)
            }
        }
    }
    companion object{
        private val comparator = object: DiffUtil.ItemCallback<Conversation>(){
            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
                return oldItem.conversationId == newItem.conversationId
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position),onClickInterface)
    }
}