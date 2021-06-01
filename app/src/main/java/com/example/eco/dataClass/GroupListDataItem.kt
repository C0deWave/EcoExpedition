package com.example.eco.dataClass

data class GroupListDataItem(
    val group_name: String,
    val master_name: String,
    val open_date: String,
    val intro: String,
    val group_pic:String,
    val meeting_date: String,
    val meeting_type: String,
    val meeting_intro: String,
    val participant: ArrayList<String>,
    val dona: String,
    val dona_all: String,
    val loc: String,
)