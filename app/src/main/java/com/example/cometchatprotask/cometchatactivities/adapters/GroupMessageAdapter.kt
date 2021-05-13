package com.example.cometchatprotask.cometchatactivities.adapters


import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.*
import com.cometchat.pro.models.Action
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.MediaMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.cometchatactivities.viewHolders.*

class GroupMessageAdapter: ListAdapter<BaseMessage,RecyclerView.ViewHolder>(comparator) {
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
    private val ACTION_CALL = 13
    //message type action
    private val ACTION_GROUP_MEMBER =14
    private val ACTION_GROUP_MESSAGE = 15
    //message type custom
    private val CUSTOM_MESSAGE = 16

    private val TAG = "GroupMessageAdapter"
    companion object{
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
        return when(viewType){
            RIGHT_TEXT_MESSAGE-> RightMessageViewHolder.create(parent)
            LEFT_TEXT_MESSAGE-> LeftMessageViewHolder.create(parent)
            ACTION_GROUP_MEMBER-> ActionViewHolder.create(parent)
            ACTION_CALL-> ActionViewHolder.create(parent)
            RIGHT_IMAGE_MESSAGE -> RightImageMessageViewHolder.create(parent)
            LEFT_IMAGE_MESSAGE->LeftImageMessageViewHolder.create(parent)
            else -> ActionViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val baseMessage = getItem(position)
        when(holder.itemViewType){
            RIGHT_TEXT_MESSAGE->{
                val rightMessageViewHolder = holder as RightMessageViewHolder
                rightMessageViewHolder.bind(baseMessage as TextMessage)
            }
            LEFT_TEXT_MESSAGE -> {
                val leftMessageViewHolder = holder as LeftMessageViewHolder
                Log.e(TAG, "onBindViewHolder: ${getItem(position)}", )
                leftMessageViewHolder.bind(baseMessage as TextMessage)
            }
            ACTION_GROUP_MEMBER,ACTION_CALL->{
                val actionViewHolder = holder as ActionViewHolder
                actionViewHolder.bind(baseMessage)
            }
            RIGHT_IMAGE_MESSAGE->{
                val rightImageMessageViewHolder = holder as RightImageMessageViewHolder
                rightImageMessageViewHolder.bind(baseMessage)
            }
            LEFT_IMAGE_MESSAGE->{
                val leftImageMessageViewHolder = holder as LeftImageMessageViewHolder
                leftImageMessageViewHolder.bind(baseMessage)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItemsType(position)
    }

    private fun getItemsType(position: Int) : Int {
        var message = getItem(position)
        return when(message.category){
            CometChatConstants.CATEGORY_MESSAGE->{
                when(message.type){
                    CometChatConstants.MESSAGE_TYPE_TEXT->{
                        return if(message.sender.uid == getLoggedInUser().uid){
                            RIGHT_TEXT_MESSAGE
                        }else  LEFT_TEXT_MESSAGE
                    }
                    CometChatConstants.MESSAGE_TYPE_IMAGE->{
                        return if (message.sender.uid == getLoggedInUser().uid){
                            RIGHT_IMAGE_MESSAGE
                        }else LEFT_IMAGE_MESSAGE
                    }
                    /*CometChatConstants.MESSAGE_TYPE_AUDIO->{}
                    CometChatConstants.MESSAGE_TYPE_VIDEO->{}*/
                    else -> -1
                }
            }
            CometChatConstants.CATEGORY_ACTION->{
                when(message.type){
                    CometChatConstants.ActionKeys.ACTION_TYPE_GROUP_MEMBER-> ACTION_GROUP_MEMBER
                    //CometChatConstants.ActionKeys.ACTION_TYPE_GROUP->{}
                    else -> -1
                }
            }
            CometChatConstants.CATEGORY_CALL-> ACTION_CALL
           // CometChatConstants.CATEGORY_CUSTOM->{}
            else -> -1
        }

    }
}