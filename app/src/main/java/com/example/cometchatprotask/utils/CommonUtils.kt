package com.example.cometchatprotask.utils

import android.util.Log
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object{
        private  val TAG = "CommonUtils"
        fun convertTimestampToDate(timeStamp : Long) : String{
            val simpleDateFormat = SimpleDateFormat("h:mm a").format(Date(timeStamp * 1000))
           return simpleDateFormat
        }

        fun getLastMessageDate(timestamp: Long): String? {
            val lastMessageTime = SimpleDateFormat("h:mm a").format(Date(timestamp * 1000))
            val lastMessageDate = SimpleDateFormat("dd/MM/yyyy").format(Date(timestamp * 1000))
            val lastMessageWeek = SimpleDateFormat("EEE").format(Date(timestamp * 1000))
            val currentTimeStamp = System.currentTimeMillis()
            val diffTimeStamp = currentTimeStamp - timestamp * 1000
            Log.e(TAG, "getLastMessageDate: " + 24 * 60 * 60 * 1000)
            return if (diffTimeStamp < 24 * 60 * 60 * 1000) {
                lastMessageTime
            } else if (diffTimeStamp < 48 * 60 * 60 * 1000) {
                "Yesterday"
            } else if (diffTimeStamp < 7 * 24 * 60 * 60 * 1000) {
                lastMessageWeek
            } else {
                lastMessageDate
            }
        }
    }
}