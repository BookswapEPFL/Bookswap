package com.android.bookswap.model.chat

import android.util.Log
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatScreenViewModel {

  /**
   * Formats a timestamp into a readable string.
   *
   * @param timestamp The timestamp to format.
   * @return A formatted string representing the timestamp. If the timestamp is from today, it
   *   returns the time in "HH:mm" format. Otherwise, it returns the date in "MMM dd, yyyy" format.
   */
  fun formatTimestamp(timestamp: Long): String {
    val messageDate = Date(timestamp)
    val currentDate = Date()
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateTimeFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    return if (dateFormat.format(messageDate) == dateFormat.format(currentDate)) {
      timeFormat.format(messageDate)
    } else {
      dateTimeFormat.format(messageDate)
    }
  }

  /**
   * Adds the other user to the current user's contacts and vice versa.
   *
   * @param userSource The repository to fetch and update user data.
   * @param currentUser The current user.
   * @param otherUser The other user to add to the current user's contacts.
   */
  fun addContacts(userSource: UsersRepository, currentUser: DataUser, otherUser: DataUser) {
    // Add otherUser to currentUser's contacts
    userSource.getUser(currentUser.userUUID) { result ->
      if (result.isSuccess) {
        val updatedUser = result.getOrThrow()
        if (!updatedUser.contactList.contains(otherUser.userUUID.toString())) {
          userSource.addContact(currentUser.userUUID, otherUser.userUUID.toString()) { contactResult
            ->
            if (contactResult.isSuccess) {
              Log.d(
                  "ChatScreen", "Added ${otherUser.userUUID} to ${currentUser.userUUID}'s contacts")
            } else {
              Log.e(
                  "ChatScreen",
                  "Failed to add contact: ${contactResult.exceptionOrNull()?.message}")
            }
          }
        }
      } else {
        Log.e("ChatScreen", "Failed to fetch current user: ${result.exceptionOrNull()?.message}")
      }
    }

    // Add currentUser to otherUser's contacts
    userSource.getUser(otherUser.userUUID) { result ->
      if (result.isSuccess) {
        val updatedOtherUser = result.getOrThrow()
        if (!updatedOtherUser.contactList.contains(currentUser.userUUID.toString())) {
          userSource.addContact(otherUser.userUUID, currentUser.userUUID.toString()) { contactResult
            ->
            if (contactResult.isSuccess) {
              Log.d(
                  "ChatScreen", "Added ${currentUser.userUUID} to ${otherUser.userUUID}'s contacts")
            } else {
              Log.e(
                  "ChatScreen",
                  "Failed to add contact: ${contactResult.exceptionOrNull()?.message}")
            }
          }
        }
      } else {
        Log.e("ChatScreen", "Failed to fetch other user: ${result.exceptionOrNull()?.message}")
      }
    }
  }
}
