package com.example.cometchatprotask.cometchatactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.ActivityCometChatBinding
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.login.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CometChatActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var binding: ActivityCometChatBinding
    private  val TAG = "CometChatActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCometChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.conversation,
            R.id.user,
            R.id.groups,
            R.id.profile
        ).build()
        var navController = findNavController(R.id.bottom_nav_fragment)
        setupActionBarWithNavController( navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.bottomNavBar,navController)
        val users = CometChat.getLoggedInUser()
        Log.e(TAG, "Show users: ${users.toString()}")
    }

    override fun onClick(p0: View?) {
        when(p0?.id){

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cometchat_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
            R.id.logout -> {
                cometChatLogout()
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    private fun cometChatLogout() : Boolean {
        var logout = false
        CometChat.logout(object : CometChat.CallbackListener<String>(){
            override fun onSuccess(p0: String?) {
                this@CometChatActivity.toast("Logout successfully")
                startActivity(Intent(this@CometChatActivity,MainActivity::class.java))
                finish()
                logout = true
            }

            override fun onError(p0: CometChatException?) {
                logout = false
            }

        })
        return logout
    }

    override fun onBackPressed() {
        finish()
    }

}