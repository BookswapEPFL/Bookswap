package com.android.bookswap.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.resources.C
import com.android.bookswap.ui.books.BookProfileScreen
import com.android.bookswap.ui.books.edit.EditBookScreen
import io.mockk.coEvery
import io.mockk.mockk
import java.util.UUID
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationFromBookProfileToEditBookTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun EditBookScreen_allows_editing_when_currentUser_is_bookUser() {
    // Arrange
    val currentUserId = UUID.randomUUID()
    val testBookId = UUID.randomUUID()
    val testBook =
        DataBook(
            testBookId,
            "Original Title",
            "Author",
            "Original Description",
            5,
            "https://example.com/photo.jpg",
            BookLanguages.ENGLISH,
            "1234567890",
            listOf(BookGenres.FICTION),
            currentUserId)

    val mockBookRepo = mockk<BooksRepository>(relaxed = true)
    coEvery { mockBookRepo.getBook(eq(testBookId), any(), any()) } answers
        {
          val onSuccess = secondArg<(DataBook) -> Unit>()
          onSuccess(testBook)
        }

    composeTestRule.setContent {
      val navController = rememberNavController()

      // Add a NavHost to handle navigation
      NavHost(navController, C.Screen.BOOK_PROFILE) {
        composable(C.Screen.BOOK_PROFILE) {
          BookProfileScreen(
              testBookId,
              mockBookRepo,
              NavigationActions(navController),
              currentUserId = currentUserId)
        }
        composable("${C.Screen.EDIT_BOOK}/{bookId}") { backStackEntry ->
          val bookId = backStackEntry.arguments?.getString("bookId")?.let { UUID.fromString(it) }
          if (bookId != null) {
            EditBookScreen(
                mockBookRepo, NavigationActions(navController), testBook.copy(uuid = bookId))
          }
        }
      }
    }
    // This is juste temporary as when Matias will do the MainActivity part for the navigation
    // from the Profile to the BookProfile then I will finish the navigation in the main from the
    // BookProfile to the EditBook

    // Act & Assert
    // Assert that the book title is displayed, i.e. the top of the scrollable column is shown
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.title).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.title).assertIsDisplayed()

    // Scrolls to the end of the column
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.scrollable_end))

    // Assert that the Edit Button is displayed
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).assertIsDisplayed()

    // Click the Edit Button and navigate to EditBookScreen
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).performClick()

    // Verify book information on EditBookScreen
    composeTestRule.onNodeWithTag(C.Tag.EditBook.title).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.EditBook.title).assertTextEquals(testBook.title, "Title")

    // composeTestRule.onNodeWithTag("inputBookDescription").assertTextEquals("Original
    // Description")

    // Edit book title and save
    composeTestRule.onNodeWithTag(C.Tag.EditBook.title).performTextInput("Updated Title")
    composeTestRule
        .onNodeWithTag(C.Tag.EditBook.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.EditBook.save))
    composeTestRule.onNodeWithTag(C.Tag.EditBook.save).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.EditBook.save).performClick()

    // Verify updated book is reflected in BookProfileScreen
    coEvery { mockBookRepo.getBook(eq(testBookId), any(), any()) } answers
        {
          val onSuccess = secondArg<(DataBook) -> Unit>()
          onSuccess(testBook.copy(title = "Updated Title"))
        }
  }

  @Test
  fun EditButton_is_not_displayed_when_currentUser_is_different_from_bookUser() {
    // Arrange
    val currentUserId = UUID.randomUUID()
    val bookUserId = UUID.randomUUID()
    val testBookId = UUID.randomUUID()
    val testBook =
        DataBook(
            testBookId,
            "A Book",
            "Author",
            "Description",
            4,
            null,
            BookLanguages.ENGLISH,
            "1234567890",
            listOf(BookGenres.FICTION),
            bookUserId // Different from currentUserId
            )

    val mockBookRepo = mockk<BooksRepository>(relaxed = true)
    coEvery { mockBookRepo.getBook(eq(testBookId), any(), any()) } answers
        {
          val onSuccess = secondArg<(DataBook) -> Unit>()
          onSuccess(testBook)
        }

    // Act
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBookId, mockBookRepo, navigationActions, currentUserId = currentUserId)
    }

    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.scrollable_end))
    // Assert
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).assertDoesNotExist()
  }
}
