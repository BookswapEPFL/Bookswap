package com.android.bookswap.data

import java.util.UUID

/**
 * Represent a book with various properties
 *
 * @param uuid Internal id for the book
 * @param title Title of the book
 * @param author Author of the book
 * @param description Short description of the book
 * @param rating Rating of the book out of 5 (if applicable)
 * @param photo Photo of a book
 * @param language Language of the book
 * @param isbn ISBN of the book (International Standard Book Number)
 */
data class DataBook(
    val uuid: UUID,
    val title: String,
    val author: String?,
    val description: String?,
    val rating: Int?,
    val photo: String?,
    val language: BookLanguages,
    val isbn: String?
)

/** All supported book language type */
enum class BookLanguages(val languageCode: String) {
  FRENCH("FR"), // French language
  GERMAN("DE"), // German language
  ENGLISH("EN"), // English language
  SPANISH("ES"), // Spanish language
  ITALIAN("IT"), // Italian language
  ROMANSH("RM"), // Romansh, a language spoken in Switzerland
  OTHER("OTHER") // All languages that are not yet implemented
}

/** List of all supported book languages */
val LIST_BOOK_LANGUAGES =
  listOf(
      BookLanguages.FRENCH,
      BookLanguages.GERMAN,
      BookLanguages.ENGLISH,
      BookLanguages.SPANISH,
      BookLanguages.ITALIAN,
      BookLanguages.ROMANSH,
      BookLanguages.OTHER
  )
/** Genre of a book */
enum class Genre {
    Fiction,
    NonFiction,
    Fantasy,
    ScienceFiction,
    Mystery,
    Thriller,
    Romance,
    Horror,
    Historical,
    Western,
    Dystopian,
    Memoir,
    Biography,
    Autobiography,
    SelfHelp,
    Health,
    Travel,
    Guide
}

/** List of all supported book genres */
val LIST_BOOK_GENRES =
    listOf(
        Genre.Fiction,
        Genre.NonFiction,
        Genre.Fantasy,
        Genre.ScienceFiction,
        Genre.Mystery,
        Genre.Thriller,
        Genre.Romance,
        Genre.Horror,
        Genre.Historical,
        Genre.Western,
        Genre.Dystopian,
        Genre.Memoir,
        Genre.Biography,
        Genre.Autobiography,
        Genre.SelfHelp,
        Genre.Health,
        Genre.Travel,
        Genre.Guide,
    )