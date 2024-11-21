package com.android.bookswap.ui.books.edit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditBookScreenTest {

  private val mockViewModel: EditBookViewModel = mockk()

  @MockK private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  private val sampleBook =
      DataBook(
          uuid = UUID.randomUUID(),
          title = "Sample Book",
          author = "Sample Author",
          description = "Sample Description",
          rating = 4,
          photo = "sample_photo_url",
          language = BookLanguages.ENGLISH,
          isbn = "123456789",
          genres = listOf(BookGenres.FANTASY))

  @Before
  fun setUp() {
    MockKAnnotations.init(this)
    every { mockViewModel.deleteBooks(any(), any()) } just runs
    every {
      mockViewModel.updateDataBook(
          any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs

    every { navigationActions.currentRoute() } returns "EDIT_BOOK"
    composeTestRule.setContent { EditBookScreen(mockViewModel, sampleBook) }
  }

  @Test
  fun displayAllComponent() {
    composeTestRule.onNodeWithTag("edit_book_screen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("title_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("author_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("rating_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("photo_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("language_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("genre_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("isbn_field").assertIsDisplayed()

    composeTestRule.onNodeWithTag("save_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("save_button").assertTextEquals("Save")

    composeTestRule.onNodeWithTag("delete_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("delete_button").assertTextEquals("Delete")
  }

  @Test
  fun inputsHaveInitialValue() {
    composeTestRule.onNodeWithTag("title_field").assertTextContains(sampleBook.title)
    composeTestRule.onNodeWithTag("author_field").assertTextContains(sampleBook.author ?: "")
    composeTestRule
        .onNodeWithTag("description_field")
        .assertTextContains(sampleBook.description ?: "")
    composeTestRule.onNodeWithTag("photo_field").assertTextContains(sampleBook.photo ?: "")
    composeTestRule
        .onNodeWithTag("language_field")
        .assertTextContains(sampleBook.language.toString())
  }
}
