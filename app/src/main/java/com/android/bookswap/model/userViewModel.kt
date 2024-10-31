package com.android.bookswap.model

import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataUser
import java.util.UUID

open class UserViewModel(var email: String) : ViewModel() {
  var uid = "ERROR_UUID"
  private var dataUser = DataUser(email = email)
  private var isLoaded = false

  open fun getUser(force: Boolean = false): DataUser {
    if (!isLoaded || force) {
      fetchUser()
    }
    return dataUser
  }

  private fun fetchUser() {}

  fun updateUser(
      greeting: String = dataUser.greeting,
      firstName: String = dataUser.firstName,
      lastName: String = dataUser.lastName,
      email: String = dataUser.email,
      phone: String = dataUser.phoneNumber,
      latitude: Double = dataUser.latitude,
      longitude: Double = dataUser.longitude,
      picURL: String = dataUser.profilePictureUrl,
      bookList : List<UUID> = dataUser.bookList
  ) {
    updateUser(
        DataUser(greeting, firstName, lastName, email, phone, latitude, longitude, picURL, uid, bookList))
  }

  fun updateUser(newDataUser: DataUser) {
    this.dataUser = newDataUser
    this.uid = newDataUser.userId
  }
}
