package com.android.bookswap.data

import java.util.UUID

data class DataMessage(
    val messageType: MessageType = MessageType.TEXT,
    val uuid: UUID = UUID.randomUUID(),
    val text: String = "",
    val senderUUID: UUID,
    val receiverUUID: UUID,
    val timestamp: Long = 0L
)

enum class MessageType {
  TEXT,
  IMAGE
}
