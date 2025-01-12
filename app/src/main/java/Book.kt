package com.hwm.hwmonlinebookshop

data class Book(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val author: String = "",
    val price: Double = 0.0,
    val imageUrl: String = ""
)