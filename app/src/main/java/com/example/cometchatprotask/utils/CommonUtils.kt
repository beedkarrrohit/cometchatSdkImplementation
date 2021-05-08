package com.example.cometchatprotask.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat

class CommonUtils {
    companion object{
        fun convertTimestampToDate(timeStamp : Long) : String{
            val time = Timestamp(timeStamp)
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
           return simpleDateFormat.format(time)
        }
    }
}