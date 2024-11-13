package com.android.bookswap.ui.books.edit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditBookScreenTest {

  @MockK private lateinit var booksRepository: BooksFirestoreRepository

  @MockK private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  private val sampleBook =
      DataBook(
          uuid = UUID.randomUUID(),
          title = "Sample Book",
          author = "Sample Author",
          description = "Sample Description",
          rating = 4,
          photo = null,
          language = BookLanguages.ENGLISH,
          isbn = "123456789",
          genres = listOf(BookGenres.FANTASY))

  @Before
  fun setUp() {
    MockKAnnotations.init(this)

    every { navigationActions.currentRoute() } returns "EDIT_BOOK"
  }

  @Test
  fun displayEditScreenComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

    composeTestRule.onNodeWithTag("editBookScreen").assertIsDisplayed()
  }

  @Test
  fun displayEditTitleComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

    composeTestRule.onNodeWithTag("editBookTitle").assertIsDisplayed()
  }

  @Test
  fun displayEditTitleValueComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

    composeTestRule.onNodeWithTag("editBookTitle").assertTextEquals("Edit your Book")
  }

  @Test
  fun displayEditButtonComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
  }

  @Test
  fun displayEditSaveValueComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }
    composeTestRule
        .onNodeWithTag("editBookScreenColumn")
        .performScrollToNode(hasTestTag("bookSave"))
    composeTestRule.onNodeWithTag("bookSave").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bookSave").assertTextEquals("Save")
  }

  @Test
  fun displayEditDeleteValueComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }
    composeTestRule
        .onNodeWithTag("editBookScreenColumn")
        .performScrollToNode(hasTestTag("bookDelete"))
    composeTestRule.onNodeWithTag("bookDelete").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bookDelete").assertTextEquals("Delete")
  }

  @Test
  fun displayEditBookTitleComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

    composeTestRule.onNodeWithTag("inputBookTitle").assertIsDisplayed()
  }

  @Test
  fun displayEditBookAuthorComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }
    composeTestRule.onNodeWithTag("inputBookAuthor").assertIsDisplayed()
  }

  @Test
  fun displayEditBookDescriptionComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }
    composeTestRule.onNodeWithTag("inputBookDescription").assertIsDisplayed()
  }

  @Test
  fun displayEditBookRatingComponent() {
    composeTestRule.setContent {
      EditBookScreen(booksRepository, navigationActions, sampleBook.copy(photo = null))
    }
    composeTestRule.onNodeWithTag("inputBookRating").assertIsDisplayed()
  }

  @Test
  fun displayEditBookPhotoComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }
    composeTestRule.onNodeWithTag("inputBookPhoto").assertIsDisplayed()
  }

  @Test
  fun displayEditBookLanguageComponent() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }
    composeTestRule.onNodeWithTag("inputBookLanguage").assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

    composeTestRule.onNodeWithTag("inputBookTitle").assertTextContains(sampleBook.title)
    composeTestRule.onNodeWithTag("inputBookAuthor").assertTextContains(sampleBook.author ?: "")
    composeTestRule
        .onNodeWithTag("inputBookDescription")
        .assertTextContains(sampleBook.description ?: "")
    composeTestRule
        .onNodeWithTag("inputBookLanguage")
        .assertTextContains(sampleBook.language.toString())
  }

  @Test
  fun genreDropdownWorks() {
    composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

    // opens genre dropdown and select a genre
    composeTestRule.onNodeWithTag("GenreDropdown").performClick()
    composeTestRule.onNodeWithTag("GenreDropdownItem_Fantasy").performClick()

    // verify the selected genre
    composeTestRule.onNodeWithTag("SelectedGenre").assertTextContains("Fantasy")
  }
}
