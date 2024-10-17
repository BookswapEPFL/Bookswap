package com.android.bookswap.data

data class DataUser(
    var greeting: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var profilePictureUrl: String = "",
    var userId: String = ""
) {

  fun printFullname(): String {
    return "$greeting $firstName $lastName:"
  }
}
