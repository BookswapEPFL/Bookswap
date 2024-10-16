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
  override fun toString(): String {
    return "$greeting $firstName $lastName"
  }

  fun printFull1Line(): String {
    return "${this.toString()}:" +
        "¦$greeting|$firstName|$lastName" +
        "¦$email|$phoneNumber" +
        "¦$latitude, $longitude" +
        "¦$profilePictureUrl" +
        "¦$userId"
  }

  fun printFullMultiLine(): String {
    return printFull1Line().replace("¦", "\n  ")
  }
}
