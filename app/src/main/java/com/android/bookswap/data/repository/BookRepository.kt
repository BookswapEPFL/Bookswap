package com.android.bookswap.data.repository

import com.android.bookswap.data.DataBook
import java.util.UUID

// Interface defining a contract for managing book-related operations in a repository.
interface BooksRepository {

  // Function to initialize the repository. This can be used to set up data or resources.
  // This default implementation does nothing, but subclasses can override it.
  fun init(OnSucess: () -> Unit)

  // Function to generate and return a new unique identifier (UID) for a book.
  // This default implementation returns an empty string, but it should be overridden to generate
  // actual UIDs.
  fun getNewUid(): UUID

  // Function to fetch a list of books from the repository.
  // Parameters:
  // - OnSucess: A callback function that receives the list of books when the operation succeeds.
  // - onFailure: A callback function that receives an exception if the operation fails.
  fun getBook(OnSucess: (List<DataBook>) -> Unit, onFailure: (Exception) -> Unit)

  // Function to add a new book to the repository.
  // Parameters:
  // - dataBook: The book data to be added.
  // - OnSucess: A callback function that receives the updated list of books when the operation
  // succeeds.
  // - onFailure: A callback function that receives an exception if the operation fails.
  fun addBook(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit)

  // Function to update an existing book in the repository.
  // Parameters:
  // - dataBook: The book data to be updated.
  // - OnSucess: A callback function that receives the updated list of books when the operation
  // succeeds.
  // - onFailure: A callback function that receives an exception if the operation fails.
  fun updateBook(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit)

  // Function to delete a book from the repository.
  // Parameters:
  // - id: The unique identifier of the book to be deleted.
  // - dataBook: The book data to be deleted (can also just use the ID).
  // - OnSucess: A callback function that receives the updated list of books when the operation
  // succeeds.
  // - onFailure: A callback function that receives an exception if the operation fails.
  fun deleteBooks(
      id: String,
      dataBook: DataBook,
      OnSucess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
