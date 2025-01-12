package com.hwm.hwmonlinebookshop

data class Book(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var author: String = "",
    var price: Double = 0.0,
    var imageUrl: String = ""
)