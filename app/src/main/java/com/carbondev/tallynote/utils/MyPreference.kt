package com.carbondev.tallynote.utils


import android.content.Context

const val PREFERENCE_NAME = "SharedPreference"
const val PREFERENCE_LANGUAGE = "Language"

class MyPreference(context : Context){


    val preference = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)

    fun getLanguage() : String? {
        return preference.getString(PREFERENCE_LANGUAGE,"bn")
    }

    fun setLanguage(Language:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_LANGUAGE,Language)
        editor.apply()
    }

}