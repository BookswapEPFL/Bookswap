package com.android.bookswap.data.repository

import com.android.bookswap.data.DataBook
import java.util.UUID

/**
 * Interface defining a contract for managing book-related operations in a repository.
 */
interface BooksRepository {

  /**
   * Function to initialize the repository. This can be used to set up data or resources.
   * This default implementation does nothing, but subclasses can override it.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Function to generate and return a new unique identifier (UUID) for a book.
   * This default implementation returns an empty string, but it should be overridden to generate
   * actual UUIDs.
   */
  fun getNewUUID(): UUID

    /**
     * Function to fetch a list of books from the repository.
     * @param callback A callback function that receives the list of books when the operation succeeds
     * or an exception if it fails
      */
  fun getBook(callback: (Result<List<DataBook>>) -> Unit)

    /**
     * Function to add a new book to the repository
     * @param dataBook The book data to be added
     * @param callback A callback function that receives an exception if the operation fails
      */
  fun addBook(dataBook: DataBook, callback: (Result<Unit>) -> Unit)

    /**
     * Function to update an existing book in the repository.
     * @param dataBook The book data to be updated
     * @param callback A callback function that receives an exception if the operation fails
      */
  fun updateBook(dataBook: DataBook, callback: (Result<Unit>) -> Unit)

    /**
     * Function to delete a book from the repository.
     * @param uuid The unique identifier of the book to be deleted.
     * @param dataBook The book data to be deleted (can also just use the uuid).
     * @param callback A callback function that receives an exception if the operation fails.
     */
  fun deleteBooks(
      uuid: UUID,
      dataBook: DataBook,
      callback: (Result<Unit>) -> Unit
  )
}
