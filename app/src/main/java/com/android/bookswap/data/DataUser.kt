package com.android.bookswap.data

import java.util.UUID

/**
 * Represents a user with various properties.
 *
 * @param userUUID Unique identifier for the user
 * @param greeting Greeting message for the user
 * @param firstName First name of the user
 * @param lastName Last name of the user
 * @param email Email address of the user
 * @param phoneNumber Phone number of the user
 * @param latitude Latitude of the user's location
 * @param longitude Longitude of the user's location
 * @param profilePictureUrl URL of the user's profile picture
 * @param bookList List of UUIDs representing the user's books
 * @param googleUid Google UID of the user
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
   * Returns the full name of the user in the format: "greeting firstName lastName".
   *
   * @return A string representing the full name of the user.
   */
  fun printFullname(): String {
    return "$greeting $firstName $lastName"
  }
}
