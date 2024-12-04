package com.android.bookswap.ui.books.add

import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

import com.android.bookswap.model.add.AddToBookViewModel

import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddToBookTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockViewModel: AddToBookViewModel = mockk()

  @Before
  fun init() {
    every {
      mockViewModel.saveDataBook(any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs
    mockkStatic(Toast::class)
    val toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock
  }

  @Test
  fun testSaveButtonDisabledInitially() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.title).performTextInput("My Book Title")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.isbn).performTextInput("978-3-16-148410-0")
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
      NavigationActions(navController)

      AddToBookScreen(mockViewModel)
    }
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.isbn).performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsNotEnabled()
  }

  @Test
  fun testDropdownMenuIsInitiallyClosed() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Verify that the dropdown menu is initially not expanded
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).assertIsDisplayed()
    composeTestRule.onNodeWithText("Language*").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuOpensOnClick() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

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
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Expand the dropdown menu:
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

    // Click on a specific language ("English")
    composeTestRule.onNodeWithText("English").performClick()

    // Verify the language field updates with the selected language
    composeTestRule.onNodeWithText("English").assertExists()
  }

  @Test
  fun testDropdownMenuClosesAfterSelection() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Expand the dropdown menu
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

    // Select a language to close the dropdown
    composeTestRule.onNodeWithText("English").performClick()

    // Ensure the dropdown items are no longer displayed (here we juste looks that Italian is not
    // visible)
    composeTestRule.onNodeWithText("Italian").assertDoesNotExist()
  }
}
