package com.android.bookswap.ui.books

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.DefaultMockKs
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserViewModel
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
  private val mockUserViewModel: UserViewModel = DefaultMockKs.mockKUserViewModel
  private lateinit var testBook: DataBook

  @Before
  fun setup() {
    testBook =
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
            mockUserViewModel.uuid)
  }

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
      CompositionLocalProvider(
          LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
            BookProfileScreen(testBookId, mockBookRepo, navigationActions)
          }
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
      CompositionLocalProvider(
          LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
            BookProfileScreen(testBookId, mockBookRepo, navigationActions)
          }
    }

    composeTestRule.onNodeWithTag(C.Tag.BookProfile.previous_image).assertHasClickAction()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).assertHasClickAction()
  }

  @Test
  fun pictureChangesOnIconClick() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      CompositionLocalProvider(
          LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
            BookProfileScreen(testBookId, mockBookRepo, navigationActions)
          }
    }

    // Verify the first picture is displayed
    composeTestRule.onNodeWithTag("0_" + C.Tag.BookProfile.image).assertIsDisplayed()

    // Perform a click action on the icon
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.next_image).performClick()

    // Verify the next picture is displayed
    composeTestRule.onNodeWithTag("1_" + C.Tag.BookProfile.image).assertIsDisplayed()
  }
}
