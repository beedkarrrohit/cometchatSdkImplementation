package com.example.cometchatprotask.cometchatactivities.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.viewHolders.BaseViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.LeftMessageViewHolder
import com.example.cometchatprotask.cometchatactivities.viewHolders.RightMessageViewHolder


class UserChatScreenAdapter : ListAdapter<BaseMessage,RecyclerView.ViewHolder>(comparator) {
    private val RIGHT_TEXT_MESSAGE = 1
    private val LEFT_TEXT_MESSAGE = 2
    private val TAG = "UserChatScreenAdapter"
    companion object{
        var itemViewType : Int = 0
        private var comparator = object : DiffUtil.ItemCallback<BaseMessage>(){
            override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem.id == newItem.id
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
        }
        return messageViewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(itemViewType){
            RIGHT_TEXT_MESSAGE -> {
                val rightMessageViewHolder = holder as RightMessageViewHolder
                var current = getItem(position)
                rightMessageViewHolder.setIsRecyclable(false)
                rightMessageViewHolder.bind(current as TextMessage)

            }
            LEFT_TEXT_MESSAGE ->{
                val leftMessageViewHolder = holder as LeftMessageViewHolder
                leftMessageViewHolder.setIsRecyclable(false)
                leftMessageViewHolder.bind(getItem(position) as TextMessage)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItemViewTypes(position)
        return position
    }

    fun getItemViewTypes(position: Int)  {
        val message = getItem(position)
        if (message.sender.uid == CometChat.getLoggedInUser().uid){
            itemViewType = RIGHT_TEXT_MESSAGE
        }else{
            itemViewType = LEFT_TEXT_MESSAGE
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


}