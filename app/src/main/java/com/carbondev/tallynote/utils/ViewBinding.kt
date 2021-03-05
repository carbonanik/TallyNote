package com.carbondev.tallynote.utils

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.carbondev.tallynote.R
import java.util.*

@BindingAdapter(value = ["app:dueOrAdv", "app:dueOrAdvLD"], requireAll = false)
fun setDueOrAdv(textView: TextView, dueOrAdv: String?, dueOrAdvLD: MutableLiveData<String>?) {
    var prefix: String

    if (dueOrAdv != null) {
        if (dueOrAdv[0] == '-') {
            prefix = textView.context.getString(R.string.adv_clone)
            textView.text = (prefix + numEnOrBn(dueOrAdv.substring(1), textView.context))
        } else {
            prefix = textView.context.getString(R.string.due_clone)
            textView.text = (prefix + numEnOrBn(dueOrAdv, textView.context))
        }
    }
    dueOrAdvLD?.value?.let {
        if (it[0] == '-') {
            prefix = textView.context.getString(R.string.adv_clone)
            textView.text = (prefix + numEnOrBn(it.substring(1), textView.context))
        } else {
            prefix = textView.context.getString(R.string.due_clone)
            textView.text = (prefix + numEnOrBn(it, textView.context))
        }
    }
}

fun numEnOrBn(num: String, context: Context): String {
//    println(context.resources.configuration.locale.language)
    return if (context.resources.configuration.locale.language == "bn") {
        num.replace("0", "০").replace("1", "১")
            .replace("2", "২").replace("3", "৩")
            .replace("4", "৪").replace("5", "৫")
            .replace("6", "৬").replace("7", "৭")
            .replace("8", "৮").replace("9", "৯")
    } else
        num
}