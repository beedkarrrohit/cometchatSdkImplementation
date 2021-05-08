package com.example.cometchatprotask.cometchatactivities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.GroupsRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.example.cometchatprotask.cometchatactivities.adapters.GroupListAdapter
import com.example.cometchatprotask.cometchatactivities.adapters.OnClickInterface
import com.example.cometchatprotask.databinding.FragmentGroupsBinding
import com.example.cometchatprotask.databinding.RecyclerBinding
import com.example.cometchatprotask.handler.toast

class Groups : Fragment(),OnClickInterface {
    lateinit var binding : FragmentGroupsBinding
    lateinit var subBinding :RecyclerBinding
    lateinit var groupListAdapter: GroupListAdapter
    private val TAG = "Groups"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentGroupsBinding.inflate(layoutInflater)
        subBinding = RecyclerBinding.bind(binding.root)
        groupListAdapter = GroupListAdapter(this)
        subBinding.recycler.adapter = groupListAdapter
        getGroupList()
        Log.e(TAG, "selfId: $uid", )
        return binding.root
    }

    private fun getGroupList() {
        var groupsRequest : GroupsRequest? = null
        val limit = 30
        groupsRequest = GroupsRequest.GroupsRequestBuilder().setLimit(limit).build()
        groupsRequest?.fetchNext(object : CometChat.CallbackListener<List<Group>>() {
            override fun onSuccess(p0: List<Group>?) {
                Log.e(TAG, "onSuccessGroup: $p0")
                groupListAdapter.submitList(p0)
            }

            override fun onError(p0: CometChatException?) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onItemClick(position: Int) {
        var group = groupListAdapter.currentList[position]
        Log.e(TAG, "onItemClickG: $group")
        if(group.isJoined){
            val intent = Intent(activity,GroupChatScreenActivity::class.java)
            intent.putExtras(createIntentBundle(group))
            startActivity(intent)
        }else{
            var dialogBuilder = AlertDialog.Builder(activity)
                .setMessage("Do you want to join this group")
                .setCancelable(false)
                .setPositiveButton("Join",DialogInterface.OnClickListener{
                        _, i ->  joinGroup(group.guid,group.groupType,group.password)
                })
                .setNegativeButton("Cancel",DialogInterface.OnClickListener{
                    dialog,i-> dialog.cancel()
                })
                var alert = dialogBuilder.create()
            alert.setTitle("Join Group")
            alert.show()
        }
    }
     private fun createIntentBundle(group: Group) : Bundle{
         val bundle = Bundle()
         bundle.putString("gname",group.name)
         bundle.putString("guid",group.guid)
         bundle.putString("icon",group.icon)
         bundle.putInt("members",group.membersCount)
         bundle.putString("selfuid", uid.uid)
          return bundle
     }

    private fun joinGroup(guid:String,type:String,password:String?){
        if(type == CometChatConstants.GROUP_TYPE_PUBLIC){
            binding.groupProgress.visibility = View.VISIBLE
            cometChatJoinGroup(guid,type,password)
        }
    }

    private fun cometChatJoinGroup(guid:String,type:String,password:String?){
       var pass = ""
        if(password != null) pass = password
        CometChat.joinGroup(guid, type, password, object : CometChat.CallbackListener<Group>() {
            override fun onSuccess(p0: Group?) {
                Log.e(TAG, "onJoinGroupSuccess: $p0")
                binding.groupProgress.visibility = View.GONE
                var intent = Intent(activity,GroupChatScreenActivity::class.java)
                intent.putExtras(createIntentBundle(p0!!))
                startActivity(intent)
            }
            override fun onError(p0: CometChatException?) {
            }

        })
    }
    companion object{
        var uid: User = CometChat.getLoggedInUser()
    }

    override fun onResume() {
        super.onResume()
        getGroupList()
    }
}