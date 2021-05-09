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
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.RecyclerItemRowBinding
import com.example.cometchatprotask.utils.CommonUtils

class CallScreenAdapter : ListAdapter<BaseMessage,CallScreenAdapter.CallListViewHolder>(comparator) {
    private val TAG = "CallScreenAdapter"

    class CallListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        companion object{
            lateinit var binding: RecyclerItemRowBinding
            private val loggedInUser = CometChat.getLoggedInUser().uid
            fun create(parent: ViewGroup):CallListViewHolder{
                binding = RecyclerItemRowBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_row,parent,false))
                return CallListViewHolder(binding.root)
            }
        }
        fun bind(current: BaseMessage){
            val call : Call = current as Call
            var status : String = ""
            var time : String
            var isVideo : Boolean
            if(call.receiverType == CometChatConstants.RECEIVER_TYPE_USER){
                if((call.callInitiator as User).uid == CometChat.getLoggedInUser().uid){
                    binding.usersName.text = (call.callReceiver as User).name
                    Glide.with(itemView).load((call.receiver as User).avatar).placeholder(R.drawable.user).into(binding.avatar)
                    if(call.callStatus == CometChatConstants.CALL_STATUS_CANCELLED || call.callStatus == CometChatConstants.CALL_STATUS_UNANSWERED){
                        status = "Missed"
                    }else if(call.callStatus == CometChatConstants.CALL_STATUS_REJECTED){
                        status = "Rejected"
                    }else{
                        status ="Outgoing"
                    }
                }else{
                    binding.usersName.text=(call.callInitiator as User).name
                    Glide.with(itemView).load((call.callInitiator as User).avatar).placeholder(R.drawable.user).into(binding.avatar)
                    if(call.callStatus == CometChatConstants.CALL_STATUS_CANCELLED || call.callStatus == CometChatConstants.CALL_STATUS_UNANSWERED){
                        status = "Missed"
                    }else if(call.callStatus == CometChatConstants.CALL_STATUS_REJECTED){
                        status = "Rejected"
                    }else{
                        status ="Incoming"
                    }
                }
            }else{
                status = ""
            }
            if(call.type == CometChatConstants.CALL_TYPE_AUDIO) {
                status = status + " Audio Call"
                isVideo = true
            }else if(call.type == CometChatConstants.CALL_TYPE_VIDEO){
                status = status + "Video Call"
            }
            binding.status.text = status
            binding.callTime.visibility = View.VISIBLE
            binding.callTime.text =  CommonUtils.getLastMessageDate(call.initiatedAt)
            binding.callButton.visibility = View.VISIBLE
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