package com.android.bookswap.model.chat

data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = 0L
)
