package com.android.bookswap.ui.books.add

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.resources.C
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
      AddToBookScreen(mockBooksRepository, userId = userId)
    }
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      val userId = UUID.randomUUID()
      AddToBookScreen(mockBooksRepository, userId = userId)
    }
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.title).performTextInput("My Book Title")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.isbn).performTextInput("1234567890")
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).performClick()
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsEnabled()
  }

  @Test
  fun testCreateDataBook_ValidData() {
    // Test with valid data
    val book =
        createDataBook(
            mockContext,
            UUID.randomUUID(),
            "My Book",
            "Author Name",
            "This is a description",
            "4",
            "https://example.com/photo.jpg",
            "ENGLISH",
            "1234567890",
            listOf(BookGenres.TRAVEL),
            UUID.randomUUID(),
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
            mockContext,
            UUID.randomUUID(),
            "",
            "Author Name",
            "This is a description",
            "4",
            "https://example.com/photo.jpg",
            "ENGLISH",
            "1234567890",
            listOf(BookGenres.TRAVEL),
            UUID.randomUUID(),
        )

    // Assert that the book is null due to invalid title
    assertNull(book)

    // Test with invalid rating
    book =
        createDataBook(
            mockContext,
            UUID.randomUUID(),
            "My Book",
            "Author Name",
            "This is a description",
            "invalid_rating",
            "https://example.com/photo.jpg",
            "ENGLISH",
            "1234567890",
            listOf(BookGenres.TRAVEL),
            UUID.randomUUID(),
        )

    // Assert that the book is null due to invalid rating
    assertNull(book)

    // Test with invalid language
    book =
        createDataBook(
            mockContext,
            UUID.randomUUID(),
            "My Book",
            "Author Name",
            "This is a description",
            "4",
            "https://example.com/photo.jpg",
            "INVALID_LANGUAGE",
            "1234567890",
            listOf(BookGenres.TRAVEL),
            UUID.randomUUID(),
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
      AddToBookScreen(mockBooksRepository, userId = userId)
    }
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.isbn).performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsNotEnabled()
  }

  @Test
  fun testDropdownMenuIsInitiallyClosed() {
    val userId = UUID.randomUUID()
    composeTestRule.setContent { AddToBookScreen(mockBooksRepository, userId = userId) }

    // Verify that the dropdown menu is initially not expanded
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).assertIsDisplayed()
    composeTestRule.onNodeWithText("Language*").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuOpensOnClick() {
    val userId = UUID.randomUUID()
    composeTestRule.setContent { AddToBookScreen(mockBooksRepository, userId = userId) }

    // Simulate clicking the dropdown to expand it
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

    // Verify that dropdown items are displayed:
    composeTestRule.onNodeWithText("French").assertIsDisplayed()
    composeTestRule.onNodeWithText("German").assertIsDisplayed()
    composeTestRule.onNodeWithText("English").assertIsDisplayed()
    composeTestRule.onNodeWithText("Spanish").assertIsDisplayed()
    composeTestRule.onNodeWithText("Italian").assertIsDisplayed()
    composeTestRule.onNodeWithText("Romansh").assertIsDisplayed()
    composeTestRule.onNodeWithText("Other").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuItemSelection() {
    val userId = UUID.randomUUID()

    composeTestRule.setContent { AddToBookScreen(mockBooksRepository, userId = userId) }

    // Expand the dropdown menu:
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

    // Click on a specific language ("English")
    composeTestRule.onNodeWithText("English").performClick()

    // Verify the language field updates with the selected language
    composeTestRule.onNodeWithText("English").assertExists()
  }

  @Test
  fun testDropdownMenuClosesAfterSelection() {
    val userId = UUID.randomUUID()

    composeTestRule.setContent { AddToBookScreen(mockBooksRepository, userId = userId) }

    // Expand the dropdown menu
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

    // Select a language to close the dropdown
    composeTestRule.onNodeWithText("English").performClick()

    // Ensure the dropdown items are no longer displayed (here we juste looks that Italian is not
    // visible)
    composeTestRule.onNodeWithText("Italian").assertDoesNotExist()
  }
}
