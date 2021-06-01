package com.example.eco.dataClass

data class SnsData (
        val sns_name: String,
        val sns_pic: String,
        val sns_intro:String,
        var thumsup: String,
        val writter : String,
        var isFaveriteClick: Boolean? = false
        )