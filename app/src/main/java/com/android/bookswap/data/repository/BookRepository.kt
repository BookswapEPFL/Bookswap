package com.android.bookswap.data.repository

import com.android.bookswap.data.DataBook
import java.util.UUID

/** Interface defining a contract for managing book-related operations in a repository. */
interface BooksRepository {
  /**
   * Function to initialize the repository. This can be used to set up data or resources. This
   * default implementation does nothing, but subclasses can override it.
   *
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun init(callback: (Result<Unit>) -> Unit)

  /**
   * Function to generate and return a new unique identifier (UID) for a book. This default
   * implementation returns an empty string, but it should be overridden to generate actual UIDs.*
   */
  fun getNewUid(): String

  /**
   * Function to fetch a list of books from the repository.
   *
   * @param callback callback function that receives list of book if success
   */
  fun getBook(
      callback: (Result<List<DataBook>>) -> Unit,
  )

  /**
   * Function to add a new book to the repository.
   *
   * @param dataBook The book data to be added.
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun addBooks(dataBook: DataBook, callback: (Result<Unit>) -> Unit)

  /**
   * Function to update an existing book in the repository.
   *
   * @param dataBook The book data to be updated.
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun updateBook(dataBook: DataBook, callback: (Result<Unit>) -> Unit)

  /**
   * Function to delete a book from the repository.
   *
   * @param uuid The unique identifier of the book to be deleted.
   * @param callback callback function that receives Result.success() when operation succeed of
   *   Result.failure(exception) if error
   */
  fun deleteBook(uuid: UUID, callback: (Result<Unit>) -> Unit)
}
