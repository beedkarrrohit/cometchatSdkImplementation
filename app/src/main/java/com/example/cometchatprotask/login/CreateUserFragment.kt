package com.example.cometchatprotask.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.FragmentCreateUserBinding
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.utils.Constants

class CreateUserFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentCreateUserBinding
    private val TAG = "CreateUserFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentCreateUserBinding.inflate(layoutInflater)
        binding.createUser.setOnClickListener(this)
       return binding.root
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.createUser -> {
                var uid = binding.UID.text.toString()
                var name = binding.name.text.toString()
                if (checkInput(uid, name)) {
                    val user = User()
                    user.name = name
                    user.uid = uid
                    CometChat.createUser(user, Constants.AUTH_KEY, object : CometChat.CallbackListener<User>() {
                        override fun onSuccess(p0: User?) {
                            Log.d(TAG, "onSuccess: ${p0.toString()}")
                            requireContext().toast("User Created Successfully")
                            Login.login(requireContext(),p0?.uid)
                        }
                        override fun onError(p0: CometChatException?) {
                            requireContext().toast("Failed to create User: ${p0?.message}")
                        }
                    })
                } else {
                    requireContext().toast("Please Fill up the Above Fields")
                }
            }
        }
    }

    private fun checkInput(uid : String, name : String): Boolean {
        return !(TextUtils.isEmpty(uid) && TextUtils.isEmpty(name))
    }
}