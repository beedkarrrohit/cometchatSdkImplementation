package com.example.cometchatprotask.cometchatactivities.viewHolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.LeftTextMessageLayoutBinding
import com.example.cometchatprotask.utils.CommonUtils

class LeftMessageViewHolder(itemView : View) : BaseViewHolder(itemView){
    companion object{
        lateinit var binding: LeftTextMessageLayoutBinding
        fun create(parent: ViewGroup): LeftMessageViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.left_text_message_layout,parent,false)
            binding = LeftTextMessageLayoutBinding.bind(view)
            return LeftMessageViewHolder(view)
        }
    }
    fun bind(message : TextMessage){
        binding.leftMessage.text = message.text
        binding.timestamp.text = CommonUtils.convertTimestampToDate(message.deliveredToMeAt)

    }
}