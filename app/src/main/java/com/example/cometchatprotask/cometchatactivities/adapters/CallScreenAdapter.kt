package com.example.cometchatprotask.cometchatactivities.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.RecyclerItemRowBinding
import com.example.cometchatprotask.utils.CommonUtils

class CallScreenAdapter : ListAdapter<BaseMessage,CallScreenAdapter.CallListViewHolder>(comparator) {
    private val TAG = "CallScreenAdapter"

    class CallListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val binding = RecyclerItemRowBinding.bind(itemView)
        companion object{
            private val loggedInUser = CometChat.getLoggedInUser().uid
            fun create(parent: ViewGroup):CallListViewHolder{
                return CallListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_row,parent,false))
            }
        }
        fun bind(current: BaseMessage){
            val call : Call = current as Call
            var status : String = ""
            var time : String
            var isVideoCall : Boolean = false
            var isIncoming : Boolean = false
            var isMissed = false
            if(call.receiverType == CometChatConstants.RECEIVER_TYPE_USER){
                if((call.callInitiator as User).uid == CometChat.getLoggedInUser().uid){
                    binding.usersName.text = (call.callReceiver as User).name
                    Glide.with(itemView).load((call.callReceiver as User).avatar).placeholder(R.drawable.user).into(binding.avatar)
                    if(call.callStatus == CometChatConstants.CALL_STATUS_CANCELLED || call.callStatus == CometChatConstants.CALL_STATUS_UNANSWERED){
                        status = "Missed"
                        isMissed = true
                    }else if(call.callStatus == CometChatConstants.CALL_STATUS_REJECTED){
                        status = "Rejected"
                    }else{
                        status ="Outgoing"
                    }
                    isIncoming = false
                }else{
                    binding.usersName.text=(call.callInitiator as User).name
                    Glide.with(itemView).load((call.callInitiator as User).avatar).placeholder(R.drawable.user).into(binding.avatar)
                    if(call.callStatus == CometChatConstants.CALL_STATUS_CANCELLED || call.callStatus == CometChatConstants.CALL_STATUS_UNANSWERED){
                        status = "Missed"
                        isMissed = true
                    }else if(call.callStatus == CometChatConstants.CALL_STATUS_REJECTED){
                        status = "Rejected"
                    }else{
                        status ="Incoming"
                    }
                    isIncoming = true
                }
            }else{
                binding.usersName.text = (call.callReceiver as Group).name
                Glide.with(itemView).load((call.callReceiver as Group).icon).placeholder(R.drawable.user).into(binding.avatar)
                if((call.callInitiator as User).uid == CometChat.getLoggedInUser().uid){
                    if(call.callStatus == CometChatConstants.CALL_STATUS_UNANSWERED){
                        status = "Missed"
                        isMissed = true
                    }else if(call.callStatus == CometChatConstants.CALL_STATUS_REJECTED){
                        status = "Rejected"
                    }else status = "incoming"
                    isIncoming = false
                }else{
                    if(call.callStatus == CometChatConstants.CALL_STATUS_UNANSWERED){
                        status = "Missed"
                        isMissed = true
                    }else if(call.callStatus == CometChatConstants.CALL_STATUS_REJECTED){
                        status = "Rejected"
                    }else status = "incoming"
                    isIncoming = true
                }
            }
            if(call.type == CometChatConstants.CALL_TYPE_AUDIO) {
                status = "$status Audio Call"
                isVideoCall = false
            }else if(call.type == CometChatConstants.CALL_TYPE_VIDEO){
                status = status + " Video Call"
                isVideoCall = true
            }
            if(isVideoCall){
                binding.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vidcam_drawable,0,0,0)
            }else{
                if(isIncoming && isMissed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_missed_white_24dp,0,0,0)
                }else if(isIncoming && !isMissed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_received_white_24dp,0,0,0)
                }else if(!isIncoming && !isMissed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_made_white_24dp,0,0,0)
                }else if(!isIncoming && isMissed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_missed_outgoing_white_24dp,0,0,0)
                }
            }
            binding.status.text = status
            binding.callTime.visibility = View.VISIBLE
            binding.callTime.text =  CommonUtils.getLastMessageDate(call.initiatedAt)
        }
    }
    companion object{
        private val comparator = object: DiffUtil.ItemCallback<BaseMessage>(){
            override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallListViewHolder {
        return CallListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CallListViewHolder, position: Int) {
        Log.e(TAG, "onBindViewHolderCall: ${getItem(position)}", )
        holder.bind(getItem(position))
    }
}