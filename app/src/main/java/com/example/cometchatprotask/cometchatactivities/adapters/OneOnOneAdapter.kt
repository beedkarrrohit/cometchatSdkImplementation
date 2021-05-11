package com.example.cometchatprotask.cometchatactivities.adapters

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.cometchatactivities.viewHolders.ActionViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.LeftMessageViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.RightMessageViewHolder

class OneOnOneAdapter(context: Context , messageList: List<BaseMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageList : MutableList<BaseMessage> = ArrayList()
    companion object{
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
        private  val ACTION_CALL = 13
        private val ACTION_AUDIO_CALL = 14
        private val ACTION_VIDEO_CALL = 15
        //message type custom
    }

    init {
        submitList(messageList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            RIGHT_TEXT_MESSAGE ->{
                return RightMessageViewHolder.create(parent)
            }
            LEFT_TEXT_MESSAGE->{
                return LeftMessageViewHolder.create(parent)
            }
            ACTION_CALL->{
                return ActionViewHolder.create(parent)
            }
            else->{
                return ActionViewHolder.create(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val baseMessage = messageList[position]
        when(holder.itemViewType){
            RIGHT_TEXT_MESSAGE -> {
                Log.e("uu", "onBindViewHolder: ${holder}" )
                val rightMessageViewHolder = holder as RightMessageViewHolder
                rightMessageViewHolder.bind(baseMessage)
            }LEFT_TEXT_MESSAGE->{
                val leftMessageViewHolder = holder as LeftMessageViewHolder
                leftMessageViewHolder.bind(baseMessage)
            }ACTION_CALL->{
                val actionViewHolder = holder as ActionViewHolder
                actionViewHolder.bind(baseMessage)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun submitList(list : List<BaseMessage>){
        this.messageList.addAll(0,list)
        notifyItemRangeInserted(0,list.size)
    }

    fun updateList(list : List<BaseMessage>){
        submitList(list)
    }

    fun getItemTypes(position: Int) : Int {
        val message = messageList[position]
        return when(message.category){
            CometChatConstants.CATEGORY_MESSAGE->{
                when(message.type){
                    CometChatConstants.MESSAGE_TYPE_TEXT->{
                        if(message.sender.uid == CometChat.getLoggedInUser().uid){
                            return RIGHT_TEXT_MESSAGE
                        }else {
                            return LEFT_TEXT_MESSAGE
                        }
                    }
                    /*CometChatConstants.MESSAGE_TYPE_IMAGE->{}
                    CometChatConstants.MESSAGE_TYPE_VIDEO->{}
                    CometChatConstants.MESSAGE_TYPE_FILE->{}
                    CometChatConstants.MESSAGE_TYPE_AUDIO->{}
                    CometChatConstants.MESSAGE_TYPE_CUSTOM->{}*/
                    else -> -1
                }
            }
            //CometChatConstants.CATEGORY_CUSTOM->{}
            CometChatConstants.CATEGORY_CALL->{
               return ACTION_CALL
            }
            else -> -1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItemTypes(position)
    }
}