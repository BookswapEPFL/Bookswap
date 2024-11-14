package com.android.bookswap.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import java.util.concurrent.CountDownLatch
/**
 * ViewModel for the user data of the application.
 *
 * @param uuid UUID of the user
 * @param repository Repository to fetch user data
 */

open class UserViewModel(
    var uuid: UUID,
    repository: UsersRepository = UserFirestoreSource(FirebaseFirestore.getInstance())
) : ViewModel() {
  private var dataUser = DataUser(uuid)
  private var isLoaded = false
  var isStored = false
  private val userRepository: UsersRepository = repository

  open fun getUser(force: Boolean = false): DataUser {
    if (!isLoaded || force) {
      fetchUser()
    }
    return dataUser
  }

  private fun fetchUser() {
    userRepository.getUser(uuid) { result ->
      result.onSuccess {
        dataUser = it
        isLoaded = true
        isStored = true
      }
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
      picURL: String = dataUser.profilePictureUrl,
      bookList: List<UUID> = dataUser.bookList,
      googleUid: String = dataUser.googleUid
  ) {
    updateUser(
        DataUser(
            uuid,
            greeting,
            firstName,
            lastName,
            email,
            phone,
            latitude,
            longitude,
            picURL,
            bookList,
            googleUid))
  }

  fun updateUser(newDataUser: DataUser) {
    this.dataUser = newDataUser
    this.uuid = newDataUser.userUUID
    isLoaded = true
    userRepository.updateUser(dataUser) { result ->
      result.fold({ isStored = true }, { isStored = false })
    }
  }

    fun addUser() {
    userRepository.addUser(this.dataUser) { result ->
      result.fold({ Log.d("UserViewModel", "User added successfully") }, { Log.e("UserViewModel", "Error adding user") })
    }
  }

    fun getUserByGoogleUid(googleUid: String) {
        userRepository.getUser(googleUid) { result ->
            // If the user is found, update the dataUser and set isLoaded to true
            result.onSuccess {
                dataUser = it
                isLoaded = true
                isStored = true
            }
            //If the user is not found, set isLoaded to false
            result.onFailure {
                isLoaded = true
            }
        }
    }

    fun updateGoogleUid(googleUid: String) {
        dataUser.googleUid = googleUid
        updateUser(dataUser)
    }
}
