package com.android.bookswap.ui.profile

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.OthersUserViewModel
import com.android.bookswap.model.UserBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Test class for the OtherUserProfileScreen. */
@RunWith(AndroidJUnit4::class)
class OthersUserProfileTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockOthersUserViewModel: OthersUserViewModel
  private lateinit var mockUserBookViewModel: UserBookViewModel
  private lateinit var mockBooksRepository: BooksRepository
  private lateinit var mockNavigationActions: NavigationActions

  private val testUserId = UUID.randomUUID()
  private val testUser =
      DataUser(
          userUUID = testUserId,
          greeting = "Mr.",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          phoneNumber = "1234567890",
          latitude = 45.0,
          longitude = 50.0,
          profilePictureUrl = "",
          bookList = listOf(UUID.randomUUID(), UUID.randomUUID()))

  private val testBooks =
      listOf(
          DataBook(
              uuid = UUID.randomUUID(),
              title = "Book 1",
              author = "Author 1",
              description =
                  "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
              rating = 3,
              photo = "https://example.com/photo.jpg",
              language = BookLanguages.SPANISH,
              isbn = "978-84-09025-23-5",
              genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
              userId = UUID.randomUUID(),
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
              userId = UUID.randomUUID(),
              false,
              false))

  @Before
  fun setup() {
    mockBooksRepository = mockk()
    mockNavigationActions = mockk()
    mockOthersUserViewModel = mockk(relaxed = true)
    mockUserBookViewModel = mockk(relaxed = true)

    // Mock the user data fetching
    every { mockOthersUserViewModel.getUserByUUID(testUserId, any()) } answers
        {
          val callback = secondArg<(DataUser?) -> Unit>()
          callback(testUser)
        }

    // Mock the book data fetching
    coEvery { mockUserBookViewModel.getBooks(testUser.bookList) } returns testBooks
  }

  @Test
  fun testUserDetailsDisplayed() {
    composeTestRule.setContent {
      OthersUserProfileScreen(
          userId = testUserId,
          otherUserVM = mockOthersUserViewModel,
          booksRepository = mockBooksRepository,
          userBookViewModel = mockUserBookViewModel,
          navigationActions = mockNavigationActions)
    }

    // Verify user details
    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.fullname + C.Tag.LabeledText.label)
        .assertIsDisplayed()
        .assertTextEquals("Name:")
    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.fullname + C.Tag.LabeledText.text)
        .assertIsDisplayed()
        .assertTextEquals("Mr. John Doe")

    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.email + C.Tag.LabeledText.label)
        .assertIsDisplayed()
        .assertTextEquals("Email:")
    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.email + C.Tag.LabeledText.text)
        .assertIsDisplayed()
        .assertTextEquals("john.doe@example.com")

    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.phone + C.Tag.LabeledText.label)
        .assertIsDisplayed()
        .assertTextEquals("Phone:")
    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.phone + C.Tag.LabeledText.text)
        .assertIsDisplayed()
        .assertTextEquals("1234567890")

    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.address + C.Tag.LabeledText.label)
        .assertIsDisplayed()
        .assertTextEquals("Address:")
    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.address + C.Tag.LabeledText.text)
        .assertIsDisplayed()
        .assertTextEquals("45.0, 50.0")

    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.chatButton)
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Message with John")
  }

  @Test
  fun testBookListDisplayed() {
    composeTestRule.setContent {
      OthersUserProfileScreen(
          userId = testUserId,
          otherUserVM = mockOthersUserViewModel,
          booksRepository = mockBooksRepository,
          userBookViewModel = mockUserBookViewModel,
          navigationActions = mockNavigationActions)
    }

    // Verify book list is displayed
    composeTestRule.onNodeWithTag(C.Tag.BookListComp.book_list_container).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("1_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag(C.Tag.BookDisplayComp.title)
        .assertAll(hasText("Book 1").or(hasText("Book 2")))
    composeTestRule
        .onAllNodesWithTag(C.Tag.BookDisplayComp.author)
        .assertAll(hasText("Author 1").or(hasText("Author 2")))
  }

  @Test
  fun displaysImage_whenPhotoUrlIsNotEmpty() {
    val imageTestUserId = UUID.randomUUID()
    val imageTestUser =
        DataUser(
            userUUID = imageTestUserId,
            greeting = "Mr.",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            latitude = 45.0,
            longitude = 50.0,
            profilePictureUrl = "https://test_profile_pic.jpg",
            bookList = listOf(UUID.randomUUID(), UUID.randomUUID()))

    // Mock the user data fetching
    every { mockOthersUserViewModel.getUserByUUID(imageTestUserId, any()) } answers
        {
          val callback = secondArg<(DataUser?) -> Unit>()
          callback(imageTestUser)
        }

    composeTestRule.setContent {
      OthersUserProfileScreen(
          userId = imageTestUserId,
          otherUserVM = mockOthersUserViewModel,
          booksRepository = mockBooksRepository,
          userBookViewModel = mockUserBookViewModel,
          navigationActions = mockNavigationActions)
    }
    // Verify the container is displayed
    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.profilePictureContainer)
        .assertIsDisplayed()

    // Verify the picture is displayed inside the container
    composeTestRule.onNodeWithTag(C.Tag.OtherUserProfile.profile_image_picture).assertIsDisplayed()

    // Ensure the icon is not displayed
    composeTestRule.onNodeWithTag(C.Tag.OtherUserProfile.profile_image_icon).assertDoesNotExist()
  }

  @Test
  fun displaysIcon_whenPhotoUrlIsEmpty() {
    val imageTestUserId = UUID.randomUUID()
    val imageTestUser =
        DataUser(
            userUUID = imageTestUserId,
            greeting = "Mr.",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            latitude = 45.0,
            longitude = 50.0,
            profilePictureUrl = "",
            bookList = listOf(UUID.randomUUID(), UUID.randomUUID()))

    // Mock the user data fetching
    every { mockOthersUserViewModel.getUserByUUID(imageTestUserId, any()) } answers
        {
          val callback = secondArg<(DataUser?) -> Unit>()
          callback(imageTestUser)
        }

    composeTestRule.setContent {
      OthersUserProfileScreen(
          userId = imageTestUserId,
          otherUserVM = mockOthersUserViewModel,
          booksRepository = mockBooksRepository,
          userBookViewModel = mockUserBookViewModel,
          navigationActions = mockNavigationActions)
    }
    // Verify the container is displayed
    composeTestRule
        .onNodeWithTag(C.Tag.OtherUserProfile.profilePictureContainer)
        .assertIsDisplayed()

    // Verify the icon is displayed inside the container
    composeTestRule.onNodeWithTag(C.Tag.OtherUserProfile.profile_image_icon).assertIsDisplayed()

    // Ensure the picture is not displayed
    composeTestRule.onNodeWithTag(C.Tag.OtherUserProfile.profile_image_picture).assertDoesNotExist()
  }
}
