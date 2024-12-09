package com.android.bookswap.model.map

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** This class is responsible for filtering books based on the selected genres and languages */
class BookFilter {

  private val _genresFilter = MutableStateFlow<List<BookGenres>>(emptyList())
  private val _languagesFilter = MutableStateFlow<List<BookLanguages>>(emptyList())

  val genresFilter: StateFlow<List<BookGenres>> = _genresFilter.asStateFlow()
  val languagesFilter: StateFlow<List<BookLanguages>> = _languagesFilter.asStateFlow()

  /**
   * Filters the books based on the selected genres and languages
   *
   * @param books The list of books to be filtered
   * @return The filtered list of books
   */
  fun filterBooks(books: List<DataBook>): List<DataBook> {
    var filteredBooks = books
    if (_genresFilter.value.isNotEmpty()) {
      filteredBooks = filterByGenres(filteredBooks)
    }
    if (_languagesFilter.value.isNotEmpty()) {
      filteredBooks = filterByLanguages(filteredBooks)
    }
    return filteredBooks
  }

  // Filters the books based on the selected genres
  private fun filterByGenres(books: List<DataBook>): List<DataBook> {
    return books.filter { book -> book.genres.any { it in _genresFilter.value } }
  }
  // Filters the books based on the selected languages
  private fun filterByLanguages(books: List<DataBook>): List<DataBook> {
    return books.filter { book -> book.language in _languagesFilter.value }
  }

  /**
   * Sets the selected genres
   *
   * @param genres The list of genres to be set
   */
  fun setGenres(genres: List<String>) {
    val newGenresFilter =
        genres.mapNotNull { genre ->
          // Use the Genre parameter to get the corresponding BookGenres enum
          BookGenres.values().firstOrNull { it.Genre.equals(genre, ignoreCase = true) }
        }
    _genresFilter.value = newGenresFilter
  }

  /**
   * Sets the selected languages
   *
   * @param languages The list of languages to be set
   */
  fun setLanguages(languages: List<String>) {
    val newLanguagesFilter =
        languages.mapNotNull { language ->
          runCatching { BookLanguages.valueOf(language.uppercase()) }.getOrNull()
        }
    _languagesFilter.value = newLanguagesFilter
  }
}
