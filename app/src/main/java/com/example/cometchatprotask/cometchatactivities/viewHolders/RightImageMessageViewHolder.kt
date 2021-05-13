package com.example.cometchatprotask.cometchatactivities.viewHolders

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cometchat.pro.models.Attachment
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.MediaMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.CcRightImageMessageBinding
import com.example.cometchatprotask.utils.CommonUtils


class RightImageMessageViewHolder(val item : View) : BaseViewHolder(item) {
    private  val TAG = "RightImageMessageViewHo"
    var binding = CcRightImageMessageBinding.bind(item)
    companion object{
        fun create(parent: ViewGroup): RightImageMessageViewHolder{
            return RightImageMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cc_right_image_message,parent,false))
        }
    }
    fun bind(base : BaseMessage){
        Log.e(TAG, "bind Image Massage: $base" )
        var attachment = (base as MediaMessage).attachment as Attachment
        var file_url = attachment.fileUrl
        Glide.with(item).load(file_url).placeholder(R.drawable.cc_left_chat_bubble_background).into(binding.rightImageMessage)
        binding.timestamp.text =  CommonUtils.convertTimestampToDate(base.sentAt)
        setReadReceipt(base)
    }

    private fun setReadReceipt(base: BaseMessage) {
        if(base.readAt != 0L){
            binding.timestamp.apply {
                setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_seen_blue_tick,0,0,0)
                compoundDrawablePadding=10
            }
        }else if(base.deliveredAt != 0L){
            binding.timestamp.apply{
                setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_delivered_double_tick,0,0,0)
                compoundDrawablePadding=10
            }
        }else {
            binding.timestamp.apply{
                setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_sent_single_tick,0,0,0)
                compoundDrawablePadding=10
            }
        }
    }
}