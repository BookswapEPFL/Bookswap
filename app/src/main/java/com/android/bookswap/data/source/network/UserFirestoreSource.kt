package com.android.bookswap.data.source.network

import android.util.Log
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

/** Name of the Firestore collection that stores users */
private const val COLLECTION_NAME = "Users"

/** Implement [UsersRepository] interface using Firebase's Firestore as the data source */
class UserFirestoreSource(private val db: FirebaseFirestore) : UsersRepository {
  /**
   * Fetches the list of users from the Firestore collection If the task is successful, maps the
   * Firestore documents to DataUser objects Calls OnSuccess with the list of users, or onFailure if
   * the task fails
   */
  override fun getUsers(callback: (Result<List<DataUser>>) -> Unit) {
    db.collection(COLLECTION_NAME).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataUser objects or returns an empty list
        callback(
            Result.success(
                task.result?.mapNotNull { documentToUser(it).getOrNull() } ?: emptyList()))
      } else {
        callback(Result.failure(task.exception!!))
      }
    }
  }
  /**
   * Fetches the list of users from the Firestore collection If the task is successful, maps the
   * Firestore documents to DataUser objects Calls OnSuccess with the list of users, or onFailure if
   * the task fails
   */
  override fun getUser(uuid: UUID, callback: (Result<DataUser>) -> Unit) {

    db.collection(COLLECTION_NAME).whereEqualTo("userUUID", uuid).get().addOnCompleteListener { task
      ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataUser objects or returns an empty list
        callback(
            Result.success(
                task.result?.firstNotNullOfOrNull { documentToUser(it).getOrNull() } ?: DataUser()))
      } else {
        callback(Result.failure(task.exception!!))
      }
    }
  }
  /**
   * Fetches the list of users from the Firestore collection If the task is successful, maps the
   * Firestore documents to DataUser objects Calls OnSuccess with the list of users, or onFailure if
   * the task fails
   */
  override fun getUser(googleUid: String, callback: (Result<DataUser>) -> Unit) {
    db.collection(COLLECTION_NAME)
        .whereEqualTo("googleUid", googleUid)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Log.d("TAG_USR_GET_BY_GUID", "usr count: ${task.result?.size()}")
            val user = task.result?.firstNotNullOfOrNull { documentToUser(it).getOrNull() }
            if (user != null) {
              callback(Result.success(user))
            } else {
              callback(
                  Result.failure(
                      NoSuchElementException("No user found with googleUID: $googleUid")))
            }
          } else {
            callback(Result.failure(task.exception ?: Exception("Unknown error occurred")))
          }
        }
  }

  /** Adds a new user to the Firestore collection */
  override fun addUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(dataUser.userUUID.toString()).set(dataUser),
        callback,
    )
  }
  /**
   * Updates an existing user in Firestore by replacing the document with the same title Uses
   * performFirestoreOperation to handle success and failure
   */
  override fun updateUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(dataUser.userUUID.toString()).set(dataUser),
        callback)
  }

  /**
   * Deletes a user from Firestore by its title Uses performFirestoreOperation to handle success and
   * failure
   */
  override fun deleteUser(uuid: UUID, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(uuid.toString()).delete(), callback)
  }
  /**
   * Maps a Firestore document to a DataUser object If any required field is missing, returns null
   * to avoid incomplete objects
   *
   * @return DataUser on success, otherwise error
   */
  fun documentToUser(document: DocumentSnapshot): Result<DataUser> {

    return try {
      val mostSignificantBits = document.getLong("userUUID.mostSignificantBits")!!
      val leastSignificantBits = document.getLong("userUUID.leastSignificantBits")!!
      val greeting = document.getString("greeting")!!
      val firstname = document.getString("firstName")!!
      val lastname = document.getString("lastName")!!
      val email = document.getString("email")!!
      val phoneNumber = document.getString("phoneNumber")!!
      val latitude = document.getDouble("latitude")!!
      val longitude = document.getDouble("longitude")!!
      val profilePicture = document.getString("profilePictureUrl")!!
      val googleUid = document.getString("googleUid")!!
      // Log.d("TAG_DOC2USR", "GUID: $googleUid")
      val bookList =
          (document.get("bookList") as List<Map<String, Long>>).map { bookMap ->
            Log.d("TAG_BOOK_MAP", "bookMap: $bookMap")
            val mostSigBits = bookMap["mostSignificantBits"]
            val leastSigBits = bookMap["leastSignificantBits"]
            if (mostSigBits != null && leastSigBits != null) {
              UUID(mostSigBits, leastSigBits)
            } else {
              null
            }
          }
      val contactList =
          try {
            (document.get("contactList") as? List<String>) ?: emptyList()
          } catch (e: Exception) {
            Log.e("TAG_CONTACT_LIST_ERROR", "Error parsing contactList: ${e.message}")
            emptyList()
          }

      if (bookList.any { it == null }) {
        throw IllegalArgumentException("Book list contains null UUIDs")
      }

      Result.success(
          DataUser(
              UUID(mostSignificantBits, leastSignificantBits),
              greeting,
              firstname,
              lastname,
              email,
              phoneNumber,
              latitude,
              longitude,
              profilePicture,
              bookList.filterNotNull(),
              googleUid,
              contactList.filterNotNull()))
    } catch (e: Exception) {
      Log.e("FirestoreSource", "Error converting document to User: ${e.message}")
      Result.failure(e)
    }
  }
  /**
   * Helper function to perform Firestore operations (add, update, delete) Executes the provided
   * Firestore task and triggers success or failure callbacks
   *
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  private fun performFirestoreOperation(task: Task<Void>, callback: (Result<Unit>) -> Unit) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        callback(Result.success(Unit))
      } else {
        result.exception?.let { e -> callback(Result.failure(e)) }
      }
    }
  }
}
