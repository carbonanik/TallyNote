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

//    fun mul() = String.format("%.0f",(firstNumber * secondNumber))

//    fun div() = String.format("%.0f",(firstNumber / secondNumber))
}

//class SCal2(private val initialNumber : String = "0") {
//    private var resault  = initialNumber.toFloat()

//    fun initNum(initialNumber: String){
//        resault = initialNumber.toFloat()
//    }

//    fun add(newNumber : String): String {
//        resault += newNumber.toFloat()
//        return resault.toString()
//    }

//    fun sub(newNumber : String): String {
//        resault -= newNumber.toFloat()
//        return resault.toString()
//    }

//    fun mul(newNumber : String): String {
//        resault *= newNumber.toFloat()
//        return resault.toString()
//    }

//    fun div(newNumber : String): String {
//        resault /= newNumber.toFloat()
//        return resault.toString()
//    }

//    fun equal(): String {
//        return resault.toString()
//    }
//}

//fun main(){
//    val sc = SCal2()
//    sc.initNum("10")
//    println(sc.add("10"))
//}