package com.android.bookswap.data.source.network

import android.util.Log
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

/** Name of the Firestore collection that stores users */
private const val COLLECTION_NAME = "Users"

/** Implement [UsersRepository] interface using Firebase's Firestore as the data source */
class UserFirestoreSource(private val db: FirebaseFirestore) : UsersRepository {

  /**
   * Initializes the repository by adding an auth state listener to Firebase Authentication If the
   * user is authenticated, it triggers the OnSuccess callback
   */
  override fun init(callback: (Result<Unit>) -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        Log.i("current firebase user", it.currentUser!!.uid)
        callback(Result.success(Unit))
      }
      callback(Result.failure(Exception("User is not logged in firestore")))
    }
  }
  /** Generates and returns a new unique document ID for a user in Firestore */
  override fun getNewUid(): String {
    return db.collection(COLLECTION_NAME).document().id
  }
  /**
   * Fetches the list of users from the Firestore collection If the task is successful, maps the
   * Firestore documents to DataUser objects Calls OnSuccess with the list of users, or onFailure if
   * the task fails
   */
  override fun getUsers(callback: (Result<List<DataUser>>) -> Unit) {
    db.collection(COLLECTION_NAME).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataUser objects or returns an empty list
        val users =
            task.result?.mapNotNull { document ->
              val result = documentToUser(document)
              result.getOrNull()
            } ?: emptyList()
        callback(Result.success(users))
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
  override fun getUser(uuid: String, callback: (Result<DataUser>) -> Unit) {

    db.collection(COLLECTION_NAME).whereEqualTo("UUID", uuid).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Maps Firestore documents to DataUser objects or returns an empty list
        val users =
            task.result?.mapNotNull { document ->
              val result = documentToUser(document)
              result.getOrNull()
            } ?: emptyList()
        callback(Result.success(users.first()))
      } else {
        callback(Result.failure(task.exception!!))
      }
    }
  }

  /** Adds a new user to the Firestore collection */
  override fun addUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(dataUser.userId).set(dataUser),
        callback,
    )
  }
  /**
   * Updates an existing user in Firestore by replacing the document with the same title Uses
   * performFirestoreOperation to handle success and failure
   */
  override fun updateUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    performFirestoreOperation(
        db.collection(COLLECTION_NAME).document(dataUser.userId).set(dataUser), callback)
  }

  /**
   * Deletes a user from Firestore by its title Uses performFirestoreOperation to handle success and
   * failure
   */
  override fun deleteUser(uuid: String, callback: (Result<Unit>) -> Unit) {
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
      val userID = document.getString("UUID")!!
      val greeting = document.getString("Greeting")!!
      val firstname = document.getString("Firstname")!!
      val lastname = document.getString("Lastname")!!
      val email = document.getString("Email")!!
      val phoneNumber = document.getString("Phone")!!
      val latitude = document.getDouble("Latitude")!!
      val longitude = document.getDouble("Longitude")!!
      val profilePicture = document.getString("Picture")!!
      Result.success(
          DataUser(
              greeting,
              firstname,
              lastname,
              email,
              phoneNumber,
              latitude,
              longitude,
              profilePicture,
              userID))
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
