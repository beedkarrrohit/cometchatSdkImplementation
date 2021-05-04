package com.example.cometchatprotask.utils

import com.cometchat.pro.core.AppSettings

class Constants{
    companion object{
        var AUTH_KEY = "b3c172e90d839d00e43f3d8037d8188f9b5fcba9"
        val appID:String="32117cf666b9454"
        val region:String="us"
        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build()
    }
}
