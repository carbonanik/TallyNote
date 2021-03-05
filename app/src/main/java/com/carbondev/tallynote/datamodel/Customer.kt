package com.carbondev.tallynote.datamodel

import com.google.firebase.database.Exclude


data class Customer (
    var key         : String  = "",
    var name        : String  = "",
    var createdAt   : String  = "",
    var lastEdited  : Long    = 0,
    var gender      : String  = "",
    var phoneNo     : String  = "",
    var address     : String  = "",
    var totalTransaction : String = "0",
    var totalDue    : String = "0"
//    var detail      : String = ""
){

    @Exclude fun setGender(g : Int){
        gender = when(g) {
            1 -> Gender().male
            2 -> Gender().female
            else -> ""
        }
    }
}

