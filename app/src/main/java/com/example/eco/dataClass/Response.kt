package com.example.eco.dataClass

import com.example.eco.dataClass.Body
import com.example.eco.dataClass.Header

data class Response(
        val body: Body,
        val header: Header
)