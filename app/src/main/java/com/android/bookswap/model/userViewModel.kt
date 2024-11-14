package com.android.bookswap.model

import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.BooksFirestoreSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

open class UserViewModel(
    var uuid: UUID,
    firebaseFirestore: FirebaseFirestore,
) : ViewModel() {
  val repository: UsersRepository = UserFirestoreSource(firebaseFirestore)
  val booksRepository: BooksRepository = BooksFirestoreSource(firebaseFirestore)
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

  fun getBooks(): List<DataBook> {
    return booksRepository.getBooksList(dataUser.bookList, {})
  }
}
