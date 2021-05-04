package com.example.cometchatprotask.cometchatactivities.viewHolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.RightTextMessageLayoutBinding

class RightMessageViewHolder(itemView : View) : BaseViewHolder(itemView)  {
    companion object{
        lateinit var binding : RightTextMessageLayoutBinding
         fun create (parent : ViewGroup) : RightMessageViewHolder {
             val view = LayoutInflater.from(parent.context).inflate(R.layout.right_text_message_layout,parent,false)
             binding = RightTextMessageLayoutBinding.bind(view)
             return RightMessageViewHolder(view)
        }
    }

    fun bind(message: TextMessage){
        binding.rightMessage.text = message.text
    }
}