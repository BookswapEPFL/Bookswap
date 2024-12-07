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
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditBookScreenTest {

  private val booksRepository: BooksRepository = mockk()

  private val navigationActions: NavigationActions = mockk()

  @get:Rule val composeTestRule = createComposeRule()

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
          false,
          false)

  @Before
  fun setUp() {
    every { navigationActions.currentRoute() } returns "EDIT_BOOK"
    every { booksRepository.getBook(any(), any(), any()) } answers
        {
          secondArg<(DataBook) -> Unit>()(sampleBook)
        }
  }

  @Test
  fun displayEditScreenComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }

    composeTestRule.onNodeWithTag(C.Tag.edit_book_screen_container).assertIsDisplayed()
  }

  @Test
  fun displayEditTitleComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertIsDisplayed()
  }

  @Test
  fun displayEditTitleValueComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertTextEquals("Edit your Book")
  }

  @Test
  fun displayEditButtonComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.back_button).assertIsDisplayed()
  }

  @Test
  fun displayEditSaveValueComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule
        .onNodeWithTag(C.Tag.EditBook.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.EditBook.save))
    composeTestRule.onNodeWithTag(C.Tag.EditBook.save).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.EditBook.save).assertTextEquals("Save")
  }

  @Test
  fun displayEditDeleteValueComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule
        .onNodeWithTag(C.Tag.EditBook.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.EditBook.delete))
    composeTestRule.onNodeWithTag(C.Tag.EditBook.delete).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.EditBook.delete).assertTextEquals("Delete")
  }

  @Test
  fun displayEditBookTitleComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }

    composeTestRule.onNodeWithTag(C.Tag.EditBook.title).assertIsDisplayed()
  }

  @Test
  fun displayEditBookAuthorComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule.onNodeWithTag(C.Tag.EditBook.author).assertIsDisplayed()
  }

  @Test
  fun displayEditBookDescriptionComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule.onNodeWithTag(C.Tag.EditBook.synopsis).assertIsDisplayed()
  }

  @Test
  fun displayEditBookRatingComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule.onNodeWithTag(C.Tag.EditBook.rating).assertIsDisplayed()
  }

  @Test
  fun displayEditBookPhotoComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule.onNodeWithTag(C.Tag.EditBook.image).assertIsDisplayed()
  }

  @Test
  fun displayEditBookLanguageComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }
    composeTestRule.onNodeWithTag(C.Tag.EditBook.language).assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.uuid)
    }

    composeTestRule.onNodeWithTag(C.Tag.EditBook.title).assertTextContains(sampleBook.title)
    composeTestRule.onNodeWithTag(C.Tag.EditBook.author).assertTextContains(sampleBook.author ?: "")
    composeTestRule
        .onNodeWithTag(C.Tag.EditBook.synopsis)
        .assertTextContains(sampleBook.description ?: "")
    composeTestRule.onNodeWithTag(C.Tag.EditBook.image).assertTextContains(sampleBook.photo ?: "")
    composeTestRule
        .onNodeWithTag(C.Tag.EditBook.language)
        .assertTextContains(sampleBook.language.toString())
  }
}
