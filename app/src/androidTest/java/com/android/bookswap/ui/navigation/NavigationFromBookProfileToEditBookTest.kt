package com.android.bookswap.ui.navigation

import androidx.compose.runtime.CompositionLocalProvider
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
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.books.BookProfileScreen
import com.android.bookswap.ui.books.edit.EditBookScreen
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationFromBookProfileToEditBookTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockUserRepository: UsersRepository
  private lateinit var userVM: UserViewModel
  private lateinit var mockBookRepo: BooksRepository
  private val currentUserId: UUID = UUID.randomUUID()
  private val testBookId: UUID = UUID.randomUUID()
  private val bookUserId: UUID = UUID.randomUUID()
  private lateinit var testBookOwner: DataBook
  private lateinit var testBookOther: DataBook
  private lateinit var mockEditVM: EditBookViewModel

  @Before
  fun setUp() {
    testBookOwner =
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
            currentUserId,
            false,
            false)
    testBookOther = testBookOwner.copy(userId = bookUserId)

    mockEditVM = mockk(relaxed = true)
    every { mockEditVM.deleteBook(any(), any()) } just runs
    every {
      mockEditVM.updateDataBook(
          any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs
    every { mockEditVM.getBook(any(), any(), any()) } answers
        {
          secondArg<(DataBook) -> Unit>()(testBookOwner)
        }
    mockUserRepository = mockk()
    userVM = UserViewModel(currentUserId, mockUserRepository)
    mockBookRepo = mockk<BooksRepository>(relaxed = true)

    every { mockUserRepository.getUser(currentUserId, any()) } answers { currentUserId }
    every { userVM.getUser().userUUID } returns currentUserId
  }

  @Test
  fun EditBookScreen_allows_editing_when_currentUser_is_bookUser() {
    // Arrange
    coEvery { mockBookRepo.getBook(eq(testBookId), any(), any()) } answers
        {
          val onSuccess = secondArg<(DataBook) -> Unit>()
          onSuccess(testBookOwner)
        }

    composeTestRule.setContent {
      val navController = rememberNavController()
      CompositionLocalProvider(LocalAppConfig provides AppConfig(userViewModel = userVM)) {
        // Add a NavHost to handle navigation
        NavHost(navController, C.Screen.BOOK_PROFILE) {
          composable(C.Screen.BOOK_PROFILE) {
            BookProfileScreen(testBookId, mockBookRepo, NavigationActions(navController))
          }
          composable("${C.Screen.EDIT_BOOK}/{bookUUID}") { backStackEntry ->
            val bookUUID =
                backStackEntry.arguments?.getString("bookUUID")?.let { UUID.fromString(it) }
            if (bookUUID != null) {
              EditBookScreen(mockEditVM, NavigationActions(navController), bookUUID)
            }
          }
        }
      }
    }
    // Act & Assert
    // Assert that the book title is displayed, i.e. the top of the scrollable column is shown
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.title).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.title).assertIsDisplayed()

    // Scrolls to the end of the column
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.edit))

    // Assert that the Edit Button is displayed
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).assertTextEquals("Edit Book")

    // Click the Edit Button and navigate to EditBookScreen
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).performClick()

    // Verify book information on EditBookScreen
    composeTestRule.onNodeWithTag(C.Tag.EditBook.title).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.EditBook.title)
        .assertTextEquals(testBookOwner.title, "Title")

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
          onSuccess(testBookOwner.copy(title = "Updated Title"))
        }
  }

  @Test
  fun EditButton_is_not_displayed_when_currentUser_is_different_from_bookUser() {
    // Arrange
    coEvery { mockBookRepo.getBook(eq(testBookId), any(), any()) } answers
        {
          val onSuccess = secondArg<(DataBook) -> Unit>()
          onSuccess(testBookOther)
        }

    // Act
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      CompositionLocalProvider(LocalAppConfig provides AppConfig(userViewModel = userVM)) {
        BookProfileScreen(testBookId, mockBookRepo, navigationActions)
      }
    }

    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.edit))
    // Assert
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.edit).assertTextEquals("Go to User")
  }
}
