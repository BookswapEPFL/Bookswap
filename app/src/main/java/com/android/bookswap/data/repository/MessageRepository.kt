package com.android.bookswap.data.repository

import android.content.Context
import com.android.bookswap.data.DataMessage
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID

interface MessageRepository {

  /** Generates a new unique id for a message */
  fun getNewUUID(): UUID

  /**
   * Initialize the repository
   *
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun init(callback: (Result<Unit>) -> Unit)

  /**
   * Get all messages as a list
   *
   * @param callback callback function that receives list of messages if success
   */
  fun getMessages(
      callback: (Result<List<DataMessage>>) -> Unit,
  )

  /**
   * Add a message to the repository
   *
   * @param message message to be added
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun sendMessage(message: DataMessage, callback: (Result<Unit>) -> Unit)

  /**
   * Delete a message from the repository
   *
   * @param messageUUID UUID of message to be deleted
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun deleteMessage(messageUUID: UUID, callback: (Result<Unit>) -> Unit, context: Context)

  /**
   * Delete all messages of this chat from the repository
   *
   * @param user1Id id of the first user
   * @param user2Id id of the second user
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun deleteAllMessages(user1Id: String, user2Id: String, callback: (Result<Unit>) -> Unit)

  /**
   * Update a message in the repository
   *
   * @param message message to be updated
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun updateMessage(message: DataMessage, callback: (Result<Unit>) -> Unit, context: Context)

  /**
   * Add a listener to the repository to get messages in real-time
   *
   * @param otherUserId id of the other user
   * @param currentUserId id of the current user
   * @param callback callback function that receives list of messages if success
   * @return ListenerRegistration object that can be used to remove the listener
   */
  fun addMessagesListener(
      otherUserId: String,
      currentUserId: String,
      callback: (Result<List<DataMessage>>) -> Unit
  ): ListenerRegistration
}
