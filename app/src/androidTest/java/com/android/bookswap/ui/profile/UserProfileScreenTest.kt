package com.android.bookswap.ui.profile

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserBookViewModel
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.screen.UserProfileScreen
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UserProfileScreenTest : TestCase() {

  private lateinit var photoStorage: PhotoFirebaseStorageSource
  private lateinit var mockUserBookViewModel: UserBookViewModel
  private lateinit var mockBooksRepository: BooksRepository
  private lateinit var mockNavigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()
  private val testUserId = UUID.randomUUID()
  private val standardUser =
      DataUser(
          testUserId,
          "M.",
          "John",
          "Doe",
          "John.Doe@example.com",
          "+41223456789",
          0.0,
          0.0,
          "dummyPic.png")

  private val testBooks =
      listOf(
          DataBook(
              uuid = UUID.randomUUID(),
              title = "Book 1",
              author = "Author 1",
              description =
                  "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
              rating = 3,
              photo = null,
              language = BookLanguages.SPANISH,
              isbn = "978-84-09025-23-5",
              genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
              userId = testUserId,
              false,
              false),
          DataBook(
              uuid = UUID.randomUUID(),
              title = "Book 2",
              author = "Author 2",
              description =
                  "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
              rating = 4,
              photo = null,
              language = BookLanguages.SPANISH,
              isbn = "978-84-09025-23-5",
              genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
              userId = testUserId,
              false,
              false))

  @Before
  fun setup() {
    val userVM: UserViewModel = mockk()
    every { userVM.getUser(any()) } returns standardUser
    every { userVM.uuid } returns standardUser.userUUID
    every { userVM.addressStr } returns MutableStateFlow("address")

    photoStorage = mockk(relaxed = true)
    mockBooksRepository = mockk()
    mockNavigationActions = mockk()
    photoStorage = mockk(relaxed = true)
    mockUserBookViewModel = mockk(relaxed = true)

    // Mock the book data fetching
    coEvery { mockUserBookViewModel.getBooks(standardUser.bookList) } returns testBooks

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      CompositionLocalProvider(LocalAppConfig provides AppConfig(userViewModel = userVM)) {
        UserProfile(
            photoStorage = photoStorage,
            booksRepository = mockBooksRepository,
            userBookViewModel = mockUserBookViewModel,
            navigationActions = mockNavigationActions,
            { TopAppBarComponent(Modifier, navigationActions, "Messages") })
      }
    }
  }

  @Test
  fun testDisplay() {
    run(testName = "assertContent") {
      step("Start User Profile Screen") {
        ComposeScreen.onComposeScreen<UserProfileScreen>(composeTestRule) {
          titleTxt { assertIsDisplayed() }
          fullNameTxt {
            assertIsDisplayed()
            assertTextEquals("M. John Doe")
          }
          emailTxt {
            assertIsDisplayed()
            assertTextEquals("John.Doe@example.com")
          }
          phoneNumberTxt {
            assertIsDisplayed()
            assertTextEquals("+41223456789")
          }
          addressTxt {
            assertIsDisplayed()
            assertTextEquals("address")
          }
          editProfileBtn {
            assertIsDisplayed()
            assertIsEnabled()
          }
        }
      }
    }
  }

  @Test
  fun testTakePhoto() {
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.profileImage).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.profileImage).performClick()
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.profileImageBox).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.take_photo).assertExists()
  }

  @Test
  fun testEdit() {
    run(testName = "assertEditAction") {
      ComposeScreen.onComposeScreen<UserProfileScreen>(composeTestRule) {
        step("Start User Profile Screen") {
          fullNameTxt {
            assertIsDisplayed()
            assertTextEquals("M. John Doe")
          }
          emailTxt {
            assertIsDisplayed()
            assertTextEquals("John.Doe@example.com")
          }
          phoneNumberTxt {
            assertIsDisplayed()
            assertTextEquals("+41223456789")
          }
          addressTxt {
            assertIsDisplayed()
            assertTextEquals("address")
          }
          editProfileBtn {
            assertIsDisplayed()
            assertIsEnabled()
            assertHasClickAction()
          }
        }
        step("Click edit button") {
          editProfileBtn {
            assertIsDisplayed()
            assertIsEnabled()
            assertHasClickAction()
            performClick()
          }
        }
      }
    }
  }

  @Test
  fun testBookListIsDisplayed() {
    // Verify book list container
    composeTestRule.onNodeWithTag(C.Tag.BookListComp.book_list_container).assertIsDisplayed()

    // Verify each book is displayed
    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("1_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()

    // Verify book titles
    composeTestRule
        .onAllNodesWithTag(C.Tag.BookDisplayComp.title)
        .assertAll(hasText("Book 1").or(hasText("Book 2")))

    // Verify book authors
    composeTestRule
        .onAllNodesWithTag(C.Tag.BookDisplayComp.author)
        .assertAll(hasText("Author 1").or(hasText("Author 2")))
  }
}
