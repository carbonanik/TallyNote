package com.carbondev.tallynote.utils

import java.text.SimpleDateFormat
import java.util.*

class MyDateFormat(private var timeStamp: String) {

    private val calendar = Calendar.getInstance()

    init {
        if (timeStamp.isEmpty()){
            timeStamp = "2000-01-01 00:00:00"
        }
        val iso8601Format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = iso8601Format.parse(timeStamp)
        calendar.time = date!!
//        calendar.add(Calendar.MINUTE , 6*60)
    }

    fun getDayOfWeekName(): String {
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())!!.toString()
    }

    fun getMonthName(): String? {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    }

    fun getHourIn12Format(): String {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return when {
            hour == 0 -> "12"
            hour < 12 -> hour.toString()
            hour == 12 -> "12"
            else -> (hour - 12).toString()
        }
    }

    fun getDayOfMonth() : String {
        return calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    fun getYear(): String {
        return calendar.get(Calendar.YEAR).toString()
    }

    fun getMinute(): String {
        return calendar.get(Calendar.MINUTE).toString()
    }

    fun getAmPm(): String {
        return calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault())!!.toString()
    }

    fun getMillis(): Long {
        return calendar.timeInMillis
    }

    fun sellDateString(): String {
        return "List: " +
                getDayOfWeekName() +
                ", " +
                getDayOfMonth() +
                "-" +
                getMonthName() +
                "-" +
                getYear() +
                "  " +
                getHourIn12Format() +
                ":" +
                getMinute() +
                " " +
                getAmPm()
    }
}
