package com.carbondev.tallynote.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateTimeString {
    fun now(): String {
        val date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }
}