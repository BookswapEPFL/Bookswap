package com.android.bookswap.model.chat

import android.content.Context
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.google.gson.Gson
import java.io.File

class OfflineMessageStorage(context: Context) {
  private val messages = mutableListOf<DataMessage>()
  private val messagesFile: File = File(context.filesDir, "Messages.txt")

  fun getMessagesFromText(): MutableList<DataMessage> {
    if (!messagesFile.exists()) return mutableListOf()
    val gson = Gson()
    val jsonString = messagesFile.readText()
    return gson.fromJson(jsonString, Array<DataMessage>::class.java).toMutableList()
  }

  fun setMessages() {
    val gson = Gson()
    val jsonString = gson.toJson(messages)
    messagesFile.writeText(jsonString)
  }

  fun addMessage(message: DataMessage) {
    messages.add(message)
  }

  fun getMessages(): List<DataMessage> {
    return messages
  }

  fun clearMessages() {
    messages.clear()
  }

  fun extractMessages(messages: MutableList<DataMessage>, limit: Int = 10): List<DataMessage> {
    // Sort messages by timestamp in descending order
    val sortedMessages = messages.sortedByDescending { it.timestamp }

    // Initialize a list to hold the result
    val result = mutableListOf<DataMessage>()

    // Keep track of how many TEXT messages we've added
    var textMessageCount = 0

    // Iterate through the sorted messages
    for (message in sortedMessages) {
      if (message.messageType == MessageType.TEXT && textMessageCount < limit) {
        // Add TEXT messages if we haven't reached the limit
        result.add(message)
        textMessageCount++
      } else if (message.messageType == MessageType.IMAGE) {
        // Always add IMAGE messages
        result.add(message)
      }

      // Stop the loop if we've added 10 TEXT messages and all IMAGE messages in between
      if (textMessageCount >= limit) {
        break
      }
    }

    // Return the result in chronological order (optional, if needed)
    return result.sortedBy { it.timestamp }
  }
}
