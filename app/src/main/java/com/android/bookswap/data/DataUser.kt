package com.android.bookswap.data

import java.util.UUID

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
    var bookList: List<UUID> = emptyList()
) {

  fun printFullname(): String {
    return "$greeting $firstName $lastName"
  }
}
