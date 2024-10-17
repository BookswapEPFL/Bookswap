package com.android.bookswap.model.map

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookFilter {
    // Utilisation de MutableStateFlow pour les filtres de genres et de langues
    private val _genresFilter = MutableStateFlow<List<BookGenres>>(emptyList())
    private val _languagesFilter = MutableStateFlow<List<BookLanguages>>(emptyList())

    // StateFlow immuable pour exposer les filtres
    val genresFilter: StateFlow<List<BookGenres>> = _genresFilter.asStateFlow()
    val languagesFilter: StateFlow<List<BookLanguages>> = _languagesFilter.asStateFlow()

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

    private fun filterByGenres(books: List<DataBook>): List<DataBook> {
        return books.filter { book -> book.genres.any { it in _genresFilter.value } }
    }

    private fun filterByLanguages(books: List<DataBook>): List<DataBook> {
        return books.filter { book -> book.language in _languagesFilter.value }
    }

    fun setGenres(genres: List<String>) {
        val newGenresFilter = genres.mapNotNull { genre ->
        BookGenres.values().firstOrNull { it.Genre.equals(genre, ignoreCase = true) }
    }
        _genresFilter.value = newGenresFilter
    }

    fun setLanguages(languages: List<String>) {
        val newLanguagesFilter = languages.mapNotNull { language ->
            runCatching { BookLanguages.valueOf(language.uppercase()) }.getOrNull()
        }
        _languagesFilter.value = newLanguagesFilter
    }
}