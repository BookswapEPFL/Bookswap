package com.android.bookswap.data.chat

interface MessageRepository {

  /** Generates a new unique id for a message */
  fun getNewUid(): String

  /**
   * Initialize the repository
   *
   * @param onSuccess callback to be called when initialization is successful
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Get all messages as a list
   *
   * @param onSuccess callback to be called when messages are successfully fetched
   * @param onFailure callback to be called when messages cannot be fetched
   */
  fun getMessages(onSuccess: (List<Message>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Add a message to the repository
   *
   * @param message message to be added
   * @param onSuccess callback to be called when message is successfully added
   * @param onFailure callback to be called when message cannot be added
   */
  fun sendMessage(message: Message, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
