package com.android.bookswap.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageBox
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the contact data of the application.
 *
 * @param userVM UserViewModel object
 * @param userFirestoreSource UserFirestoreSource object
 * @param messageFirestoreSource MessageFirestoreSource object
 */

class ContactViewModel(val userVM: UserViewModel = UserViewModel(UUID.randomUUID()), val userFirestoreSource: UsersRepository = UserFirestoreSource(
    FirebaseFirestore.getInstance()), val messageFirestoreSource: MessageRepository = MessageFirestoreSource(FirebaseFirestore.getInstance())) :
    ViewModel() {

    private val _messageBoxMap = MutableStateFlow<Map<UUID, MessageBox>>(emptyMap())

    val messageBoxMap: StateFlow<Map<UUID, MessageBox>> = _messageBoxMap

    /**
     * Updates the message box map with the latest messages for each contact
     */
    fun updateMessageBoxMap() {

            try {

                var messages: List<DataMessage> = emptyList()
                messageFirestoreSource.getMessages(){
                        result -> result.fold(onSuccess = {messages = it}, onFailure = { error -> Log.e("ContactViewModel", "Error getting messages: $error") })
                }


                userVM.getUser().contactList.forEach { contactUUID ->
                    userFirestoreSource.getUser(contactUUID) { userResult ->
                        userResult.fold(
                            onSuccess = { contactUser ->
                                val filtered = messages.filter { message ->
                                    (message.senderUUID == userVM.getUser().userUUID && message.receiverUUID == contactUUID) ||
                                            (message.senderUUID == contactUUID && message.receiverUUID == userVM.getUser().userUUID)
                                }.sortedBy { it.timestamp }

                                val lastMessageText = filtered.lastOrNull()?.text ?: ""
                                val lastMessageTimestamp =
                                    filtered.lastOrNull()?.timestamp.toString()

                                _messageBoxMap.value = _messageBoxMap.value.toMutableMap().apply {
                                    this[contactUUID] = MessageBox(
                                        contactUser,
                                        lastMessageText,
                                        lastMessageTimestamp
                                    )
                                }
                            },
                            onFailure = {
                                Log.e("ContactViewModel", "Error getting user")
                            }
                        )
                    }
                }} catch (e: Exception) {
                Log.e("ContactViewModel", "Error updating message box map: ${e.message}")
            }

    }

}