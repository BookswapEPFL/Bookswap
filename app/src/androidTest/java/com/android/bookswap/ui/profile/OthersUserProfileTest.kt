package com.android.bookswap.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.OthersUserViewModel
import com.android.bookswap.model.UserBookViewModel
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

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockOthersUserViewModel: OthersUserViewModel
    private lateinit var mockUserBookViewModel: UserBookViewModel
    private lateinit var mockBooksRepository: BooksRepository

    private val testUserId = UUID.randomUUID()
    private val testUser = DataUser(
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        phoneNumber = "1234567890",
        latitude = 45.0,
        longitude = 50.0,
        profilePictureUrl = "",
        bookList = listOf(UUID.randomUUID(), UUID.randomUUID())
    )

    private val testBooks = listOf(
    DataBook(
        uuid = UUID.randomUUID(),
        title = "Book 1",
        author = "Author 1",
        description = "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
        rating = 3,
        photo = null,
        language = BookLanguages.SPANISH,
        isbn = "978-84-09025-23-5",
        genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
        userId = UUID.randomUUID()),
    DataBook(
        uuid = UUID.randomUUID(),
        title = "Book 2",
        author = "Author 2",
        description = "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
        rating = 4,
        photo = null,
        language = BookLanguages.SPANISH,
        isbn = "978-84-09025-23-5",
        genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
        userId = UUID.randomUUID())
    )

    @Before
    fun setup() {
        mockBooksRepository = mockk()
        mockOthersUserViewModel = mockk(relaxed = true)
        mockUserBookViewModel = mockk(relaxed = true)

        // Mock the user data fetching
        every { mockOthersUserViewModel.getUserByUUID(testUserId, any()) } answers {
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
                    userBookViewModel = mockUserBookViewModel
                )
        }


        // Verify user details
        composeTestRule.onNodeWithText("Name:").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()

        composeTestRule.onNodeWithText("Email:").assertIsDisplayed()
        composeTestRule.onNodeWithText("john.doe@example.com").assertIsDisplayed()

        composeTestRule.onNodeWithText("Phone:").assertIsDisplayed()
        composeTestRule.onNodeWithText("1234567890").assertIsDisplayed()

        composeTestRule.onNodeWithText("Address:").assertIsDisplayed()
        composeTestRule.onNodeWithText("45.0, 50.0").assertIsDisplayed()

    }

    @Test
    fun testBookListDisplayed() {
        composeTestRule.setContent {
                OthersUserProfileScreen(
                    userId = testUserId,
                    otherUserVM = mockOthersUserViewModel,
                    booksRepository = mockBooksRepository,
                    userBookViewModel = mockUserBookViewModel
                )
        }

        // Verify book list is displayed
        composeTestRule.onNodeWithTag("otherUserBookList").assertIsDisplayed()
        composeTestRule.onNodeWithText("Book 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Book 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Author 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Author 2").assertIsDisplayed()
    }
}
