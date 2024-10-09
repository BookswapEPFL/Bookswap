package com.android.bookswap.model.chat

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MessageRepositoryFirestore(private val db: FirebaseFirestore) : MessageRepository {
  private val collectionPath = "messages"

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    try {
      onSuccess()
    } catch (e: Exception) {
      Log.e("MessageRepository", "Initialization failed: ${e.message}")
    }
  }

  override fun getMessages(onSuccess: (List<Message>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath).get().addOnCompleteListener { result ->
      if (result.isSuccessful) {
        val messages =
            result.result!!.mapNotNull { documentSnapshot -> toMessage(documentSnapshot) }
        onSuccess(messages)
      } else {
        onFailure(result.exception ?: Exception("Unknown error fetching messages"))
      }
    }
  }

  override fun sendMessage(
      message: Message,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val messageMap =
        mapOf(
            "id" to message.id,
            "text" to message.text,
            "senderId" to message.senderId,
            "timestamp" to message.timestamp)

    db.collection(collectionPath).document(message.id).set(messageMap).addOnCompleteListener {
        result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        onFailure(result.exception ?: Exception("Unknown error sending message"))
      }
    }
  }
}

fun toMessage(document: DocumentSnapshot): Message? {
  return try {
    val id = document.getString("id") ?: return null
    val text = document.getString("text") ?: return null
    val senderId = document.getString("senderId") ?: return null
    val timestamp = document.getLong("timestamp") ?: return null
    Message(id, text, senderId, timestamp)
  } catch (e: Exception) {
    Log.e("MessageRepository", "Error converting document to Message: ${e.message}")
    null
  }
}
