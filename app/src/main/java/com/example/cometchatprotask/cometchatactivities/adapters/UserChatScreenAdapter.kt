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
import com.cometchat.pro.models.Action
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.viewHolders.ActionViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.BaseViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.LeftMessageViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.RightMessageViewHolder


class UserChatScreenAdapter : ListAdapter<BaseMessage,RecyclerView.ViewHolder>(comparator) {
    //message type Mesaage
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
    private val ACTION_AUDIO_CALL = 6
    private val ACTION_VIDEO_CALL = 7
    //message type custom


    private val TAG = "UserChatScreenAdapter"
    companion object{
        var itemViewType : Int = 0
        private var comparator = object : DiffUtil.ItemCallback<BaseMessage>(){
            override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {

                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var messageViewHolder : BaseViewHolder? = null
        Log.e(TAG, "itemViewType $viewType" )
        Log.e(TAG, "itemViewTypes: $itemViewType")
        when(itemViewType){
            RIGHT_TEXT_MESSAGE -> {
                messageViewHolder = RightMessageViewHolder.create(parent)
            }
            LEFT_TEXT_MESSAGE -> {
                messageViewHolder = LeftMessageViewHolder.create(parent)
            }

            ACTION_AUDIO_CALL ->{
                messageViewHolder = ActionViewHolder.create(parent)
            }ACTION_VIDEO_CALL ->{
            messageViewHolder = ActionViewHolder.create(parent)
        }
        }
        return messageViewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(itemViewType){
            RIGHT_TEXT_MESSAGE -> {
                val rightMessageViewHolder = holder as RightMessageViewHolder
                var current = getItem(position)
                var tm = current as TextMessage

                Log.e(TAG, "onBindViewHolder: ${tm.text}, $position ${rightMessageViewHolder.adapterPosition}", )
                rightMessageViewHolder.bind(tm)
            }
            LEFT_TEXT_MESSAGE ->{
                val leftMessageViewHolder = holder as LeftMessageViewHolder
                leftMessageViewHolder.bind(getItem(position) as TextMessage)
            }

            ACTION_AUDIO_CALL -> {
                val actionViewHolder = holder as ActionViewHolder
                actionViewHolder.bind(getItem(position) as Call)
            }
            ACTION_VIDEO_CALL->{
                val actionViewHolder = holder as ActionViewHolder
                actionViewHolder.bind(getItem(position) as Call)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItemViewTypes(position)
        return position
    }

    fun getItemViewTypes(position: Int)  {
        val message = getItem(position)
        when(message.category){
            CometChatConstants.CATEGORY_MESSAGE->{
                when(message.type){
                    CometChatConstants.MESSAGE_TYPE_TEXT->{
                        if(message.sender.uid ==  CometChat.getLoggedInUser().uid) itemViewType = RIGHT_TEXT_MESSAGE else itemViewType = LEFT_TEXT_MESSAGE
                    }
                    CometChatConstants.MESSAGE_TYPE_IMAGE->{}
                    CometChatConstants.MESSAGE_TYPE_VIDEO->{}
                    CometChatConstants.MESSAGE_TYPE_FILE->{}
                    CometChatConstants.MESSAGE_TYPE_AUDIO->{}
                    CometChatConstants.MESSAGE_TYPE_CUSTOM->{}
                }
            }
            CometChatConstants.CATEGORY_CUSTOM->{}
            CometChatConstants.CATEGORY_CALL->{
                when(message.type){
                    CometChatConstants.CALL_TYPE_AUDIO->{
                        itemViewType = ACTION_AUDIO_CALL
                    }
                    CometChatConstants.CALL_TYPE_VIDEO->{itemViewType = ACTION_VIDEO_CALL }
                }
            }
            CometChatConstants.CATEGORY_ACTION->{
                when(message.type){
                    CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER->{}
                    CometChatConstants.ActionKeys.ACTION_TYPE_GROUP->{}
                }
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


}