package com.android.bookswap.ui.books.add

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.ui.navigation.NavigationActions
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
  private val mockBooksRepository: BooksRepository = mockk()

  @Before
  fun init() {
    mockkStatic(Toast::class)
    val toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock
  }

  @Test
  fun testSaveButtonDisabledInitially() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      val userId = UUID.randomUUID()
      AddToBookScreen(mockBooksRepository, navigationActions, userId)
    }
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithTag("save_button").assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      val userId = UUID.randomUUID()
      AddToBookScreen(MockBooksRepository(), navigationActions, userId)
    }
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithTag("title_field").performTextInput("My Book Title")
    composeTestRule.onNodeWithTag("isbn_field").performTextInput("1234567890")
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithTag("save_button").performClick()
    composeTestRule.onNodeWithTag("save_button").assertIsEnabled()
  }

  @Test
  fun testCreateDataBook_ValidData() {
    // Test with valid data
    val book =
        createDataBook(
            context = mockContext,
            uuid = UUID.randomUUID(),
            title = "My Book",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "ENGLISH",
            isbn = "1234567890",
            genres = listOf(BookGenres.TRAVEL),
            userId = UUID.randomUUID(),
        )

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
            context = mockContext,
            uuid = UUID.randomUUID(),
            title = "",
            author = "Author Name",
            description = "This is a description",
            ratingStr = "4",
            photo = "https://example.com/photo.jpg",
            bookLanguageStr = "ENGLISH",
            isbn = "1234567890",
            genres = listOf(BookGenres.TRAVEL),
            userId = UUID.randomUUID(),
        )

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
            genres = listOf(BookGenres.TRAVEL),
            userId = UUID.randomUUID(),
        )

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
            genres = listOf(BookGenres.TRAVEL),
            userId = UUID.randomUUID(),
        )

    // Assert that the book is null due to invalid language
    assertNull(book)
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      val userId = UUID.randomUUID()
      AddToBookScreen(mockBooksRepository, navigationActions, userId)
    }
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithTag("isbn_field").performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithTag("save_button").assertIsNotEnabled()
  }
}
