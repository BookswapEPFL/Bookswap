package com.android.bookswap.data

import java.util.UUID

/**
 * Represents a book with various properties.
 *
 * @param uuid Internal UUID for the book.
 * @param title Title of the book.
 * @param author Author of the book.
 * @param description Short description of the book.
 * @param rating Rating of the book out of 5 (if applicable).
 * @param photo Photo of the book.
 * @param language Language of the book.
 * @param isbn ISBN of the book (International Standard Book Number).
 * @param genres List of genres the book belongs to.
 * @param userId UUID of the user who owns the book.
 * @param archived Indicates if the book is archived.
 * @param exchange Indicates if the book is available for exchange.
 */
data class DataBook(
    val uuid: UUID,
    val title: String,
    val author: String?,
    val description: String?,
    val rating: Int?,
    val photo: String?,
    val language: BookLanguages,
    val isbn: String?,
    val genres: List<BookGenres> = emptyList(),
    var userId: UUID,
    var archived: Boolean = false,
    var exchange: Boolean = false
)

/** All supported book language type */
enum class BookLanguages(val languageCode: String) {
  FRENCH("French"), // French language
  GERMAN("German"), // German language
  ENGLISH("English"), // English language
  SPANISH("Spanish"), // Spanish language
  ITALIAN("Italian"), // Italian language
  ROMANSH("Romansh"), // Romansh, a language spoken in Switzerland
  OTHER("Other") // All languages that are not yet implemented
}
/** Genre of a book */
enum class BookGenres(val Genre: String = "Other") {
  FICTION("Fiction"),
  NONFICTION("Non-Fiction"),
  FANTASY("Fantasy"),
  SCIENCEFICTION("Science-Fiction"),
  MYSTERY("Mystery"),
  THRILLER("Thriller"),
  ROMANCE("Romance"),
  HORROR("Horror"),
  HISTORICAL("Historical"),
  WESTERN("Western"),
  DYSTOPIAN("Dystopian"),
  MEMOIR("Memoir"),
  BIOGRAPHY("Biography"),
  AUTOBIOGRAPHY("Autobiography"),
  SELFHELP("Self-Help"),
  HEALTH("Health"),
  TRAVEL("Travel"),
  GUIDE("Guide"),
  OTHER("Other") // Allows custom genre name
}
