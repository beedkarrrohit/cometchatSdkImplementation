package com.example.cometchatprotask.cometchatactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.example.cometchatprotask.R
import com.example.cometchatprotask.databinding.ActivityCometChatBinding
import com.example.cometchatprotask.handler.toast
import com.example.cometchatprotask.login.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope

class CometChatActivity : AppCompatActivity(),View.OnClickListener,LoaderManager.LoaderCallbacks<BaseMessage>{
    lateinit var binding: ActivityCometChatBinding
    private  val TAG = "CometChatActivity"
    var job = SupervisorJob()
    var scope = CoroutineScope(Dispatchers.IO + job)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCometChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.conversation,
            R.id.user,
            R.id.groups,
            R.id.calls,
            R.id.profile
        ).build()
        var navController = findNavController(R.id.bottom_nav_fragment)
        setupActionBarWithNavController( navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.bottomNavBar,navController)
        val users = CometChat.getLoggedInUser()
        Log.e(TAG, "Show users: ${users.toString()}")
       // Log.e(TAG, "onCreateBool: ${isOneOnOneEnabled()}")
    }

/*     @ExperimentalCoroutinesApi
      fun isOneOnOneEnabled(): Boolean {
         val response = CompletableDeferred<Boolean>()
         lit@ var v = scope.async {
             isFeatureEnabled()
         }

     }

    suspend fun isFeatureEnabled() : Boolean {
        val response = CompletableDeferred<Boolean>()
        CometChat.isFeatureEnabled("core.chat.one-on-one.enabled",object: CometChat.CallbackListener<Boolean>(){
            override fun onSuccess(p0: Boolean?) {
                if(p0 != null) response.complete(p0)
            }
            override fun onError(p0: CometChatException?) {

            }
        })
        return response.await()
    }*/



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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<BaseMessage> {
        TODO("Not yet implemented")
    }

    override fun onLoadFinished(loader: Loader<BaseMessage>, data: BaseMessage?) {
        TODO("Not yet implemented")
    }

    override fun onLoaderReset(loader: Loader<BaseMessage>) {
        TODO("Not yet implemented")
    }


}