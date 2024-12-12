package com.android.bookswap.model.edit

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.navigation.NavigationActions
import java.util.UUID

/**
 * ViewModel class for handling the logic to edit, fetch, and delete books in the repository.
 *
 * @property booksRepository The repository managing book-related operations.
 * @property navigation Provides navigation actions for UI transitions.
 * @property userVM The UserViewModel instance used to retrieve the current user's information.
 */
class EditBookViewModel(
    private val booksRepository: BooksRepository,
    private val navigation: NavigationActions,
    private val userVM: UserViewModel
) : ViewModel() {
  /**
   * Creates a DataBook instance after validating the input parameters.
   *
   * @param context The context of the screen
   * @param title The title of the book.
   * @param author The author of the book.
   * @param description The description of the book.
   * @param ratingStr The rating of the book as a string.
   * @param photo The URL of the book's photo.
   * @param language The language of the book as a string.
   * @param isbn The ISBN of the book.
   * @param genres The list of genres the book belongs to.
   * @param archived Indicates if the book is archived.
   * @param exchange Indicates if the book is available for exchange.
   * @return A DataBook instance if all validations pass, null otherwise.
   */
  fun updateDataBook(
      context: Context,
      uuid: UUID,
      title: String,
      author: String,
      description: String,
      ratingStr: String,
      photo: String,
      language: BookLanguages,
      isbn: String,
      genres: List<BookGenres>,
      archived: Boolean,
      exchange: Boolean
  ) {
    val book =
        DataBook(
            uuid,
            title,
            author,
            description,
            ratingStr.toIntOrNull(),
            photo,
            language,
            isbn,
            genres,
            userVM.getUser().userUUID,
            archived,
            exchange)
    Log.d("EditBookViewModel", "Editing book: $book")
    booksRepository.updateBook(
        book,
        callback = {
          if (it.isSuccess) {
            Toast.makeText(context, "Book updated.", Toast.LENGTH_LONG).show()
            navigation.goBack()
          } else {
            Toast.makeText(context, "Failed to update book.", Toast.LENGTH_LONG).show()
          }
        })
  }

  /** Fetches a book from the repository by UUID. */
  fun getBook(uuid: UUID, onSuccess: (DataBook) -> Unit, onFailure: () -> Unit) {
    booksRepository.getBook(uuid, OnSucess = { onSuccess(it) }, onFailure = { onFailure() })
  }

  /** Deletes a book from the repository by UUID. */
  fun deleteBook(context: Context, uuid: UUID) {
    booksRepository.deleteBook(uuid) {
      if (it.isSuccess) {
        Toast.makeText(context, "Book deleted.", Toast.LENGTH_LONG).show()
        navigation.goBack()
      } else {
        Toast.makeText(context, "Failed to delete book.", Toast.LENGTH_LONG).show()
      }
    }
  }
}
