package com.example.cometchatprotask.cometchatactivities.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.MessageReceipt

class GroupChatScreenViewModel : ViewModel() {
    private val TAG = "GroupChatScreenViewModel"
    private var list : MutableLiveData<List<BaseMessage>> = MutableLiveData()
    fun getMessageList(): LiveData<List<BaseMessage>> {
        return list
    }

    fun fetchMessage(id: String?){
        var messagesRequest: MessagesRequest?
        val limit = 30
        val uid = id
        messagesRequest= MessagesRequest.MessagesRequestBuilder().setGUID(uid!!).setLimit(limit).build()
        messagesRequest?.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                Log.e(TAG, "submitListN: $p0")
                if(p0!=null) list.postValue(p0)
            }
            override fun onError(p0: CometChatException?) {
            }
        })
    }

    fun setDeliveryReceipt(messageReceipt: MessageReceipt){
        val nList =  list.value
        if(nList != null){
            for(i in nList.indices.reversed()){
                val baseMessage:BaseMessage = nList[i]
                if (baseMessage.deliveredAt == 0L){
                    val index = nList.indexOf(baseMessage)
                    nList[index].deliveredAt = messageReceipt.deliveredAt
                }
            }
            Log.e(TAG, "onMessagesDeliveredRRRN: ${nList[nList.size-1]}", )
            list.postValue(nList)
        }
    }

    fun addMessage(base : BaseMessage){
        if(list.value != null){
            val nList = ArrayList<BaseMessage>(list.value)
            nList.add(base)
            list.value = nList
        }
    }
}