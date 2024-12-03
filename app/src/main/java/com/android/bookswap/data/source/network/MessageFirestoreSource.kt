package com.android.bookswap.data.source.network

import android.util.Log
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.repository.MessageRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID

const val COLLECTION_PATH = "chats"
const val fifteenMinutesInMillis = 15 * 60 * 1000
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
   * @param user1UUID UUID of the first user.
   * @param user2UUID UUID of the second user.
   * @param callback Callback to be invoked with the result of the operation. The result is a list
   *   of DataMessage objects on success, or an exception on failure.
   * @return A list of messages between the two users.
   */
  override fun getMessages(
      user1UUID: UUID,
      user2UUID: UUID,
      callback: (Result<List<DataMessage>>) -> Unit
  ) {
    val chatPath = mergeUUIDs(user1UUID, user2UUID)

    db.collection(COLLECTION_PATH)
        .document(chatPath)
        .collection("messages")
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful && task.result != null) {
            val messages = task.result!!.documents.mapNotNull { documentToMessage(it).getOrNull() }
            callback(Result.success(messages))
          } else {
            callback(Result.failure(task.exception ?: Exception("Error fetching messages")))
          }
        }
  }

  /**
   * Sends a message to the Firestore database.
   *
   * @param dataMessage The message to be sent.
   * @param callback Callback to be invoked with the result of the operation. The result is a
   *   success if the message is sent successfully, or an exception on failure.
   */
  override fun sendMessage(dataMessage: DataMessage, callback: (Result<Unit>) -> Unit) {
    val messageDocument = messageToDocument(dataMessage)
    // Generate the chat path using the consistent `mergeUUIDs` order
    val chatPath = mergeUUIDs(dataMessage.senderUUID, dataMessage.receiverUUID)

    // Store the message in the correct chat subdivision
    db.collection(COLLECTION_PATH)
        .document(chatPath)
        .collection("messages")
        .document(dataMessage.uuid.toString())
        .set(messageDocument)
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            callback(Result.success(Unit))
          } else {
            callback(Result.failure(result.exception ?: Exception("Error sending message")))
          }
        }
  }
  /**
   * Deletes a message from the Firestore database.
   *
   * @param messageUUID The UUID of the message to be deleted.
   * @param user1UUID The UUID of the first user.
   * @param user2UUID The UUID of the second user.
   * @param callback Callback to be invoked with the result of the operation. The result is a
   *   success if the message is deleted successfully, or an exception on failure.
   * @param context The context for displaying Toast messages.
   */
  override fun deleteMessage(
      messageUUID: UUID,
      user1UUID: UUID,
      user2UUID: UUID,
      callback: (Result<Unit>) -> Unit
  ) {
    // Generate the chat path using the consistent `mergeUUIDs` order
    val chatPath = mergeUUIDs(user1UUID, user2UUID)

    // Fetch the message to check the deletion time window
    db.collection(COLLECTION_PATH)
        .document(chatPath)
        .collection("messages")
        .document(messageUUID.toString())
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful && task.result != null && task.result!!.exists()) {
            val document = task.result!!
            val existingMessage = documentToMessage(document).getOrNull()

            if (existingMessage != null) {
              val currentTime = System.currentTimeMillis()

              // Check if the message is within the 15-minute deletion window
              if (currentTime - existingMessage.timestamp <= fifteenMinutesInMillis) {
                // Delete the message
                db.collection(COLLECTION_PATH)
                    .document(chatPath)
                    .collection("messages")
                    .document(messageUUID.toString())
                    .delete()
                    .addOnCompleteListener { deleteTask ->
                      if (deleteTask.isSuccessful) {
                        callback(Result.success(Unit))
                      } else {
                        callback(
                            Result.failure(
                                deleteTask.exception ?: Exception("Error deleting message")))
                      }
                    }
              } else {
                // Notify the user that the deletion window has passed
                callback(
                    Result.failure(
                        Exception("Message can only be deleted within 15 minutes of being sent")))
              }
            } else {
              callback(Result.failure(Exception("Message not found")))
            }
          } else {
            callback(
                Result.failure(task.exception ?: Exception("Error fetching message for deletion")))
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
    // Generate the chat path using the consistent `mergeUUIDs` order
    val chatPath = mergeUUIDs(user1UUID, user2UUID)

    // Fetch all messages in the chat subdivision
    db.collection(COLLECTION_PATH)
        .document(chatPath)
        .collection("messages")
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful && task.result != null) {
            val batch = db.batch()
            val documents = task.result!!.documents

            // Add each document to the batch for deletion
            for (document in documents) {
              batch.delete(document.reference)
            }

            // Commit the batch
            batch.commit().addOnCompleteListener { deleteTask ->
              if (deleteTask.isSuccessful) {
                callback(Result.success(Unit))
              } else {
                callback(
                    Result.failure(deleteTask.exception ?: Exception("Error deleting messages")))
              }
            }
          } else {
            callback(
                Result.failure(task.exception ?: Exception("Error fetching messages for deletion")))
          }
        }
  }
  /**
   * Updates a message in the Firestore database.
   *
   * @param dataMessage The message to be updated.
   * @param message The message to be updated.
   * @param user1UUID The UUID of the first user.
   * @param user2UUID The UUID of the second user.
   * @param callback Callback to be invoked with the result of the operation. The result is a
   *   success if the message is updated successfully, or an exception on failure.
   * @param context The context for displaying Toast messages.
   */
  override fun updateMessage(
      dataMessage: DataMessage,
      user1UUID: UUID,
      user2UUID: UUID,
      callback: (Result<Unit>) -> Unit
  ) {
    // Generate the chat path using the consistent `mergeUUIDs` order
    val chatPath = mergeUUIDs(user1UUID, user2UUID)

    db.collection(COLLECTION_PATH)
        .document(chatPath)
        .collection("messages")
        .document(dataMessage.uuid.toString())
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val document = task.result
            if (document != null && document.exists()) {
              val existingMessage = documentToMessage(document).getOrNull()
              if (existingMessage != null) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - existingMessage.timestamp <= fifteenMinutesInMillis) {
                  val messageMap = messageToDocument(dataMessage)
                  db.collection(COLLECTION_PATH)
                      .document(dataMessage.uuid.toString())
                      .update(messageMap)
                      .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                          callback(Result.success(Unit))
                        } else {
                          callback(
                              Result.failure(
                                  updateTask.exception
                                      ?: Exception("Unknown error updating message")))
                        }
                      }
                } else {
                  callback(
                      Result.failure(
                          Exception("Message can only be updated within 15 minutes of being sent")))
                }
              } else {
                callback(Result.failure(Exception("Message not found")))
              }
            } else {
              callback(
                  Result.failure(task.exception ?: Exception("Error fetching message for update")))
            }
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
 * Maps a DataMessage object to a Firebase document-like Map
 *
 * @param dataMessage The object to convert into a Map
 * @return Map<String,Any?> A Mapping of each of the DataMessage object fields to it's value,
 *   properly formatted for storing
 */
fun messageToDocument(dataMessage: DataMessage): Map<String, Any?> {
  return mapOf(
      "uuid" to DataConverter.convert_UUID(dataMessage.uuid),
      "text" to dataMessage.text,
      "senderUUID" to DataConverter.convert_UUID(dataMessage.senderUUID),
      "receiverUUID" to DataConverter.convert_UUID(dataMessage.receiverUUID),
      "timestamp" to DataConverter.convert_Long(dataMessage.timestamp),
      "messageType" to dataMessage.messageType.name)
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
    val uuid = DataConverter.parse_raw_UUID(document.get("uuid").toString())!!
    val text = document.getString("text")!!
    val senderUUID = DataConverter.parse_raw_UUID(document.get("senderUUID").toString())!!
    val receiverUUID = DataConverter.parse_raw_UUID(document.get("receiverUUID").toString())!!
    val timestamp = DataConverter.parse_raw_long(document.get("timestamp").toString())!!
    Result.success(DataMessage(type, uuid, text, senderUUID, receiverUUID, timestamp))
  } catch (e: Exception) {
    Log.e("MessageSource", "Error converting document to Message: ${e.message}")
    Result.failure(e)
  }
}

/**
 * Merges two UUIDs into a single string.
 *
 * @param uuid1 The first UUID.
 * @param uuid2 The second UUID.
 * @return A string containing both UUIDs.
 */
private fun mergeUUIDs(uuid1: UUID, uuid2: UUID): String {
  return if (uuid1.toString() < uuid2.toString()) {
    "${uuid1}_$uuid2"
  } else {
    "${uuid2}_$uuid1"
  }
}
