package com.example.eco

data class Body(
    val items: List<Item>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)