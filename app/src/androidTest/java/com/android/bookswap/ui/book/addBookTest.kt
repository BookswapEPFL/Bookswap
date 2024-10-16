package com.android.bookswap.ui.book

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.ui.addBook.AddToBook
import com.android.bookswap.ui.addBook.createDataBook
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.UUID
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test

class AddToBookTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testSaveButtonDisabledInitially() {
    composeTestRule.setContent { AddToBook(MockBooksRepository()) }
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent { AddToBook(MockBooksRepository()) }
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithText("Title").performTextInput("My Book Title")
    composeTestRule.onNodeWithText("ISBN").performTextInput("1234567890")
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithText("Save").assertIsEnabled()
  }

  @Test
  fun displayAllComponents() {
    // Set the content for the Compose UI to be tested
    composeTestRule.setContent { AddToBook(BooksFirestoreRepository(Firebase.firestore)) }
    // Assert that all components are displayed and their contents are correct
    Thread.sleep(2000)
    composeTestRule
        .onNodeWithTag("addBookScreen")
        .assertIsDisplayed() // Ensure to add a test tag to your screen

    composeTestRule.onNodeWithTag("addBookTitle").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("addBookTitle")
        .assertTextEquals("Add Your Book") // Update this based on your screen title
    composeTestRule.onNodeWithTag("bookSave").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bookSave").assertTextEquals("Save")

    // Check that all input fields are displayed
    composeTestRule.onNodeWithTag("inputBookTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputBookAuthor").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputBookDescription").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputBookRating").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputBookISBN").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputBookPhoto").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputBookLanguage").assertIsDisplayed()
  }

  @Test
  fun testCreateDataBook_ValidData() {
    // Test with valid data
    val book =
        createDataBook(
            uuid = UUID.randomUUID(),
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "ENGLISH",
            isbn = "1234567890")

    // Assert the book is created correctly
    assertEquals("My Book", book?.title)
    assertEquals("Author Name", book?.author)
    assertEquals("This is a description", book?.description)
    assertEquals(4, book?.rating)
    assertEquals("https://example.com/photo.jpg", book?.photo)
    assertEquals(BookLanguages.ENGLISH, book?.language)
    assertEquals("1234567890", book?.isbn)
  }

  @Test
  fun testCreateDataBook_InvalidData() {
    // Test with invalid data (empty title)
    var book =
        createDataBook(
            uuid = UUID.randomUUID(),
            title = "",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "ENGLISH",
            isbn = "1234567890")

    // Assert that the book is null due to invalid title
    assertNull(book)

    // Test with invalid rating
    book =
        createDataBook(
            uuid = UUID.randomUUID(),
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "invalid_rating",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "ENGLISH",
            isbn = "1234567890")

    // Assert that the book is null due to invalid rating
    assertNull(book)

    // Test with invalid language
    book =
        createDataBook(
            uuid = UUID.randomUUID(),
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "INVALID_LANGUAGE",
            isbn = "1234567890")

    // Assert that the book is null due to invalid language
    assertNull(book)
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    composeTestRule.setContent { AddToBook(MockBooksRepository()) }
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithText("ISBN").performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
  }

  class MockBooksRepository : BooksRepository {
    private var isBookAdded = false
    private var isBookFetched = false
    private var isBookUpdated = false
    private var isBookDeleted = false
    private var shouldFail = false

    override fun init(OnSucess: () -> Unit) {
      if (!shouldFail) {
        OnSucess()
      }
    }

    override fun getNewUid(): UUID {
      return UUID.randomUUID()
    }

    override fun getBook(OnSucess: (List<DataBook>) -> Unit, onFailure: (Exception) -> Unit) {
      if (!shouldFail) {
        isBookFetched = true
        OnSucess(emptyList()) // Simulate an empty list of books
      } else {
        onFailure(Exception("Failed to fetch books"))
      }
    }

    override fun addBook(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit) {
      if (!shouldFail) {
        isBookAdded = true
        OnSucess()
      } else {
        onFailure(Exception("Failed to add book"))
      }
    }

    override fun updateBook(
        dataBook: DataBook,
        OnSucess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (!shouldFail) {
        isBookUpdated = true
        OnSucess()
      } else {
        onFailure(Exception("Failed to update book"))
      }
    }

    override fun deleteBooks(
        id: String,
        dataBook: DataBook,
        OnSucess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (!shouldFail) {
        isBookDeleted = true
        OnSucess()
      } else {
        onFailure(Exception("Failed to delete book"))
      }
    }
  }
}
