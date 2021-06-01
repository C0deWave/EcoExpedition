package com.example.eco.dataClass

data class UserInfo(
    val pic : String,
    val pswd: String,
    val email: String,
    val name: String,
    val p_group: Array<String>,
    val fav_list : ArrayList<String>?
)