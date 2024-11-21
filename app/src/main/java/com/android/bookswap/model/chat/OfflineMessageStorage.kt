package com.android.bookswap.model.chat

import android.content.Context
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.google.gson.Gson
import java.io.File
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class OfflineMessageStorage(context: Context) {
  private val messages = mutableListOf<DataMessage>()
  private val messagesFile: File = File(context.filesDir, "Messages.txt")

  // Secret key for encryption (you might want to generate and securely store this)
  private val secretKey: SecretKey = generateKey()

  // Generate a symmetric encryption key
  internal fun generateKey(): SecretKey {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(256) // Use AES-256
    return keyGen.generateKey()
  }

  // Encrypt a string
  internal fun encrypt(data: String): ByteArray {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher.doFinal(data.toByteArray(Charsets.UTF_8))
  }

  // Decrypt a byte array
  internal fun decrypt(data: ByteArray): String {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    return String(cipher.doFinal(data), Charsets.UTF_8)
  }

  fun getMessagesFromText(): MutableList<DataMessage> {
    if (!messagesFile.exists()) return mutableListOf()
    return try {
      val encryptedData = messagesFile.readBytes()
      val jsonString = decrypt(encryptedData)
      val gson = Gson()
      gson.fromJson(jsonString, Array<DataMessage>::class.java).toMutableList()
    } catch (e: Exception) {
      e.printStackTrace()
      mutableListOf()
    }
  }

  fun setMessages() {
    val gson = Gson()
    val jsonString = gson.toJson(messages)
    try {
      val encryptedData = encrypt(jsonString)
      messagesFile.writeBytes(encryptedData)
    } catch (e: IOException) {
      throw IOException("Failed to write messages to file: ${messagesFile.absolutePath}", e)
    }
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
