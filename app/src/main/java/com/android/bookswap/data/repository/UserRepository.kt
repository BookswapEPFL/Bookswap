package com.android.bookswap.data.repository

import com.android.bookswap.data.DataUser

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
      uuid: String,
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
  fun deleteUser(uuid: String, callback: (Result<Unit>) -> Unit)
}
