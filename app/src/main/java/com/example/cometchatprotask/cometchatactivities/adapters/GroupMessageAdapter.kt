package com.example.cometchatprotask.cometchatactivities.adapters


import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.models.Action
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.MediaMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.cometchatactivities.viewHolders.ActionViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.BaseViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.LeftMessageViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.RightMessageViewHolder

class GroupMessageAdapter: ListAdapter<BaseMessage,RecyclerView.ViewHolder>(comparator) {
    private val RIGHT_TEXT_MESSAGE = 1
    private val LEFT_TEXT_MESSAGE = 2
    private val ACTION_MESSAGE =5
    private val TAG = "GroupMessageAdapter"
    companion object{
        private var viewTypes = 0
        private var comparator = object : DiffUtil.ItemCallback<BaseMessage>(){
            override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var messageView : RecyclerView.ViewHolder? = null
        when(viewTypes){
            RIGHT_TEXT_MESSAGE->{
                messageView = RightMessageViewHolder.create(parent)
            }
            LEFT_TEXT_MESSAGE->{
                messageView = LeftMessageViewHolder.create(parent)
            }
            ACTION_MESSAGE->{
                messageView =  ActionViewHolder.create(parent)
            }
        }
        return messageView!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(viewTypes){
            RIGHT_TEXT_MESSAGE->{
                val rightMessageViewHolder = holder as RightMessageViewHolder
                rightMessageViewHolder.setIsRecyclable(false)
                rightMessageViewHolder.bind(getItem(position) as TextMessage)

            }
            LEFT_TEXT_MESSAGE -> {
                val leftMessageViewHolder = holder as LeftMessageViewHolder
                leftMessageViewHolder.setIsRecyclable(false)
                Log.e(TAG, "onBindViewHolder: ${getItem(position)}", )
                leftMessageViewHolder.bind(getItem(position) as TextMessage)
            }
            ACTION_MESSAGE -> {
                val actionViewHolder = holder as ActionViewHolder
                actionViewHolder.bind(getItem(position) as Action)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItemsType(position)
        return position
    }

    private fun getItemsType(position: Int){
        var message = getItem(position)
        if(message.category == CometChatConstants.CATEGORY_MESSAGE){
            if(message.sender.uid == CometChat.getLoggedInUser().uid){
                viewTypes = RIGHT_TEXT_MESSAGE
                Log.e(TAG, "getItemsType: if called ${message.sender.uid} ${CometChat.getLoggedInUser().uid}", )
            }else{
                viewTypes = LEFT_TEXT_MESSAGE
                Log.e(TAG, "getItemsType: else called ${message.sender.uid} ${CometChat.getLoggedInUser().uid}", )
            }
        }else if(message.category == CometChatConstants.CATEGORY_ACTION){
            viewTypes = ACTION_MESSAGE
        }

    }
}