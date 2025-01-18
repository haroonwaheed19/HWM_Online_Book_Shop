package com.hwm.hwmonlinebookshop
data class CartItem(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var author: String = "",
    var price: String = "",
    var quantity: Int = 1,
    var imageUrl: String = ""
)