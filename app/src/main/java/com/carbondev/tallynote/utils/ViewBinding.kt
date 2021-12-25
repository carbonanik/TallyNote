package com.carbondev.tallynote.utils

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.carbondev.tallynote.R


@BindingAdapter("dueOrAdv")
fun setDueOrAdv(textView: TextView, amount: String?) {
//    if (amount == null) return
    textView.text = findDueOrAdv(
        amount,
        R.string.due_clone,
        R.string.adv_clone,
        textView.context
    )
}

@BindingAdapter("beforeDueOrAdv")
fun setBeforeDueOrAdv(textView: TextView, beforeTotalDue: String?) {
//    if (beforeTotalDue == null) return
    textView.text = findDueOrAdv(
        beforeTotalDue,
        R.string.total_due_before,
        R.string.adv_before,
        textView.context
    )
}

@BindingAdapter("afterDueOrAdv")
fun setAfterDueOrAdv(textView: TextView, afterTotalDue : String?) {
//    if (afterTotalDue == null) return
    textView.text = findDueOrAdv(
        afterTotalDue,
        R.string.total_due_after,
        R.string.adv_after,
        textView.context
    )
}

@BindingAdapter("dateString")
fun setDateString(textView: TextView, date: String?){
    if (date == null) return
    val myDateFormat = MyDateFormat(date)
    textView.text = myDateFormat.sellDateString(textView.context)
}


@BindingAdapter("numberEnOrBn")
fun setNumberEnOrBn(textView: TextView, number: String?){
    if (number == null) return
    textView.text = numEnOrBn(number, textView.context)
}

@BindingAdapter("shortTitle")
fun setShortTitle(textView: TextView, title: String?) {
    if (title == null) return

    val trim = title.trim()
    textView.text = if (trim.length >= 24) {

//        val ellipsis = "\u2026"
        "${trim.take(22).replace("\n", " ")}…"
    } else {
        trim
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

fun findDueOrAdv(amount: String?, duePrefix: Int, advPrefix: Int, context: Context): String {
    if (amount == null) return ""
    return if (amount[0] == '-') {
        context.getString(advPrefix) + numEnOrBn(amount.substring(1), context)
    } else {
        context.getString(duePrefix) + numEnOrBn(amount, context)
    }
}

