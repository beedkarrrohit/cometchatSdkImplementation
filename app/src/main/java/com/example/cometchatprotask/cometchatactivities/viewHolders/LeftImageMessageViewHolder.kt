package com.example.cometchatprotask.cometchatactivities.viewHolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cometchat.pro.models.Attachment
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.MediaMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.CcLeftImageMessageBinding
import com.example.cometchatprotask.utils.CommonUtils

class LeftImageMessageViewHolder(val item : View) : BaseViewHolder(item) {
    private val TAG = "LeftImageMessageViewHol"
    val binding = CcLeftImageMessageBinding.bind(item)
    companion object {
        fun create(parent : ViewGroup) : LeftImageMessageViewHolder{
            return LeftImageMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cc_left_image_message,parent,false))
        }
    }
    fun bind(baseMessage : BaseMessage){
        var attachment = (baseMessage as MediaMessage).attachment as Attachment
        val file_url = attachment.fileUrl
        Glide.with(item).load(file_url).placeholder(R.drawable.cc_left_chat_bubble_background).into(binding.leftImageMessage)
        binding.timestamp.text = CommonUtils.convertTimestampToDate(baseMessage.sentAt)
    }

}