package com.example.eco.dataClass

data class BodyX(
        val items: List<ItemX>,
        val numOfRows: Int,
        val pageNo: Int,
        val totalCount: Int
)