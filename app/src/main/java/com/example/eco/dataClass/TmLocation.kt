package com.example.eco.dataClass

import com.example.eco.dataClass.Document
import com.example.eco.dataClass.Meta

data class TmLocation(
        val documents: List<Document>,
        val meta: Meta
)