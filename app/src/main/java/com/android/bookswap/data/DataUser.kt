package com.android.bookswap.data

import java.util.UUID

/**
 * Data class representing a user.
 *
 * @property userUUID The unique identifier of the user.
 * @property greeting The greeting message of the user.
 * @property firstName The first name of the user.
 * @property lastName The last name of the user.
 * @property email The email address of the user.
 * @property phoneNumber The phone number of the user.
 * @property latitude The latitude of the user's location.
 * @property longitude The longitude of the user's location.
 * @property profilePictureUrl The URL of the user's profile picture.
 * @property bookList The list of book UUIDs associated with the user.
 * @property googleUid The Google UID of the user.
 */
data class DataUser(
    var userUUID: UUID = UUID.randomUUID(),
    var greeting: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var profilePictureUrl: String = "",
    var bookList: List<UUID> = emptyList(),
    var googleUid: String = ""
) {
  /**
   * Returns the full name of the user.
   *
   * @return The full name of the user in the format "greeting firstName lastName".
   */
  fun printFullname(): String {
    return "$greeting $firstName $lastName"
  }
}
