package com.android.bookswap.data

import java.util.UUID

/**
 * Data class representing a message.
 *
 * @property messageType The type of the message (TEXT or IMAGE).
 * @property uuid The unique identifier of the message.
 * @property text The text content of the message.
 * @property senderUUID The unique identifier of the sender.
 * @property receiverUUID The unique identifier of the receiver.
 * @property timestamp The timestamp of when the message was sent.
 */
data class DataMessage(
    val messageType: MessageType = MessageType.TEXT,
    val uuid: UUID = UUID.randomUUID(),
    val text: String = "",
    val senderUUID: UUID,
    val receiverUUID: UUID,
    val timestamp: Long = 0L
)
/** Enum class representing the type of a message. */
enum class MessageType {
  TEXT,
  IMAGE
}
