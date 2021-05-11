package com.example.cometchatprotask.cometchatactivities.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.models.Conversation

class ConversationViewModel : ViewModel() {
    private  val TAG = "ConversationViewModel"
    private val list : MutableLiveData<List<Conversation>> = MutableLiveData()
     fun getList():LiveData<List<Conversation>>{
         return list
     }

    fun getConversations(conversations : List<Conversation>){
        list.value = conversations
    }

    fun update(conversation : Conversation){
        var list : MutableList<Conversation> = list.value as MutableList<Conversation>
        if(list!!.contains(conversation)){
            val oldConversation = list[list.indexOf(conversation)]
            list.remove(oldConversation)
            if (conversation.lastMessage.category != CometChatConstants.CATEGORY_CUSTOM && conversation.lastMessage.category != CometChatConstants.CATEGORY_ACTION && conversation.lastMessage.editedAt == 0L && conversation.lastMessage.deletedAt == 0L) {
                conversation.unreadMessageCount = oldConversation.unreadMessageCount + 1
            }
            else {
                conversation.unreadMessageCount = oldConversation.unreadMessageCount
            }
            list.add(0,conversation)
            this.list.value = list
        }else{
            list.add(0,conversation)
            this.list.value = list
        }
    }
}