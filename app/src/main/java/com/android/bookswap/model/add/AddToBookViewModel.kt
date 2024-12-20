package com.android.bookswap.model.add

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.android.bookswap.R
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.isNetworkAvailableForBook
import java.util.UUID

/**
 * ViewModel class for handling the logic to add a new book to the repository.
 *
 * @property booksRepository The repository to manage book-related operations with the database.
 * @property userVM The UserViewModel instance used to retrieve the current user details.
 */
class AddToBookViewModel(
    private val booksRepository: BooksRepository,
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
   * @return A DataBook instance if all validations pass, null otherwise.
   */
  fun saveDataBook(
      context: Context,
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
    if (!isNetworkAvailableForBook(context)) {
      Toast.makeText(
              context, context.getString(R.string.add_book_toast_no_connection), Toast.LENGTH_LONG)
          .show()
    }
    val book =
        DataBook(
            uuid = UUID.randomUUID(),
            title = title,
            author = author,
            description = description,
            rating = ratingStr.toIntOrNull(),
            photo = photo,
            language = language,
            isbn = isbn,
            genres = genres,
            userVM.getUser().userUUID,
            archived = archived,
            exchange = exchange)
    Log.d("AddToBookScreen", "Adding book: $book")
    booksRepository.addBook(
        book,
        callback = {
          if (it.isSuccess) {
            // add the book to the list of books of the user
            userVM.updateUser(bookList = userVM.getUser().bookList.plus(book.uuid))
            Toast.makeText(context, context.getString(R.string.add_book_toast), Toast.LENGTH_LONG)
                .show()
          } else {
            Toast.makeText(
                    context, context.getString(R.string.add_book_toast_error), Toast.LENGTH_LONG)
                .show()
          }
        })
  }
}
