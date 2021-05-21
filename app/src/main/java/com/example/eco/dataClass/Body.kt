package com.example.eco.dataClass

data class Body(
        val items: List<Item>,
        val numOfRows: Int,
        val pageNo: Int,
        val totalCount: Int
)