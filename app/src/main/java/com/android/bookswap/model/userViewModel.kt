package com.android.bookswap.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore

open class UserViewModel(
    var uuid: String = "ERROR_UUID",
    repository: UsersRepository = UserFirestoreSource(FirebaseFirestore.getInstance())
) : ViewModel() {
  private var dataUser = DataUser(uuid)
  private var isLoaded = false
  private val userRepository: UsersRepository = repository

  open fun getUser(force: Boolean = false): DataUser {
    if (!isLoaded || force) {
      fetchUser()
    }
    return dataUser
  }

  private fun fetchUser() {
    userRepository.getUser(uuid) { result ->
      result.fold(
          {
            Log.i("UserViewModel", "User fetched successfully")
            dataUser = it
            isLoaded = true
          },
          { Log.e("UserViewModel", "Failed to fetch user from remote source", it) })
    }
  }

  fun updateUser(
      greeting: String = dataUser.greeting,
      firstName: String = dataUser.firstName,
      lastName: String = dataUser.lastName,
      email: String = dataUser.email,
      phone: String = dataUser.phoneNumber,
      latitude: Double = dataUser.latitude,
      longitude: Double = dataUser.longitude,
      picURL: String = dataUser.profilePictureUrl
  ) {
    updateUser(
        DataUser(greeting, firstName, lastName, email, phone, latitude, longitude, picURL, uuid))
  }

  fun updateUser(newDataUser: DataUser) {
    this.dataUser = newDataUser
    this.uuid = newDataUser.userId
    isLoaded = true
    userRepository.updateUser(dataUser) { result ->
      result.fold(
          { Log.i("UserViewModel", "User updated successfully") },
          { Log.e("UserViewModel", "Failed to update user on remote source", it) })
    }
  }
}
