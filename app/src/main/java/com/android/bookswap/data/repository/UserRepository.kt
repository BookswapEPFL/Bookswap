package com.android.bookswap.data.repository

import com.android.bookswap.data.DataUser
import java.util.UUID

/** Interface defining a contract for managing user-related operations in a repository. */
interface UsersRepository {
  /**
   * Function to fetch a list of users from the repository.
   *
   * @param callback callback function that receives list of user if success
   */
  fun getUsers(
      callback: (Result<List<DataUser>>) -> Unit,
  )

  /**
   * Function to fetch a list of users from the repository.
   *
   * @param callback callback function that receives list of user if success
   */
  fun getUser(
      uuid: UUID,
      callback: (Result<DataUser>) -> Unit,
  )

  /**
   * Function to fetch a list of users from the repository.
   *
   * @param googleUid The unique identifier of the user
   * @param callback callback function that receives the DataUser if success
   */
  fun getUser(
      googleUid: String,
      callback: (Result<DataUser>) -> Unit,
  )

  /**
   * Function to add a new user to the repository.
   *
   * @param dataUser The user data to be added.
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun addUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit)

  /**
   * Function to update an existing user in the repository.
   *
   * @param dataUser The user data to be updated.
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun updateUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit)

  /**
   * Function to delete a user from the repository.
   *
   * @param uuid The unique identifier of the user to be deleted.
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun deleteUser(uuid: UUID, callback: (Result<Unit>) -> Unit)

  /**
   * Function to add a contact to the user's contact list in the repository.
   *
   * @param userUUID UUID of the user whose contact list is being updated.
   * @param contactUUID UUID of the contact to add.
   * @param callback Callback for success or failure.
   */
  fun addContact(userUUID: UUID, contactUUID: String, callback: (Result<Unit>) -> Unit)

  /**
   * Function to remove a contact from the user's contact list in the repository.
   *
   * @param userUUID UUID of the user whose contact list is being updated.
   * @param contactUUID UUID of the contact to remove.
   * @param callback Callback for success or failure.
   */
  fun removeContact(userUUID: UUID, contactUUID: String, callback: (Result<Unit>) -> Unit)
}
