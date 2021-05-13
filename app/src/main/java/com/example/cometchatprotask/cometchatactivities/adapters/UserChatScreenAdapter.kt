package com.example.cometchatprotask.cometchatactivities.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.*
import com.cometchat.pro.models.Action
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.viewHolders.*


class UserChatScreenAdapter : ListAdapter<BaseMessage,RecyclerView.ViewHolder>(comparator) {
    private val TAG = "UserChatScreenAdapter"
    companion object{
        private var comparator = object : DiffUtil.ItemCallback<BaseMessage>(){
            override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem.readAt == newItem.readAt
            }

            override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem == newItem
            }

        }
        //message type Message
        private val RIGHT_TEXT_MESSAGE = 1
        private val LEFT_TEXT_MESSAGE = 2
        private val RIGHT_IMAGE_MESSAGE = 3
        private val LEFT_IMAGE_MESSAGE = 4
        private val RIGHT_VIDEO_MESSAGE =5
        private val LEFT_VIDEO_MESSAGE = 6
        private val RIGHT_AUDIO_MESSAGE = 7
        private val LEFT_AUDIO_MESSAGE = 8
        private val RIGHT_FILE_MESSAGE =9
        private val LEFT_FILE_MESSAGE = 10
        private val RIGHT_CUSTOM_MESSAGE=11
        private val LEFT_CUSTOM_MESSAGE=12

        //message type call
        private val ACTION_CALL =13
        //message type custom
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.e(TAG, "itemViewType $viewType" )
        return when(viewType){
            RIGHT_TEXT_MESSAGE -> RightMessageViewHolder.create(parent)
            LEFT_TEXT_MESSAGE -> LeftMessageViewHolder.create(parent)
            ACTION_CALL -> ActionViewHolder.create(parent)
            RIGHT_IMAGE_MESSAGE-> RightImageMessageViewHolder.create(parent)
            LEFT_IMAGE_MESSAGE -> LeftImageMessageViewHolder.create(parent)
            else -> ActionViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val baseMessage = getItem(position)
        Log.e(TAG, "onBindViewHolder: ${currentList[position]}", )
        Log.e(TAG, "onBindViewHolder: ${baseMessage.deliveredAt}", )
        when(holder.itemViewType){
            RIGHT_TEXT_MESSAGE -> {
                val rightMessageViewHolder = holder as RightMessageViewHolder
                rightMessageViewHolder.bind(baseMessage as TextMessage)
            }
            LEFT_TEXT_MESSAGE ->{
                val leftMessageViewHolder = holder as LeftMessageViewHolder
                leftMessageViewHolder.bind(baseMessage as TextMessage)
            }
            ACTION_CALL ->{
                val actionViewHolder = holder as ActionViewHolder
                actionViewHolder.bind(baseMessage)
            }
            RIGHT_IMAGE_MESSAGE -> {
                val rightImageMessageViewHolder = holder as RightImageMessageViewHolder
                rightImageMessageViewHolder.bind(baseMessage)
            }
            LEFT_IMAGE_MESSAGE -> {
                val leftImageMessageViewHolder = holder as LeftImageMessageViewHolder
                leftImageMessageViewHolder.bind(baseMessage)
            }
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<BaseMessage>,
        currentList: MutableList<BaseMessage>
    ) {
        previousList == currentList
    }


    override fun getItemViewType(position: Int): Int {
        return getItemViewTypes(position)
    }

    fun getItemViewTypes(position: Int) : Int  {
        val message = getItem(position)
        return when(message.category){
            CometChatConstants.CATEGORY_MESSAGE->{
                when(message.type){
                    CometChatConstants.MESSAGE_TYPE_TEXT-> return if(message.sender.uid == getLoggedInUser().uid) RIGHT_TEXT_MESSAGE else LEFT_TEXT_MESSAGE
                    CometChatConstants.MESSAGE_TYPE_IMAGE-> return if(message.sender.uid == getLoggedInUser().uid) RIGHT_IMAGE_MESSAGE else LEFT_IMAGE_MESSAGE
                    /*CometChatConstants.MESSAGE_TYPE_VIDEO->{}
                    CometChatConstants.MESSAGE_TYPE_FILE->{}
                    CometChatConstants.MESSAGE_TYPE_AUDIO->{}
                    CometChatConstants.MESSAGE_TYPE_CUSTOM->{}*/
                    else -> -1
                }
            }
            CometChatConstants.CATEGORY_CALL->{
                return ACTION_CALL
            }else-> -1
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


}