package com.android.bookswap.data.source.network

import android.util.Log
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.repository.MessageRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

const val COLLECTION_PATH = "messages"

class MessageFirestoreSource(private val db: FirebaseFirestore) : MessageRepository {

  override fun getNewUid(): String {
    return db.collection(COLLECTION_PATH).document().id
  }

  override fun init(callback: (Result<Unit>) -> Unit) {
    try {
      callback(Result.success(Unit))
    } catch (e: Exception) {
      Log.e("MessageSource", "Initialization failed: ${e.message}")
      callback(Result.failure(e))
    }
  }

  override fun getMessages(callback: (Result<List<DataMessage>>) -> Unit) {
    db.collection(COLLECTION_PATH).get().addOnCompleteListener { response ->
      if (response.isSuccessful) {
        val messages =
            response.result?.mapNotNull { document -> documentToMessage(document).getOrNull() }
                ?: emptyList()
        callback(Result.success(messages))
      } else {
        callback(Result.failure(response.exception ?: Exception("Unknown error fetching messages")))
      }
    }
  }

  override fun sendMessage(message: DataMessage, callback: (Result<Unit>) -> Unit) {
    val messageMap =
        mapOf(
            "id" to message.id,
            "text" to message.text,
            "senderId" to message.senderId,
            "receiverId" to message.receiverId,
            "timestamp" to message.timestamp)

    db.collection(COLLECTION_PATH).document(message.id).set(messageMap).addOnCompleteListener {
        result ->
      if (result.isSuccessful) {
        callback(Result.success(Unit))
      } else {
        callback(Result.failure(result.exception ?: Exception("Unknown error sending message")))
      }
    }
  }

  override fun addMessagesListener(
      otherUserId: String,
      currentUserId: String,
      callback: (Result<List<DataMessage>>) -> Unit
  ): ListenerRegistration {
    return db.collection("messages")
        .whereIn("senderId", listOf(currentUserId, otherUserId))
        .whereIn("receiverId", listOf(currentUserId, otherUserId))
        .whereNotEqualTo("senderId", "receiverId")
        .addSnapshotListener { snapshot, e ->
          if (e != null) {
            callback(Result.failure(e))
            return@addSnapshotListener
          }

          if (snapshot != null && !snapshot.isEmpty) {
            val messages =
                snapshot.documents
                    .mapNotNull { document ->
                      try {
                        document.toObject(DataMessage::class.java)
                      } catch (ex: Exception) {
                        Log.e(
                            "MessageSource", "Error converting document to Message: ${ex.message}")
                        null
                      }
                    }
                    .sortedBy { it.timestamp } // Sort messages by timestamp
            callback(Result.success(messages))
          } else {
            callback(Result.success(emptyList()))
          }
        }
  }
}

fun documentToMessage(document: DocumentSnapshot): Result<DataMessage> {
  return try {
    val id = document.getString("id")!!
    val text = document.getString("text")!!
    val senderId = document.getString("senderId")!!
    val receiverId = document.getString("receiverId")!!
    val timestamp = document.getLong("timestamp")!!
    Result.success(DataMessage(id, text, senderId, receiverId, timestamp))
  } catch (e: Exception) {
    Log.e("MessageSource", "Error converting document to Message: ${e.message}")
    Result.failure(e)
  }
}
