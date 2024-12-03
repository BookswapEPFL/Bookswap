package com.android.bookswap.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageBox
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for the contact data of the application.
 *
 * @param userVM UserViewModel object
 * @param userFirestoreSource UserFirestoreSource object
 * @param messageFirestoreSource MessageFirestoreSource object
 */
class ContactViewModel(
    val userVM: UserViewModel,
    private val userFirestoreSource: UsersRepository =
        UserFirestoreSource(FirebaseFirestore.getInstance()),
    private val messageFirestoreSource: MessageRepository =
        MessageFirestoreSource(FirebaseFirestore.getInstance())
) : ViewModel() {

  private val _messageBoxMap = MutableStateFlow<Map<UUID, MessageBox>>(emptyMap())

  val messageBoxMap: StateFlow<Map<UUID, MessageBox>> = _messageBoxMap

  /** returns the user in the message box map with the given UUID */
  fun getUserInMessageBoxMap(uuid: UUID): DataUser? {
    return _messageBoxMap.value[uuid]?.contact
  }
  /** Updates the message box map with the latest messages for each contact */
  fun updateMessageBoxMap() {
    try {
      val currentUserUUID = userVM.getUser().userUUID

      // Iterate through the contact list
      userVM.getUser().contactList.forEach { contactUUID ->

        // Fetch messages between the current user and each contact
        messageFirestoreSource.getMessages(currentUserUUID, contactUUID) { result ->
          result.fold(
              onSuccess = { messagesForContact ->
                // Exclude IMAGE type messages
                val filteredMessages =
                    messagesForContact.filter { it.messageType == MessageType.TEXT }

                // Fetch user details for the contact
                userFirestoreSource.getUser(contactUUID) { userResult ->
                  userResult.fold(
                      onSuccess = { contactUser ->
                        // Extract the last message details
                        val lastMessage = filteredMessages.lastOrNull()
                        val lastMessageText = lastMessage?.text ?: ""
                        val lastMessageTimestamp = lastMessage?.timestamp?.toString() ?: ""

                        // Update the message box map
                        _messageBoxMap.value =
                            _messageBoxMap.value.toMutableMap().apply {
                              this[contactUUID] =
                                  MessageBox(contactUser, lastMessageText, lastMessageTimestamp)
                            }
                      },
                      onFailure = {
                        Log.e(
                            "ContactViewModel", "Error getting user for contact $contactUUID: $it")
                      })
                }
              },
              onFailure = {
                Log.e("ContactViewModel", "Error getting messages for contact $contactUUID: $it")
              })
        }
      }
    } catch (e: Exception) {
      Log.e("ContactViewModel", "Error updating message box map: ${e.message}")
    }
  }
}
