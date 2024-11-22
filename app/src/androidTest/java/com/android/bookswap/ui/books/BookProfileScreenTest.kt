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
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockNavController: NavigationActions

  private val testBook =
      DataBook(
          uuid = UUID.randomUUID(),
          title = "Historia de España",
          author = "Jose Ignacio Pastor Iglesias",
          description =
              "Recuento de la historia de España desde los primeros pobladores hasta la actualidad. Escrito con especial enfasis en los reyes catolicos y la exploracion de América.",
          rating = 9,
          photo = null,
          language = BookLanguages.SPANISH,
          isbn = "978-84-09025-23-5",
          genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY))

  @Before
  fun setUp() {
    mockNavController = mockk()
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBook, navigationActions)
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
      BookProfileScreen(testBook, navigationActions)
    }

    composeTestRule.onNodeWithTag(C.Tag.BookProfile.previous_image).assertHasClickAction()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).assertHasClickAction()
  }

  @Test
  fun pictureChangesOnIconClick() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBook, navigationActions)
    }

    // Verify the first picture is displayed
    composeTestRule.onNodeWithTag("0_" + C.Tag.BookProfile.image).assertIsDisplayed()

    // Perform a click action on the icon
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).performClick()

    // Verify the next picture is displayed
    composeTestRule.onNodeWithTag("1_" + C.Tag.BookProfile.image).assertIsDisplayed()
  }
}
