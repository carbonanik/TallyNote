package com.carbondev.tallynote.utils

class StringNumberCalculator(firstString : String, secondString : String) {

    private var firstNumber : Double = 0.0
    private var secondNumber : Double = 0.0

    init{
        firstNumber = firstString.toDouble()
        secondNumber = secondString.toDouble()
    }

    fun add() = String.format("%.0f",(firstNumber + secondNumber))

    fun sub() = String.format("%.0f",(firstNumber - secondNumber))

    private fun intAdd(a: String?, b: String?): String {
        return ((a?.toIntOrNull() ?: 0).plus(b?.toIntOrNull() ?: 0)).toString()
    }

    private fun intSub(a: String?, b: String?): String {
        return ((a?.toIntOrNull() ?: 0).minus(b?.toIntOrNull() ?: 0)).toString()
    }

//    fun mul() = String.format("%.0f",(firstNumber * secondNumber))

//    fun div() = String.format("%.0f",(firstNumber / secondNumber))
}

fun specialAdd(a: String?, b: String?): String {
    return ((a?.toFloatOrNull() ?: 0f) + (b?.toFloatOrNull() ?: 0f)).toString()
}

fun specialSub(a: String?, b: String?): String {
    return ((a?.toFloatOrNull() ?: 0f).minus(b?.toFloatOrNull() ?: 0f)).toString()
}

fun specialDiv(a: String?, b: String?): String {
    val newB = b?.toFloatOrNull() ?: 0f
    if (newB == 0f) return "0"
    return ((a?.toFloatOrNull() ?: 0f).div(newB)).toString()
}
