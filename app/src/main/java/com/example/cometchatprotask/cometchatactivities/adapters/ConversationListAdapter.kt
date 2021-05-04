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
import com.cometchat.pro.models.Conversation
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.TextMessage
import com.cometchat.pro.models.User
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
            if(conversation.conversationType == CometChatConstants.CONVERSATION_TYPE_GROUP){
                if(conversation.lastMessage.type == CometChatConstants.MESSAGE_TYPE_TEXT){
                    val lastmessage = conversation.lastMessage as TextMessage
                    binding.status.text = lastmessage.text
                }
                var group = conversation.conversationWith as Group
                binding.usersName.text = group.name
                Glide.with(itemView).load(group.icon).into(binding.avatar)
            }else{
                if(conversation.lastMessage.type == CometChatConstants.MESSAGE_TYPE_TEXT){
                    val lastMessage = conversation.lastMessage as TextMessage
                    binding.status.text = lastMessage.text
                }
                var user = conversation.conversationWith as User
                binding.usersName.text = user.name
                Glide.with(itemView).load(user.avatar).into(binding.avatar)
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