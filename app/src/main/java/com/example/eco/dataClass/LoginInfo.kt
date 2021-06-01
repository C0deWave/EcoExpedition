package com.example.eco.dataClass

data class LoginInfo(
    val pic : String,
    val pswd: String,
    val email: String,
    val name: String,
    val p_group: Array<String>,
)