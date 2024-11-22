package com.android.bookswap.data

import java.util.UUID

/**
 * Represents a message with various properties
 *
 * @param messageType Type of the message (default is TEXT)
 * @param uuid Unique identifier for the message (default is a random UUID)
 * @param text Content of the message (default is an empty string)
 * @param senderUUID Unique identifier of the sender
 * @param receiverUUID Unique identifier of the receiver
 * @param timestamp Timestamp of when the message was sent (default is 0L)
 */
data class DataMessage(
    val messageType: MessageType = MessageType.TEXT,
    val uuid: UUID = UUID.randomUUID(),
    val text: String = "",
    val senderUUID: UUID,
    val receiverUUID: UUID,
    val timestamp: Long = 0L
)

/** Enum representing the type of a message. */
enum class MessageType {
  TEXT,
  IMAGE
}

/** Data class for the message box */
data class MessageBox(val contact: DataUser, val message: String? = null, val date: String? = null)
