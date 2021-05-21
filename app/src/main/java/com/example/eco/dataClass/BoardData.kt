package com.example.eco.dataClass

data class BoardData(
        val group_name: String,
        val master_name : String,
        val open_date : String,
        val intro : String,
        var group_pic : String?,
        var meeting_date : ArrayList<String>,
        var group_id : String,
        var participant : ArrayList<String>
        ) {
}