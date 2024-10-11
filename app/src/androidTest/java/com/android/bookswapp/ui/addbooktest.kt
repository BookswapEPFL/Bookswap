package com.android.bookswapp.ui

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
import com.android.bookswap.model.Booksrepository
import com.android.bookswap.ui.Addbook.AddToBook
import com.android.bookswap.ui.Addbook.createDataBook
import com.android.bookswap.ui.Addbook.listToBooksView
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test

class AddToBookTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testSaveButtonDisabledInitially() {
    composeTestRule.setContent {
      AddToBook(listToBooksView = MockListToBooksView(MockBooksRepository()))
    }
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent {
      AddToBook(listToBooksView = MockListToBooksView(MockBooksRepository()))
    }
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithText("Title").performTextInput("My Book Title")
    composeTestRule.onNodeWithText("ISBN").performTextInput("1234567890")
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithText("Save").assertIsEnabled()
  }

  @Test
  fun displayAllComponents() {
    // Set the content for the Compose UI to be tested
    composeTestRule.setContent {
      AddToBook(listToBooksView = MockListToBooksView(MockBooksRepository()))
    }
    // Assert that all components are displayed and their contents are correct
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
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            Book_languageStr = "ENGLISH",
            Isbn = "1234567890")

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
            title = "",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            Book_languageStr = "ENGLISH",
            Isbn = "1234567890")

    // Assert that the book is null due to invalid title
    assertNull(book)

    // Test with invalid rating
    book =
        createDataBook(
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "invalid_rating",
            photo = "https://example.com/photo.jpg",
            Book_languageStr = "ENGLISH",
            Isbn = "1234567890")

    // Assert that the book is null due to invalid rating
    assertNull(book)

    // Test with invalid language
    book =
        createDataBook(
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            Book_languageStr = "INVALID_LANGUAGE",
            Isbn = "1234567890")

    // Assert that the book is null due to invalid language
    assertNull(book)
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    composeTestRule.setContent {
      AddToBook(listToBooksView = MockListToBooksView(MockBooksRepository()))
    }
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithText("ISBN").performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonDisabledWhenLanguageIsInvalid() {
    composeTestRule.setContent {
      AddToBook(listToBooksView = MockListToBooksView(MockBooksRepository()))
    }

    // Fill in all the required fields except language with invalid data
    composeTestRule.onNodeWithText("Title").performTextInput("My Book Title")
    composeTestRule.onNodeWithText("Author").performTextInput("Author Name")
    composeTestRule.onNodeWithText("Description").performTextInput("This is a description")
    composeTestRule.onNodeWithText("Rating").performTextInput("4")
    composeTestRule.onNodeWithText("ISBN").performTextInput("1234567890")
    composeTestRule.onNodeWithText("Photo").performTextInput("https://example.com/photo.jpg")
    composeTestRule.onNodeWithText("Language").performTextInput("INVALID_LANGUAGE")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
  }
}

// Simulating the listToBooksView interface implementation
class MockListToBooksView(repository: Booksrepository) : listToBooksView(repository) {
  var isBookAdded = false
  var addedBook: DataBook? = null
  var isBookDeleted = false
  var isBookUpdated = false
  var fetchedBooks: List<DataBook> = emptyList()

  override fun Add_Book(book: DataBook) {
    isBookAdded = true // Simulate adding the book
    addedBook = book
  }

  fun Update_Book(book: DataBook) {
    isBookUpdated = true // Simulate updating the book
    addedBook = book
  }

  fun Delete_Book(book: DataBook) {
    isBookDeleted = true // Simulate deleting the book
    addedBook = book
  }

  fun Fetch_Books(books: List<DataBook>) {
    fetchedBooks = books // Simulate fetching books
  }
}

class MockBooksRepository : Booksrepository {
  var isBookAdded = false
  var isBookFetched = false
  var isBookUpdated = false
  var isBookDeleted = false
  var shouldFail = false

  override fun init(OnSucess: () -> Unit) {
    if (!shouldFail) {
      OnSucess()
    }
  }

  override fun getNewUid(): String {
    return "mock_uid"
  }

  override fun getbook(OnSucess: (List<DataBook>) -> Unit, onFailure: (Exception) -> Unit) {
    if (!shouldFail) {
      isBookFetched = true
      OnSucess(emptyList()) // Simulate an empty list of books
    } else {
      onFailure(Exception("Failed to fetch books"))
    }
  }

  override fun addBooks(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit) {
    if (!shouldFail) {
      isBookAdded = true
      OnSucess()
    } else {
      onFailure(Exception("Failed to add book"))
    }
  }

  override fun updatebook(
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

  override fun deletebooks(
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
