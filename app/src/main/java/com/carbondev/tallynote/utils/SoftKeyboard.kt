package com.carbondev.tallynote.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat

class SoftKeyboard(val activity: Activity) {

    private val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    fun hideKeyboardFromActivity() {

        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isKeyboardOpen(): Boolean {
        return imm.isAcceptingText
    }
}