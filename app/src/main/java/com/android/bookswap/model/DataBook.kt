package com.android.bookswap.model

// Data class representing a book with various properties
data class DataBook(
    val Title: String, // Title of the book
    val Author: String, // Author of the book
    val Description: String, // Short description of the book
    val Rating: Int, // Rating of the book (e.g., out of 5)
    val photo: String, // photo of the book
    val Language: Languages, // Language of the book, represented by the Languages enum
    val ISBN: String // ISBN of the book (International Standard Book Number)
)

// Enum class representing the available languages for a book
enum class Languages(val languageCode: String) {
  FRENCH("FR"), // French language
  GERMAN("DE"), // German language
  ENGLISH("EN"), // English language
  SPANISH("ES"), // Spanish language
  ITALIEN("IT"), // Italian language
  ROMANCH("RM"), // Romansh, a language spoken in Switzerland
  SWISS_GERMAN("E") // Swiss German dialect
}
