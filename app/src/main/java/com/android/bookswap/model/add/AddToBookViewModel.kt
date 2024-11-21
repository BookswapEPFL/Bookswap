package com.android.bookswap.model.add

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import java.util.UUID

class AddToBookViewModel(private val booksRepository: BooksRepository) : ViewModel() {

  /**
   * Creates a DataBook instance after validating the input parameters.
   *
   * @param context The context of the screen
   * @param title The title of the book.
   * @param author The author of the book.
   * @param description The description of the book.
   * @param ratingStr The rating of the book as a string.
   * @param photo The URL of the book's photo.
   * @param bookLanguageStr The language of the book as a string.
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
      bookLanguageStr: String,
      isbn: String,
      genres: List<BookGenres>
  ) {
    // Validate Language
    val languages: BookLanguages =
        try {
          BookLanguages.valueOf(bookLanguageStr.uppercase())
        } catch (e: IllegalArgumentException) {
          Log.e(
              "AddToBookScreen",
              "Invalid language: $bookLanguageStr. Please use one of the supported languages.")
          Toast.makeText(context, "Invalid language: $bookLanguageStr.", Toast.LENGTH_LONG).show()
          return
        }

    val book =
        DataBook(
            uuid = UUID.randomUUID(),
            title = title,
            author = author,
            description = description,
            rating = ratingStr.toIntOrNull(),
            photo = photo,
            language = languages,
            isbn = isbn,
            genres = genres)
    Log.d("AddToBookScreen", "Adding book: $book")
    booksRepository.addBook(
        book,
        callback = {
          if (it.isSuccess) {
            Toast.makeText(context, "Book added.", Toast.LENGTH_LONG).show()
          } else {
            Toast.makeText(context, "Failed to add book.", Toast.LENGTH_LONG).show()
          }
        })
  }
}
