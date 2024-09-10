package com.example.teachme.models

class Message(
    val sender: String = "",
    val content: String = "",
    val date: Long = System.currentTimeMillis()
)
