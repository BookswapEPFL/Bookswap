package com.android.bookswap.ui.books.add

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.util.UUID
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddToBookTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockContext: Context = mockk()

  @Before
  fun init() {
    mockkStatic(Toast::class)
    val toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock
  }

  @Test
  fun testSaveButtonDisabledInitially() {
    composeTestRule.setContent { AddToBookScreen(MockBooksRepository()) }
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent { AddToBookScreen(MockBooksRepository()) }
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithText("Title").performTextInput("My Book Title")
    composeTestRule.onNodeWithText("ISBN").performTextInput("1234567890")
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithText("Select Genre").performClick()
    composeTestRule.onNodeWithText("Save").assertIsEnabled()
  }


  @Test
  fun testCreateDataBook_ValidData() {
      // Test with valid data
      val book = createDataBook(
          context = mockContext,
          uuid = UUID.randomUUID(),
          title = "My Book",
          author = "Author Name",
          description = "This is a description",
          ratingStr = "4",
          photo = "",
          bookLanguageStr = "ENGLISH",
          isbn = "1234567890",
          genres = listOf(BookGenres.TRAVEL)
      )

      // Assert the book is created correctly
      assertEquals("My Book", book?.title)
      assertEquals("Author Name", book?.author)
      assertEquals("This is a description", book?.description)
      assertEquals(4, book?.rating)
      assertEquals(BookLanguages.ENGLISH, book?.language)
      assertEquals("1234567890", book?.isbn)
  }

  @Test
  fun testCreateDataBook_InvalidData() {
    // Test with invalid data (empty title)
    var book =
        createDataBook(
            context = mockContext,
            uuid = UUID.randomUUID(),
            title = "",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "ENGLISH",
            isbn = "1234567890",
            genres = listOf(BookGenres.TRAVEL))

    // Assert that the book is null due to invalid title
    assertNull(book)

    // Test with invalid rating
    book =
        createDataBook(
            context = mockContext,
            uuid = UUID.randomUUID(),
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "invalid_rating",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "ENGLISH",
            isbn = "1234567890",
            genres = listOf(BookGenres.TRAVEL))

    // Assert that the book is null due to invalid rating
    assertNull(book)

    // Test with invalid language
    book =
        createDataBook(
            context = mockContext,
            uuid = UUID.randomUUID(),
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "INVALID_LANGUAGE",
            isbn = "1234567890",
            genres = listOf(BookGenres.TRAVEL))

    // Assert that the book is null due to invalid language
    assertNull(book)
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    composeTestRule.setContent { AddToBookScreen(MockBooksRepository()) }
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

    override fun getNewUUID(): UUID {
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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (!shouldFail) {
        isBookUpdated = true
        onSuccess()
      } else {
        onFailure(Exception("Failed to update book"))
      }
    }

    override fun deleteBooks(
        uuid: UUID,
        dataBook: DataBook,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (!shouldFail) {
        isBookDeleted = true
        onSuccess()
      } else {
        onFailure(Exception("Failed to delete book"))
      }
    }
  }
}
