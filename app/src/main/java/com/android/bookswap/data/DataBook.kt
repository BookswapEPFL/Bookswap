package com.android.bookswap.data

/**
 * Represent a book with various properties
 *
 * @param title Title of the book
 * @param author Author of the book
 * @param description Short description of the book
 * @param rating Rating of the book out of 5 (if applicable)
 * @param photo Photo of a book
 * @param language Language of the book
 * @param isbn ISBN of the book (International Standard Book Number)
 */
data class DataBook(
    val title: String,
    val author: String,
    val description: String,
    val rating: Int?,
    val photo: String,
    val language: BookLanguages,
    val isbn: String
)

/** All supported book language type */
enum class BookLanguages {
  FRENCH, // French language
  GERMAN, // German language
  ENGLISH, // English language
  SPANISH, // Spanish language
  ITALIAN, // Italian language
  ROMANCH, // Romansh, a language spoken in Switzerland
  SWISS_GERMAN, // Swiss German dialect
  OTHER // All languages that are not yet implemented
}
