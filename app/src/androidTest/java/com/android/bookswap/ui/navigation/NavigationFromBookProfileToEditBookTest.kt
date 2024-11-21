package com.android.bookswap.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
            uuid = testBookId,
            title = "Original Title",
            author = "Author",
            description = "Original Description",
            rating = 5,
            photo = "https://example.com/photo.jpg",
            language = BookLanguages.ENGLISH,
            isbn = "1234567890",
            genres = listOf(BookGenres.FICTION),
            userId = currentUserId)

    val mockBookRepo = mockk<BooksRepository>(relaxed = true)
    coEvery { mockBookRepo.getBook(eq(testBookId), any(), any()) } answers
        {
          val onSuccess = secondArg<(DataBook) -> Unit>()
          onSuccess(testBook)
        }

    composeTestRule.setContent {
      val navController = rememberNavController()

      // Add a NavHost to handle navigation
      NavHost(navController = navController, startDestination = Screen.BOOK_PROFILE) {
        composable(Screen.BOOK_PROFILE) {
          BookProfileScreen(
              bookId = testBookId,
              booksRepository = mockBookRepo,
              navController = NavigationActions(navController),
              currentUserId = currentUserId)
        }
        composable("${Screen.EDIT_BOOK}/{bookId}") { backStackEntry ->
          val bookId = backStackEntry.arguments?.getString("bookId")?.let { UUID.fromString(it) }
          if (bookId != null) {
            EditBookScreen(
                booksRepository = mockBookRepo,
                navigationActions = NavigationActions(navController),
                book = testBook.copy(uuid = bookId))
          }
        }
      }
    }
    // This is juste temporary as when Matias will do the MainActivity part for the navigation
    // from the Profile to the BookProfile then I will finish the navigation in the main from the
    // BookProfile to the EditBook

    // Act & Assert
    // Verify EditButton is displayed
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed()

    // Click EditButton and navigate to EditBookScreen
    composeTestRule.onNodeWithTag("editButton").performClick()

    // Verify book information on EditBookScreen
    // composeTestRule.onNodeWithTag("inputBookTitle").assertTextEquals("Original Title")
    composeTestRule.onNodeWithText("Original Title").assertIsDisplayed()

    // composeTestRule.onNodeWithTag("inputBookDescription").assertTextEquals("Original
    // Description")

    // Edit book title and save
    composeTestRule.onNodeWithTag("inputBookTitle").performTextInput("Updated Title")
    composeTestRule
        .onNodeWithTag("editBookScreenColumn")
        .performScrollToNode(hasTestTag("bookSave"))
    composeTestRule.onNodeWithTag("bookSave").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bookSave").performClick()

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
            uuid = testBookId,
            title = "A Book",
            author = "Author",
            description = "Description",
            rating = 4,
            photo = null,
            language = BookLanguages.ENGLISH,
            isbn = "1234567890",
            genres = listOf(BookGenres.FICTION),
            userId = bookUserId // Different from currentUserId
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

    // Assert
    composeTestRule.onNodeWithTag("editButton").assertDoesNotExist()
  }
}
