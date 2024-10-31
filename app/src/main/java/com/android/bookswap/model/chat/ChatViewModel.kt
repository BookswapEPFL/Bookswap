package com.android.bookswap.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.repository.MessageRepository
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing chat messages between users. Handles message fetching, sending, and
 * real-time updates.
 */
class ChatViewModel(private val messageRepository: MessageRepository) : ViewModel() {

  // Holds the list of chat messages, observed by the UI
  private val _messages = MutableStateFlow<List<DataMessage>>(emptyList())
  val messages: StateFlow<List<DataMessage>> = _messages

  // Listener registration for real-time message updates
  private var listenerRegistration: ListenerRegistration? = null

  // ID of the other user in the chat conversation
  private var otherUserId: String = ""

  // Initializes the ViewModel by loading messages for the initial user
  init {
    loadMessages()
  }

  /**
   * Loads messages for the current `otherUserId`. Fetches messages asynchronously from the
   * repository and updates `_messages`.
   */
  fun loadMessages() {
    viewModelScope.launch {
      messageRepository.fetchMessagesForUser(otherUserId) { result ->
        if (result.isSuccess) {
          _messages.value = result.getOrThrow()
        } else {
          Log.e("MessageView", "Failed to fetch messages: ${result.exceptionOrNull()?.message}")
        }
      }
    }
  }

  /**
   * Generates a new unique ID for a message.
   *
   * @return A UUID string to be used as a unique identifier.
   */
  fun getNewUid(): String {
    return UUID.randomUUID().toString()
  }

  /**
   * Changes the `otherUserId` to load messages from a new user. Clears current messages to avoid
   * displaying outdated messages.
   *
   * @param newOtherUserId The ID of the new user to load messages for.
   */
  fun changeOtherUserId(newOtherUserId: String) {
    otherUserId = newOtherUserId
    _messages.value = emptyList() // Clear current messages
    loadMessages()
  }

  /**
   * Provides the current `otherUserId`.
   *
   * @return The ID of the other user in the chat.
   */
  fun getOtherUserId(): String {
    return otherUserId
  }

  /**
   * Sets up a listener for real-time message updates between `currentUserId` and `otherUserId`.
   * Updates `_messages` whenever a new message is received.
   *
   * @param currentUserId The ID of the current user in the chat.
   */
  fun addMessagesListener(currentUserId: String) {
    viewModelScope.launch {
      listenerRegistration =
          messageRepository.addMessagesListener(otherUserId, currentUserId) { result ->
            if (result.isSuccess) {
              _messages.value = result.getOrThrow()
            } else {
              Log.e("MessageView", "Failed to fetch messages: ${result.exceptionOrNull()?.message}")
            }
          }
    }
  }

  /**
   * Cleans up the message listener when the ViewModel is destroyed. Removes the listener to avoid
   * memory leaks.
   */
  public override fun onCleared() {
    super.onCleared()
    listenerRegistration?.remove()
    listenerRegistration = null
  }
 fun givemessagerepo(): MessageRepository {
    return messageRepository
  }
  /**
   * Sends a new message and adds it to `_messages` if successful.
   *
   * @param newMessage The new message to be sent.
   */
  fun sendMessage(newMessage: DataMessage) {
    viewModelScope.launch {
      messageRepository.sendMessage(newMessage) { result ->
        if (result.isSuccess) {
          _messages.value += newMessage // Add message to the list
        } else {
          Log.e("MessageView", "Failed to send message: ${result.exceptionOrNull()?.message}")
        }
      }
    }
  }
}
