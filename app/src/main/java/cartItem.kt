package com.hwm.hwmonlinebookshop

data class CartItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val author: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String = ""
)