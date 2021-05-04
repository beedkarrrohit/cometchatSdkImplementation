package com.example.cometchatprotask.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.FragmentLoginBinding


class LoginFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        setFields()
        return binding.root
    }
    private fun setFields(){
        binding.createuser.setOnClickListener(this)
        binding.login.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.createuser -> {
                findNavController().navigate(R.id.action_loginFragment_to_createUserFragment)
            }
            R.id.login -> {
                var UID = binding.UID.text.toString()
                Login.login(requireContext(),UID)
            }
        }
    }

}