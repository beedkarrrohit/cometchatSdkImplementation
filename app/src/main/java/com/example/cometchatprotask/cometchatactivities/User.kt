package com.example.cometchatprotask.cometchatactivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.UsersRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.cometchatprotask.cometchatactivities.adapters.OnClickInterface
import com.example.cometchatprotask.cometchatactivities.adapters.UserListAdapter
import com.example.cometchatprotask.databinding.FragmentUserBinding
import com.example.cometchatprotask.databinding.RecyclerBinding
import com.example.cometchatprotask.handler.toast


class User : Fragment(),OnClickInterface {
    lateinit var binding: FragmentUserBinding
    lateinit var subBindig : RecyclerBinding
    lateinit var adapter: UserListAdapter
    private  val TAG = "User"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)
        subBindig = RecyclerBinding.bind(binding.root)
        adapter = UserListAdapter(this)
        subBindig.recycler.adapter = adapter
        getUserList()
        return binding.root
    }

    private fun getUserList() {
        var usersRequest:UsersRequest?=null
        val limit:Int=30
        usersRequest = UsersRequest.UsersRequestBuilder().setLimit(limit).build()
        usersRequest?.fetchNext(object : CometChat.CallbackListener<List<User>>(){
            override fun onSuccess(p0: List<User>?) {
                adapter.submitList(p0)
            }

            override fun onError(p0: CometChatException?) {

            }

        })

    }

    override fun onItemClick(position: Int) {
        var user = adapter.currentList[position]
        requireContext().toast("Item Clicked ${user.name}")
        var intent = Intent(activity, ChatScreenActivity::class.java)
        intent.putExtras(createIntentBundle(user))
       /* intent.putExtra("name",user.name)
        intent.putExtra("status",user.status)
        intent.putExtra("avatar",user.avatar)
        intent.p*/
        startActivity(intent)
    }

    fun createIntentBundle(user: User) : Bundle{
        val bundle = Bundle()
        bundle.putString("name",user.name)
        bundle.putString("status",user.status)
        bundle.putString("avatar",user.avatar)
        bundle.putString("ruserid",user.uid)
        Log.e(TAG, "createIntentBundle: ${uid.uid}", )
        bundle.putString("selfuid", uid.uid)
        return bundle
    }

    companion object{
        var uid: User = CometChat.getLoggedInUser()
    }
}


