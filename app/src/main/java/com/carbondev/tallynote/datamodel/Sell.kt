package com.carbondev.tallynote.datamodel


data class Sell (
    var key          : String = "",
    var customerKey  : String = "",
    var customerName : String = "",
    var date         : String = "",
    var totalPrice   : String = "0",
    var paid         : String = "0",
    var due          : String = "0",
    var beforeDue    : String = "0",
    var afterDue     : String = "0",
    var note         : String = "",
    var type         : String = typeProduct,
    var products     : ArrayList<Product> = arrayListOf(),
    var payment      : String = "0"//,
//    var dueAfterPayment : String = "0"
)