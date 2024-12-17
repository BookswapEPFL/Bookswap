package com.android.bookswap.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the user data of the application.
 *
 * @param uuid UUID of the user
 * @param repository Repository to fetch user data
 */
open class UserViewModel(
    var uuid: UUID,
    repository: UsersRepository = UserFirestoreSource(FirebaseFirestore.getInstance()),
    private var dataUser: DataUser = DataUser(uuid) // Allows easier testing
) : ViewModel() {
  private var isLoaded = false
  private val _isStored = MutableStateFlow<Boolean?>(null)
  val isStored: StateFlow<Boolean?> = _isStored
  private val userRepository: UsersRepository = repository
  val addressStr = MutableStateFlow("")

  open fun getUser(force: Boolean = false): DataUser {
    if (!isLoaded || force) {
      fetchUser()
    }
    Log.d("getUser", "Fetched user: $uuid")
    return dataUser
  }

  private fun fetchUser() {
    userRepository.getUser(uuid) { result ->
      result.onSuccess {
        dataUser = it
        isLoaded = true
        _isStored.value = true
      }
    }
  }

  /**
   * Update the user data with the given parameters. If no parameter is given, the data will not be
   * updated.
   */
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
      googleUid: String = dataUser.googleUid,
      contactList: List<UUID> = dataUser.contactList
  ) {
    updateUser(
        DataUser(
            dataUser.userUUID,
            greeting,
            firstName,
            lastName,
            email,
            phone,
            latitude,
            longitude,
            picURL,
            bookList,
            googleUid,
            contactList))
  }
  /**
   * Update the user data with the given DataUser object.
   *
   * @param newDataUser New user data
   */
  fun updateUser(newDataUser: DataUser) {
    this.dataUser = newDataUser
    this.uuid = newDataUser.userUUID
    isLoaded = true
    userRepository.updateUser(dataUser) { result ->
      result.fold({ _isStored.value = true }, { _isStored.value = false })
    }
  }

  /** Get the user by the googleUid */
  fun getUserByGoogleUid(googleUid: String) {
    userRepository.getUser(googleUid) { result ->
      // If the user is found, update the dataUser and set isLoaded to true
      result.onSuccess {
        dataUser = it
        uuid = dataUser.userUUID
        isLoaded = true
        _isStored.value = true
      }
      // If the user is not found, set isLoaded to false
      result.onFailure {
        Log.e("UserViewModel", "User not found")
        isLoaded = false
        _isStored.value = false
      }
    }
  }
  /**
   * Update the googleUid of the user.
   *
   * @param googleUid New googleUid
   */
  fun updateGoogleUid(googleUid: String) {
    dataUser.googleUid = googleUid
    updateUser(dataUser)
  }

  fun updateAddress(latitude: Double, longitude: Double, context: Context) {
    dataUser.latitude = latitude
    dataUser.longitude = longitude
    val handleAddresses: (MutableList<Address>?) -> Unit = {
      if (!it.isNullOrEmpty()) {
        addressStr.value =
            it.first().let {
              var s = ""
              for (i in 0..it.maxAddressLineIndex) {
                s += (it.getAddressLine(i))
              }
              s
            }
      }
    }

    viewModelScope.launch {
      val geocoder = Geocoder(context)
      val geocodeListener = Geocoder.GeocodeListener(handleAddresses)
      withContext(Dispatchers.IO) {
        // Perform geocoding on a background thread
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          geocoder.getFromLocation(latitude, longitude, 1, geocodeListener)
        } else {
          handleAddresses(geocoder.getFromLocation(latitude, longitude, 1))
        }
      }
    }
  }
}
