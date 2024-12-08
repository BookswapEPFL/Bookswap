package com.android.bookswap.ui.books

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
  private val currentUserId = UUID.randomUUID()

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

    // Mocking the getBook call to return the test book
    coEvery { mockBookRepo.getBook(testBookId, any(), any()) } answers
        {
          val onSuccess = it.invocation.args[1] as (DataBook) -> Unit
          onSuccess(testBook)
        }
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBookId, mockBookRepo, navigationActions)
    }

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
  fun iconsAreClickable() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBookId, mockBookRepo, navigationActions)
    }

    // Ensure visibility of the previous image button
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.previous_image))

    // Wait until the button is available
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      try {
        composeTestRule.onNodeWithTag(C.Tag.BookProfile.previous_image).fetchSemanticsNode()
        true
      } catch (e: Exception) {
        false
      }
    }

    // Assert the previous image button is clickable
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.previous_image).assertHasClickAction()

    // Ensure visibility of the next image button
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.next_image))

    // Wait until the button is available
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      try {
        composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).fetchSemanticsNode()
        true
      } catch (e: Exception) {
        false
      }
    }

    // Assert the next image button is clickable
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).assertHasClickAction()
  }

  @Test
  fun pictureChangesOnIconClick() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(
          bookId = testBookId,
          booksRepository = mockBookRepo,
          navController = navigationActions,
      )
    }

    // Ensure the button is visible
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.next_image))

    // Wait until the node is available
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      try {
        composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).fetchSemanticsNode()
        true
      } catch (e: Exception) {
        false
      }
    }

    // Verify the first picture is displayed
    composeTestRule.onNodeWithTag("0_" + C.Tag.BookProfile.image).assertIsDisplayed()

    // Perform a click action on the next image button
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).performClick()

    // Verify the next picture is displayed
    composeTestRule.onNodeWithTag("1_" + C.Tag.BookProfile.image).assertIsDisplayed()
  }
}
