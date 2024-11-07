package com.android.bookswap.data

import java.util.UUID

data class DataMessage(
    val uuid: UUID = UUID.randomUUID(),
    val text: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = 0L
)
