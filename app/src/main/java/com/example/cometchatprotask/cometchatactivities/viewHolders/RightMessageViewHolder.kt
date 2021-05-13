package com.example.cometchatprotask.cometchatactivities.viewHolders

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.RightTextMessageLayoutBinding
import com.example.cometchatprotask.utils.CommonUtils

class RightMessageViewHolder(itemView : View) : BaseViewHolder(itemView)  {
    private val TAG = "RightMessageViewHolder"
    var binding  = RightTextMessageLayoutBinding.bind(itemView)
    companion object{
         fun create (parent : ViewGroup) : RightMessageViewHolder {
             val view = LayoutInflater.from(parent.context).inflate(R.layout.right_text_message_layout,parent,false)
             //this.binding = RightTextMessageLayoutBinding.bind(view)
             return RightMessageViewHolder(view)
        }
    }

    fun bind(message: BaseMessage){
        Log.e(TAG, "bind: ${(message as TextMessage).text}" )
        binding.rightMessage.text = (message as TextMessage).text.trim()
        binding.timestamp.text=CommonUtils.convertTimestampToDate(message.sentAt)
        setReadReceipts(message)
    }

    fun setReadReceipts(textMessage: BaseMessage){
        Log.e(TAG, "setTimeStamp: ${textMessage.deliveredAt}")
        if(textMessage.readAt != 0L){
            if(textMessage.receiver is User){
                binding.timestamp.apply{
                    setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_seen_blue_tick,0,0,0)
                    compoundDrawablePadding = 10
                }
            }
        }else if(textMessage.deliveredAt != 0L){
            if(textMessage.receiver is User)binding.timestamp.apply{
                setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_delivered_double_tick,0,0,0)
                compoundDrawablePadding = 10
            }
        }else{
            if(textMessage.receiver is User)binding.timestamp.apply{
                setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_sent_single_tick,0,0,0)
                compoundDrawablePadding = 10
            }
        }
    }
}