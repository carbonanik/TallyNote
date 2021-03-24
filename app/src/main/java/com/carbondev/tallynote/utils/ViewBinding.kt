package com.carbondev.tallynote.utils

import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.carbondev.tallynote.R


@BindingAdapter("dueOrAdv")
fun setDueOrAdv(textView: TextView, amount: String?) {
    val prefix: String
    if (amount != null) {
        if (amount[0] == '-') {
            prefix = textView.context.getString(R.string.adv_clone)
            textView.text = (prefix + numEnOrBn(amount.substring(1), textView.context))
        } else {
            prefix = textView.context.getString(R.string.due_clone)
            textView.text = (prefix + numEnOrBn(amount, textView.context))
        }
    }
}


fun numEnOrBn(num: String, context: Context): String {
    return if (context.resources.configuration.locale.language == "bn") {
        num.replace("0", "০").replace("1", "১")
            .replace("2", "২").replace("3", "৩")
            .replace("4", "৪").replace("5", "৫")
            .replace("6", "৬").replace("7", "৭")
            .replace("8", "৮").replace("9", "৯")
    } else
        num
}