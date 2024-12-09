package com.android.bookswap.ui.books

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.coEvery
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockNavController: NavigationActions
  private lateinit var mockBookRepo: BooksRepository
  private val testBookId = UUID.randomUUID()
  private val modTestBookId = UUID.randomUUID()
  private val currentUserId = UUID.randomUUID()
  private lateinit var modTestBook: DataBook

  private val testBook =
      DataBook(
          testBookId,
          "Historia de España",
          "Jose Ignacio Pastor Iglesias",
          "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
          9,
          null,
          BookLanguages.SPANISH,
          "978-84-09025-23-5",
          listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
          currentUserId,
          true,
          true)

  @Before
  fun setUp() {
    mockNavController = mockk()
    mockBookRepo = mockk()

    modTestBook = testBook.copy(uuid = modTestBookId, photo = "new_photo_url")

    // Mocking the getBook call to return the test book
    coEvery { mockBookRepo.getBook(any(), any(), any()) } answers
        {
          val bookId = it.invocation.args[0] as UUID
          val onSuccess = it.invocation.args[1] as (DataBook) -> Unit
          if (bookId == testBookId) {
            onSuccess(testBook)
          } else if (bookId == modTestBookId) {
            onSuccess(modTestBook)
          }
        }
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBookId, mockBookRepo, navigationActions)
    }

    composeTestRule.onNodeWithTag(C.Tag.BookProfile.imagePlaceholder).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.title).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.author).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.location))
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.language).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.genres).assertIsDisplayed()
    testBook.genres.forEach { genre ->
      composeTestRule.onNodeWithTag(genre.Genre + C.Tag.BookProfile.genre).assertIsDisplayed()
    }
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.isbn).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.date).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.volume).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.issue).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.editorial).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.location).assertIsDisplayed()
  }

  @Test
  fun correctImageIsShownWhenBookHasPhoto() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(modTestBookId, mockBookRepo, navigationActions)
    }

    composeTestRule.onNodeWithTag(C.Tag.BookProfile.image).assertIsDisplayed()
  }
}
