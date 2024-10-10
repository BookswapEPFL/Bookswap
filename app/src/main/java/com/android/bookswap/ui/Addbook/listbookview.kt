package com.android.bookswap.ui.Addbook

import com.android.bookswap.data.DataBook
import com.android.bookswap.model.Booksrepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// A view class that interacts with the Books repository to manage a list of books.
open class listToBooksView(private val repository: Booksrepository) {

  // MutableStateFlow to hold the current list of books. It starts with an empty list.
  private val books_ = MutableStateFlow<List<DataBook>>(emptyList())

  // Publicly accessible read-only StateFlow of books, so that the UI can observe changes.
  val books: StateFlow<List<DataBook>> = books_.asStateFlow()

  // MutableStateFlow to hold the currently selected book. Initially, no book is selected (null).
  private val selectbook_ = MutableStateFlow<DataBook?>(null)

  // Publicly accessible read-only StateFlow of the selected book, so the UI can observe the
  // selected book.
  open val selectbook: StateFlow<DataBook?> = selectbook_.asStateFlow()

  // Initialization block. Calls the repository's init() function when the class is instantiated.
  init {
    repository.init {}
  }

  // Function to generate a new unique identifier (UID) for a book, using the repository.
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  // Function to fetch the list of books from the repository.
  // The result is handled via callback:
  // - On success, updates the `books_` state flow with the fetched list.
  // - On failure, it does nothing (could be enhanced to handle failure cases).
  fun getBooks() {
    repository.getbook(OnSucess = { books_.value = it }, onFailure = {})
  }
  // Function to add a new book to the repository.
  // Once the book is successfully added, it triggers the fetch for the updated book list.
  // Failure case is currently not handled (can be improved).
  open fun Add_Book(addBook: DataBook) {
    repository.addBooks(
        addBook,
        OnSucess = { getBooks() }, // On success, refresh the book list.
        onFailure = {} // Failure case is not handled.
        )
  }
}
