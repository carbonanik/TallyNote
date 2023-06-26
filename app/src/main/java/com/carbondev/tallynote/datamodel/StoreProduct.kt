package com.carbondev.tallynote.datamodel

data class StoreProduct(
    var key: String = "",
    var name: String = "",
    var createdAt: String = "",
    var lastEdited: Long = 0,
    var countInStock: String? = "",
    var productPrice: String? = "",
    var unitPrice: String? = "",
) {

//    fun copyWith(
//        key: String?,
//        name: String?,
//        createdAt: String?,
//        lastEdited: Long?,
//        countInStock: String?,
//        productPrice: String?
//    ): StoreProduct {
//        return StoreProduct(
//            key = key ?: this.key,
//            name = name ?: this.name,
//            createdAt = createdAt ?: this.createdAt,
//            lastEdited = lastEdited ?: this.lastEdited,
//            countInStock = countInStock ?: this.countInStock,
//            productPrice = productPrice ?: this.productPrice
//        )
//    }
}