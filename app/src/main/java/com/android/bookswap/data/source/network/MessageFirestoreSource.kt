package com.android.bookswap.data.source.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.repository.MessageRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID

const val COLLECTION_PATH = "messages"
/**
 * Firestore source for managing messages.
 *
 * @param db The Firestore database instance.
 */
class MessageFirestoreSource(private val db: FirebaseFirestore) : MessageRepository {
  /**
   * Generates a new UUID.
   *
   * @return A randomly generated UUID.
   */
  override fun getNewUUID(): UUID {
    return UUID.randomUUID()
  }
  /**
   * Firestore source for managing messages.
   *
   * @param db The Firestore database instance.
   */
  override fun init(callback: (Result<Unit>) -> Unit) {
    try {
      callback(Result.success(Unit))
    } catch (e: Exception) {
      Log.e("MessageSource", "Initialization failed: ${e.message}")
      callback(Result.failure(e))
    }
  }
  /**
   * Fetches all messages from the Firestore database.
   *
   * @param callback Callback to be invoked with the result of the operation. The result is a list
   *   of DataMessage objects on success, or an exception on failure.
   */
  override fun getMessages(callback: (Result<List<DataMessage>>) -> Unit) {
    db.collection(COLLECTION_PATH).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val documents = task.result?.documents
        if (!documents.isNullOrEmpty()) {
          val messages = documents.mapNotNull { documentToMessage(it).getOrNull() }
          callback(Result.success(messages))
        } else {
          callback(Result.success(emptyList()))
        }
      } else {
        callback(Result.failure(task.exception ?: Exception("Unknown error fetching messages")))
      }
    }
  }
  /**
   * Sends a message to the Firestore database.
   *
   * @param message The message to be sent.
   * @param callback Callback to be invoked with the result of the operation. The result is a
   *   success if the message is sent successfully, or an exception on failure.
   */
  override fun sendMessage(message: DataMessage, callback: (Result<Unit>) -> Unit) {
    val messageMap =
        mapOf(
            "uuid" to message.uuid.toString(),
            "text" to message.text,
            "senderUUID" to message.senderUUID.toString(),
            "receiverUUID" to message.receiverUUID.toString(),
            "timestamp" to message.timestamp,
            "messageType" to message.messageType.name)

    db.collection(COLLECTION_PATH)
        .document(message.uuid.toString())
        .set(messageMap)
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            callback(Result.success(Unit))
          } else {
            callback(Result.failure(result.exception ?: Exception("Unknown error sending message")))
          }
        }
  }
  /**
   * Deletes a message from the Firestore database.
   *
   * @param messageUUID The UUID of the message to be deleted.
   * @param callback Callback to be invoked with the result of the operation. The result is a
   *   success if the message is deleted successfully, or an exception on failure.
   */
  override fun deleteMessage(
      messageUUID: UUID,
      callback: (Result<Unit>) -> Unit,
      context: Context
  ) {
    val fifteenMinutesInMillis = 15 * 60 * 1000
    val currentTime = System.currentTimeMillis()

    db.collection(COLLECTION_PATH).document(messageUUID.toString()).get().addOnCompleteListener {
        task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null && document.exists()) {
          val existingMessage = documentToMessage(document).getOrNull()
          if (existingMessage != null) {
            if (currentTime - existingMessage.timestamp <= fifteenMinutesInMillis) {
              db.collection(COLLECTION_PATH)
                  .document(messageUUID.toString())
                  .delete()
                  .addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                      callback(Result.success(Unit))
                    } else {
                      callback(
                          Result.failure(
                              deleteTask.exception ?: Exception("Unknown error deleting message")))
                    }
                  }
            } else {
              Toast.makeText(
                      context,
                      "Message can only be deleted within 15 minutes of being sent",
                      Toast.LENGTH_LONG)
                  .show()
              callback(
                  Result.failure(
                      Exception("Message can only be deleted within 15 minutes of being sent")))
            }
          } else {
            callback(Result.failure(Exception("Message not found")))
          }
        } else {
          callback(Result.failure(Exception("Message not found")))
        }
      } else {
        callback(Result.failure(task.exception ?: Exception("Unknown error fetching message")))
      }
    }
  }
  /**
   * Deletes all messages between two users from the Firestore database.
   *
   * @param user1UUID The UUID of the first user.
   * @param user2UUID The UUID of the second user.
   * @param callback Callback to be invoked with the result of the operation. The result is a
   *   success if all messages are deleted successfully, or an exception on failure.
   */
  override fun deleteAllMessages(
      user1UUID: UUID,
      user2UUID: UUID,
      callback: (Result<Unit>) -> Unit
  ) {
    db.collection(COLLECTION_PATH)
        .whereIn("senderUUID", listOf(user1UUID, user2UUID))
        .whereIn("receiverUUID", listOf(user1UUID, user2UUID))
        .whereNotEqualTo("senderUUID", "receiverUUID")
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val documents = task.result
            if (documents != null && !documents.isEmpty) {
              val batch = db.batch()
              documents.documents.forEach { document -> batch.delete(document.reference) }
              batch.commit().addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                  callback(Result.success(Unit))
                } else {
                  callback(
                      Result.failure(
                          deleteTask.exception ?: Exception("Unknown error deleting messages")))
                }
              }
            } else {
              callback(Result.success(Unit))
            }
          } else {
            callback(Result.failure(task.exception ?: Exception("Unknown error fetching messages")))
          }
        }
  }
  /**
   * Updates a message in the Firestore database.
   *
   * @param message The message to be updated.
   * @param callback Callback to be invoked with the result of the operation. The result is a
   *   success if the message is updated successfully, or an exception on failure.
   * @param context The context for displaying Toast messages.
   */
  override fun updateMessage(
      message: DataMessage,
      callback: (Result<Unit>) -> Unit,
      context: Context
  ) {
    val fifteenMinutesInMillis = 15 * 60 * 1000
    val currentTime = System.currentTimeMillis()

    db.collection(COLLECTION_PATH).document(message.uuid.toString()).get().addOnCompleteListener {
        task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null && document.exists()) {
          val existingMessage = documentToMessage(document).getOrNull()
          if (existingMessage != null) {
            if (currentTime - existingMessage.timestamp <= fifteenMinutesInMillis) {
              val messageMap =
                  mapOf(
                      "text" to message.text,
                      "timestamp" to currentTime,
                      "messageType" to message.messageType.name)
              db.collection(COLLECTION_PATH)
                  .document(message.uuid.toString())
                  .update(messageMap)
                  .addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                      callback(Result.success(Unit))
                    } else {
                      callback(
                          Result.failure(
                              updateTask.exception ?: Exception("Unknown error updating message")))
                    }
                  }
            } else {
              Toast.makeText(
                      context,
                      "Message can only be updated within 15 minutes of being sent",
                      Toast.LENGTH_LONG)
                  .show()
              callback(
                  Result.failure(
                      Exception("Message can only be updated within 15 minutes of being sent")))
            }
          } else {
            callback(Result.failure(Exception("Message not found")))
          }
        } else {
          callback(Result.failure(Exception("Message not found")))
        }
      } else {
        callback(Result.failure(task.exception ?: Exception("Unknown error fetching message")))
      }
    }
  }
  /**
   * Adds a listener for real-time updates to messages between two users.
   *
   * @param otherUserUUID The UUID of the other user.
   * @param currentUserUUID The UUID of the current user.
   * @param callback Callback to be invoked with the result of the operation. The result is a list
   *   of DataMessage objects on success, or an exception on failure.
   * @return A ListenerRegistration object to manage the listener.
   */
  override fun addMessagesListener(
      otherUserUUID: UUID,
      currentUserUUID: UUID,
      callback: (Result<List<DataMessage>>) -> Unit
  ): ListenerRegistration {
    return db.collection("messages")
        .whereIn("senderId", listOf(currentUserUUID, otherUserUUID))
        .whereIn("receiverId", listOf(currentUserUUID, otherUserUUID))
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
/**
 * Converts a Firestore document to a DataMessage object.
 *
 * @param document The Firestore document to be converted.
 * @return A Result containing a DataMessage object if all required fields are present, or an
 *   exception on failure.
 */
fun documentToMessage(document: DocumentSnapshot): Result<DataMessage> {
  return try {
    val type = MessageType.valueOf(document.getString("messageType")!!)
    val uuid = UUID.fromString(document.getString("uuid")!!)
    val text = document.getString("text")!!
    val senderUUID = UUID.fromString(document.getString("senderUUID")!!)
    val receiverUUID = UUID.fromString(document.getString("receiverUUID")!!)
    val timestamp = document.getLong("timestamp")!!
    Result.success(DataMessage(type, uuid, text, senderUUID, receiverUUID, timestamp))
  } catch (e: Exception) {
    Log.e("MessageSource", "Error converting document to Message: ${e.message}")
    Result.failure(e)
  }
}
