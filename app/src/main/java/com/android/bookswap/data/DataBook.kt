package com.android.bookswap.data

import com.android.bookswap.resources.Enums
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
    var archived: Boolean,
    var exchange: Boolean
)

/** All supported book language type */
enum class BookLanguages(val languageName: String, val languageCode: String) {
  FRENCH(Enums.Languages.FRENCH, Enums.LanguagesCode.FRENCH), // French language
  GERMAN(Enums.Languages.GERMAN, Enums.LanguagesCode.GERMAN), // German language
  ENGLISH(Enums.Languages.ENGLISH, Enums.LanguagesCode.ENGLISH), // English language
  SPANISH(Enums.Languages.SPANISH, Enums.LanguagesCode.SPANISH), // Spanish language
  ITALIAN(Enums.Languages.ITALIAN, Enums.LanguagesCode.ITALIAN), // Italian language
  ROMANSH(
      Enums.Languages.ROMANSH,
      Enums.LanguagesCode.ROMANSH), // Romansh, a language spoken in Switzerland
  OTHER(
      Enums.Languages.OTHER,
      Enums.LanguagesCode.OTHER); // All languages that are not yet implemented

  /**
   * Test whether the BookLanguages value is defined by the string given in parameter.
   *
   * @param string The string to test against, can be either a 2 letter code or a full language name
   * @return True if the BookLanguages value is defined by the string, False otherwise
   */
  fun isDefinedBy(string: String): Boolean {
    fun f(s: String) = s.trim().uppercase()
    val identifier = f(string)
    return when (identifier.length) {
      0,
      1 -> false
      2 -> f(languageCode).contentEquals(identifier)
      else -> f(languageName).contentEquals(identifier)
    }
  }

  companion object {
    /**
     * Retrieves the Enum value corresponding to the given identifier.
     *
     * @param string The string used as identifier, can be either a 2 letter code or a full language
     *   name
     * @return The corresponding BookLanguages value or BookLanguages.OTHER if no value matches
     */
    fun get(language: String): BookLanguages {
      return values().find { it.isDefinedBy(language) } ?: OTHER
    }
  }
}
/** Genre of a book */
enum class BookGenres(val Genre: String) {
  FICTION(Enums.Genres.FICTION),
  NONFICTION(Enums.Genres.NONFICTION),
  FANTASY(Enums.Genres.FANTASY),
  SCIENCEFICTION(Enums.Genres.SCIENCEFICTION),
  MYSTERY(Enums.Genres.MYSTERY),
  THRILLER(Enums.Genres.THRILLER),
  ROMANCE(Enums.Genres.ROMANCE),
  HORROR(Enums.Genres.HORROR),
  HISTORICAL(Enums.Genres.HISTORICAL),
  WESTERN(Enums.Genres.WESTERN),
  DYSTOPIAN(Enums.Genres.DYSTOPIAN),
  MEMOIR(Enums.Genres.MEMOIR),
  BIOGRAPHY(Enums.Genres.BIOGRAPHY),
  AUTOBIOGRAPHY(Enums.Genres.AUTOBIOGRAPHY),
  SELFHELP(Enums.Genres.SELFHELP),
  HEALTH(Enums.Genres.HEALTH),
  TRAVEL(Enums.Genres.TRAVEL),
  GUIDE(Enums.Genres.GUIDE),
  OTHER(Enums.Genres.OTHER);

  /**
   * Test whether the BookGenres value is defined by the string given in parameter.
   *
   * @param string The string to test against.
   * @return True if the BookGenres value is defined by the string, False otherwise
   */
  fun isDefinedBy(string: String): Boolean {
    fun f(s: String) = s.trim().uppercase()
    val identifier = f(string)
    return when (identifier.length) {
      0,
      1 -> false
      else -> f(Genre).contentEquals(identifier)
    }
  }

  companion object {
    /**
     * Retrieves the Enum value corresponding to the given identifier.
     *
     * @param string The string used as identifier.
     * @return The corresponding BookGenres value or BookGenres.OTHER if no value matches
     */
    fun get(genre: String): BookGenres {
      return values().find { it.isDefinedBy(genre) } ?: OTHER
    }
  }
}
