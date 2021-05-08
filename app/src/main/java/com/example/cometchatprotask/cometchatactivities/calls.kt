package com.example.cometchatprotask.cometchatactivities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.cometchatactivities.adapters.CallScreenAdapter
import com.example.cometchatprotask.databinding.FragmentCallsBinding


class calls : Fragment() {
    lateinit var binding : FragmentCallsBinding
    lateinit var adapter : CallScreenAdapter
    private  val TAG = "calls"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCallsBinding.inflate(layoutInflater)
        setupFields()
        fetchCallList()
        return binding.root
    }
    fun setupFields(){
        adapter = CallScreenAdapter()
        binding.callRecycler.adapter = adapter
    }

    fun fetchCallList(){
        Log.e(TAG, "onError fetchCallList: Fetching call list", )
        var messagesRequest: MessagesRequest
        val limit = 30
        val list = mutableListOf<String>(
            CometChatConstants.CATEGORY_CALL
        )
        Log.e(TAG, "onError fetchCallList: $list")
        messagesRequest = MessagesRequest.MessagesRequestBuilder().setCategories(list).setLimit(limit).build()
        messagesRequest?.fetchPrevious(object : CometChat.CallbackListener<List<BaseMessage>>() {
            override fun onSuccess(p0: List<BaseMessage>?) {
                Log.e(TAG, "onError onSuccess: $p0")
                if (!p0.isNullOrEmpty()) {
                    adapter.submitList(p0)
                } else {
                    binding.noRecentCalls.visibility = View.VISIBLE
                }
            }

            override fun onError(p0: CometChatException?) {
                Log.e(TAG, "onError: $p0",)
            }
        })
    }

}