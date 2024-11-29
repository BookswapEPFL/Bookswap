package com.android.bookswap.model

class InputVerification {

  fun testIsbn(isbn: String): Boolean {
    // Remove hyphens or spaces to normalize input
    val isbnStr = isbn.replace("-", "").replace(" ", "")

    // Check length for ISBN-10 or ISBN-13
    if (isbnStr.length == 10) {
      return isValidISBN10(isbnStr)
    } else if (isbnStr.length == 13) {
      return isValidISBN13(isbnStr)
    }
    // Invalid length
    return false
  }

  internal fun isValidISBN10(isbn: String): Boolean {
    // Check for correct format (9 digits and a digit or 'X' as the last character)
    if (!isbn.matches(Regex("^\\d{9}[\\dXx]$"))) {
      return false
    }

    // Calculate checksum for ISBN-10
    val sum = isbn.dropLast(1).mapIndexed { index, char -> (index + 1) * char.digitToInt() }.sum()

    val checksum = sum % 11
    val checkDigit = if (checksum == 10) 'X' else checksum.toString()[0]

    // Compare calculated checksum to the last character
    return isbn.last().uppercaseChar() == checkDigit
  }

  internal fun isValidISBN13(isbn: String): Boolean {
    // Check for correct format (13 digits)
    if (!isbn.matches(Regex("^\\d{13}$"))) {
      return false
    }

    // Calculate checksum for ISBN-13
    val sum =
        isbn.mapIndexed { index, char -> char.digitToInt() * if (index % 2 == 0) 1 else 3 }.sum()

    // Check if the sum is divisible by 10
    return sum % 10 == 0
  }

  fun validateEmail(input: String): Boolean {
    // Updated regex for stricter email validation
    val emailRegex =
        Regex("^[a-zA-Z0-9!#\$%&'*+/=?^_`{|}~.-]+@[a-zA-Z0-9.-]+(?<!\\.)\\.[a-zA-Z]{2,}$")
    return emailRegex.matches(input)
  }

  fun validatePhone(input: String): Boolean {
    // Regex for validating phone numbers
    val phoneRegex =
        Regex("^\\+?(\\d{1,3})?[-.\\s]?\\(?\\d{2,4}\\)?[-.\\s]?\\d{2,4}[-.\\s]?\\d{4,9}\$")
    return phoneRegex.matches(input)
  }

  fun validateNonEmpty(input: String): Boolean {
    return input.isNotBlank()
  }
}
