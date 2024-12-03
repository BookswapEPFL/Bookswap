package com.android.bookswap.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

/**
 * ViewModel for the user data of the application.
 *
 * @param uuid UUID of the user
 * @param repository Repository to fetch user data
 */
open class OthersUserViewModel(
    var uuid: UUID,
    private val usersRepository: UsersRepository =
        UserFirestoreSource(FirebaseFirestore.getInstance())
) : ViewModel() {

  /**
   * Fetches user data by user ID using a single callback.
   *
   * @param userId The UUID of the user to fetch.
   * @param callback Callback invoked with the user data if found, or null if not.
   */
  fun getUserByUUID(userId: UUID, callback: (DataUser?) -> Unit) {
    usersRepository.getUser(userId) { result ->
      result
          .onSuccess { user ->
            Log.i("OthersUserViewModel", "User fetched: $user")
            callback(user) // Pass the fetched user to the callback.
          }
          .onFailure { error ->
            Log.e("OthersUserViewModel", "Failed to fetch user: ${error.message}")
            callback(null) // Pass null to the callback in case of failure.
          }
    }
  }
}
