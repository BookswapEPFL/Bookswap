package com.android.bookswap.model

import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

open class UserViewModel(
    var uuid: UUID,
    repository: UsersRepository = UserFirestoreSource(FirebaseFirestore.getInstance())
) : ViewModel() {
  private var dataUser = DataUser(uuid)
  private var isLoaded = false
  var isStored = false
  private val userRepository: UsersRepository = repository
  /**
   * Retrieves the user data.
   *
   * @param force if true, forces a fetch from the repository even if data is already loaded.
   * @return the user data.
   */
  open fun getUser(force: Boolean = false): DataUser {
    if (!isLoaded || force) {
      fetchUser()
    }
    return dataUser
  }
  /** Fetches the user data from the repository. */
  private fun fetchUser() {
    userRepository.getUser(uuid) { result ->
      result.onSuccess {
        dataUser = it
        isLoaded = true
        isStored = true
      }
    }
  }
  /**
   * Updates the user data with the provided parameters.
   *
   * @param greeting the greeting message of the user.
   * @param firstName the first name of the user.
   * @param lastName the last name of the user.
   * @param email the email address of the user.
   * @param phone the phone number of the user.
   * @param latitude the latitude of the user's location.
   * @param longitude the longitude of the user's location.
   * @param picURL the URL of the user's profile picture.
   * @param bookList the list of book UUIDs associated with the user.
   * @param googleUid the Google UID of the user.
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
  /**
   * Updates the user data with the provided `DataUser` object.
   *
   * @param newDataUser the new user data to update.
   */
  fun updateUser(newDataUser: DataUser) {
    this.dataUser = newDataUser
    this.uuid = newDataUser.userUUID
    isLoaded = true
    userRepository.updateUser(dataUser) { result ->
      result.fold({ isStored = true }, { isStored = false })
    }
  }
}
