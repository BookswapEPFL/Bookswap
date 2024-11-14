package com.android.bookswap.data

import java.util.UUID

/**
 * Represent a book with various properties
 *
 * @param uuid Internal uuid for the book
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
    val isbn: String?,
    val genres: List<BookGenres> = emptyList()
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
