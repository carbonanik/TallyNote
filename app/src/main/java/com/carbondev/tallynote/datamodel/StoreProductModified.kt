package com.carbondev.tallynote.datamodel

data class StoreProductModified(
    var key: String = "",
    var storeProductKey: String = "",
    var dateTime: String? = "",
    var isAdded: Boolean? = true,
    var count: String? = "",
    var price: String? = "",
    var note: String? = "",
    var lastEdited: Long? = 0
)
