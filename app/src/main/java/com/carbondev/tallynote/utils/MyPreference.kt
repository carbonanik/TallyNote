package com.carbondev.tallynote.utils


import android.content.Context
import android.content.SharedPreferences

const val PREFERENCE_NAME = "SharedPreference"
const val PREFERENCE_LANGUAGE = "Language"

class MyPreference(context : Context){


    private val preference: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)

    fun getLanguage() : String? {
        return preference.getString(PREFERENCE_LANGUAGE,"bn")
    }

    fun setLanguage(Language:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_LANGUAGE,Language)
        editor.apply()
    }

}