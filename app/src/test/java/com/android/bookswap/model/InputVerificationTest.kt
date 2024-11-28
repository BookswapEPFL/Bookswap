package com.android.bookswap.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InputVerificationTest {
  private lateinit var inputVerification: InputVerification

  @Before
  fun setup() {
    inputVerification = InputVerification()
  }

  @Test
  fun `testIsbn returns false for invalid lengths`() {
    assertFalse(inputVerification.testIsbn("123456789")) // 9 characters
    assertFalse(inputVerification.testIsbn("12345678901")) // 11 characters
    assertFalse(inputVerification.testIsbn("123")) // Too short
    assertFalse(inputVerification.testIsbn("12345678901234")) // Too long
  }

  @Test
  fun `testIsbn returns true for valid ISBN-10`() {
    assertTrue(inputVerification.testIsbn("0-306-40615-2")) // Valid ISBN-10
    assertTrue(inputVerification.testIsbn("0306406152")) // Valid ISBN-10
  }

  @Test
  fun `testIsbn returns true for valid ISBN-13`() {
    assertTrue(inputVerification.testIsbn("978-3-16-148410-0")) // Valid ISBN-13
    assertTrue(inputVerification.testIsbn("9783161484100")) // Valid ISBN-13
  }

  @Test
  fun `testIsbn returns false for invalid ISBN-10 checksum`() {
    assertFalse(inputVerification.testIsbn("0-306-40615-X")) // Invalid checksum
    assertFalse(inputVerification.testIsbn("1234567890")) // Invalid checksum
  }

  @Test
  fun `testIsbn returns false for invalid ISBN-13 checksum`() {
    assertFalse(inputVerification.testIsbn("978-3-16-148410-9")) // Invalid checksum
    assertFalse(inputVerification.testIsbn("1234567890123")) // Invalid checksum
  }

  @Test
  fun `testIsbn ignores hyphens and spaces`() {
    assertTrue(inputVerification.testIsbn("978 3 16 148410 0")) // Valid ISBN-13
    assertTrue(inputVerification.testIsbn("0 306 40615 2")) // Valid ISBN-10
    assertFalse(inputVerification.testIsbn("978 3 16 148410 9")) // Invalid ISBN-13
    assertFalse(inputVerification.testIsbn("0 306 40615 X")) // Invalid ISBN-10
  }

  @Test
  fun `isValidISBN10 returns true for valid ISBN-10`() {
    assertTrue(inputVerification.isValidISBN10("0306406152")) // Valid
    assertTrue(inputVerification.isValidISBN10("0-306-40615-2".replace("-", ""))) // Valid
  }

  @Test
  fun `isValidISBN10 returns false for invalid ISBN-10`() {
    assertFalse(inputVerification.isValidISBN10("1234567890")) // Invalid checksum
    assertFalse(inputVerification.isValidISBN10("123456788X")) // Invalid checksum
    assertFalse(inputVerification.isValidISBN10("12345")) // Too short
  }

  @Test
  fun `isValidISBN13 returns true for valid ISBN-13`() {
    assertTrue(inputVerification.isValidISBN13("9783161484100")) // Valid
    assertTrue(inputVerification.isValidISBN13("978-3-16-148410-0".replace("-", ""))) // Valid
  }

  @Test
  fun `isValidISBN13 returns false for invalid ISBN-13`() {
    assertFalse(inputVerification.isValidISBN13("9783161484109")) // Invalid checksum
    assertFalse(inputVerification.isValidISBN13("1234567890123")) // Invalid checksum
    assertFalse(inputVerification.isValidISBN13("12345")) // Too short
  }

  @Test
  fun `isValidISBN13 handles edge cases`() {
    assertFalse(inputVerification.isValidISBN13("abcdefghijklm")) // Invalid characters
    assertFalse(inputVerification.isValidISBN13("")) // Empty string
  }

  @Test
  fun `isValidISBN10 handles edge cases`() {
    assertFalse(inputVerification.isValidISBN10("abcdefghij")) // Invalid characters
    assertFalse(inputVerification.isValidISBN10("")) // Empty string
  }

  @Test
  fun `validateEmail returns true for valid emails`() {
    assertTrue(inputVerification.validateEmail("john.doe@example.com"))
    assertTrue(inputVerification.validateEmail("jane.doe+alias@domain.co"))
    assertTrue(inputVerification.validateEmail("user123@sub.domain.org"))
    assertTrue(inputVerification.validateEmail("valid_email@domain.com"))
    assertTrue(inputVerification.validateEmail("email.with-dash@domain.com"))
  }

  @Test
  fun `validateEmail returns false for invalid emails`() {
    assertFalse(inputVerification.validateEmail("plainaddress"))
    assertFalse(inputVerification.validateEmail("@missingusername.com"))
    assertFalse(inputVerification.validateEmail("username@.missingdomain"))
    assertFalse(inputVerification.validateEmail("username@domain.c")) // TLD too short
    assertFalse(inputVerification.validateEmail("username@domain,com")) // Comma instead of dot
    assertFalse(inputVerification.validateEmail("username@domain..com")) // Double dot
    assertFalse(inputVerification.validateEmail("username@domain.com.")) // Trailing dot
  }

  @Test
  fun `validatePhone returns true for valid phone numbers`() {
    assertTrue(inputVerification.validatePhone("+1234567890"))
    assertTrue(inputVerification.validatePhone("123-456-7890"))
    assertTrue(inputVerification.validatePhone("(123) 456-7890"))
    assertTrue(inputVerification.validatePhone("+1 (123) 456-7890"))
    assertTrue(inputVerification.validatePhone("123 456 7890"))
  }

  @Test
  fun `validatePhone returns false for invalid phone numbers`() {
    assertFalse(inputVerification.validatePhone("12345")) // Too short
    assertFalse(inputVerification.validatePhone("abcdefg")) // Invalid characters
    assertFalse(inputVerification.validatePhone("+123(456)")) // Incomplete
    assertFalse(inputVerification.validatePhone("+123 456 78901234567890")) // Too long
    assertFalse(inputVerification.validatePhone("123-456-abcde")) // Letters
  }

  @Test
  fun `validatePhone handles edge cases`() {
    assertFalse(inputVerification.validatePhone("")) // Empty
    assertFalse(inputVerification.validatePhone(" ")) // Spaces only
    assertFalse(inputVerification.validatePhone("+-")) // Only symbols
    assertFalse(inputVerification.validatePhone("(123))")) // Mismatched parentheses
    assertTrue(inputVerification.validatePhone("+1 234 567 8901")) // Valid
  }

  @Test
  fun `validateNonEmpty returns true for non-empty strings`() {
    assertTrue(inputVerification.validateNonEmpty("Non-empty"))
    assertTrue(inputVerification.validateNonEmpty("  Leading and trailing spaces  "))
    assertTrue(inputVerification.validateNonEmpty("a")) // Single character
  }

  @Test
  fun `validateNonEmpty returns false for empty or blank strings`() {
    assertFalse(inputVerification.validateNonEmpty(""))
    assertFalse(inputVerification.validateNonEmpty("     ")) // Only spaces
    assertFalse(inputVerification.validateNonEmpty("\t\n")) // Whitespace characters
  }
}
