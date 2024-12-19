package com.android.bookswap.ui.books.edit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditBookScreenTest {

  private val mockViewModel: EditBookViewModel = mockk()

  private val navigationActions: NavigationActions = mockk()

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var photoStorage: PhotoFirebaseStorageRepository

  private val sampleBook =
      DataBook(
          UUID.randomUUID(),
          "Sample Book",
          "Sample Author",
          "Sample Description",
          4,
          "sample_photo_url",
          BookLanguages.ENGLISH,
          "123456789",
          listOf(BookGenres.FANTASY),
          UUID.randomUUID(),
          archived = false,
          exchange = true)

  @Before
  fun setUp() {
    photoStorage = mockk()
    every { photoStorage.addPhotoToStorage(any(), any(), any()) } just runs
    MockKAnnotations.init(this)
    every { mockViewModel.deleteBook(any(), any()) } just runs
    every {
      mockViewModel.updateDataBook(
          any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs
    every { mockViewModel.getBook(any(), any(), any()) } answers
        {
          secondArg<(DataBook) -> Unit>()(sampleBook)
        }

    every { navigationActions.currentRoute() } returns "EDIT_BOOK"
    composeTestRule.setContent {
      EditBookScreen(mockViewModel, photoStorage, bookUUID = sampleBook.uuid)
    }
  }

  @Test
  fun displayEditScreenComponent() {
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.scrollable).assertIsDisplayed()
  }

  @Test
  fun displayEditSaveValueComponent() {
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.EditBook.save))
    composeTestRule.onNodeWithTag(C.Tag.EditBook.save).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.EditBook.save).assertTextEquals("Save")
  }

  @Test
  fun displayEditDeleteValueComponent() {
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.EditBook.delete))
    composeTestRule.onNodeWithTag(C.Tag.EditBook.delete).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.EditBook.delete).assertTextEquals("Delete")
  }

  @Test
  fun inputsHaveInitialValue() {
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.title_field)
        .assertTextContains(sampleBook.title)

    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.author_field)
        .assertTextContains(sampleBook.author ?: "")

    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.description_field)
        .assertTextContains(sampleBook.description ?: "")

    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.isbn_field)
        .assertTextContains(sampleBook.isbn ?: "")

    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.language_field)
        .assertTextContains(sampleBook.language.languageName)

    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.genre_field)
        .assertTextContains(sampleBook.genres.joinToString { it.Genre })
  }
}
