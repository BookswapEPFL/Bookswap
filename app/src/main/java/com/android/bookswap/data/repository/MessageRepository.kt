package com.android.bookswap.data.repository

import com.android.bookswap.data.DataMessage

interface MessageRepository {

  /** Generates a new unique id for a message */
  fun getNewUid(): String

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
}
