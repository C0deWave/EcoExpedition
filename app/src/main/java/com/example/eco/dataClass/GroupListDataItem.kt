package com.example.eco.dataClass

data class GroupListDataItem(
    val group_name: String,
    val intro: String,
    val master_name: String,
    val meeting_date: String,
    val open_date: String,
    val participant: List<String>,
    val group_pic:String
)