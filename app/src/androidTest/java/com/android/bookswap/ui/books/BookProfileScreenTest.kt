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

    private val testBook = DataBook(
        uuid = testBookId,
        title = "Historia de España",
        author = "Jose Ignacio Pastor Iglesias",
        description = "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
        rating = 9,
        photo = null,
        language = BookLanguages.SPANISH,
        isbn = "978-84-09025-23-5",
        genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
        userId = currentUserId
    )

    @Before
    fun setUp() {
        mockNavController = mockk()
        mockBookRepo = mockk()

        // Mocking the getBook call to return the test book
        coEvery { mockBookRepo.getBook(testBookId, any(), any()) } answers {
            val onSuccess = it.invocation.args[1] as (DataBook) -> Unit
            onSuccess(testBook)
        }
    }

    @Test
    fun hasRequiredComponents() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            BookProfileScreen(bookId = testBookId, booksRepository = mockBookRepo, navController = navigationActions, currentUserId = currentUserId)
        }

        composeTestRule.onNodeWithTag("bookTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookAuthor").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookProfileScroll").performScrollToNode(hasTestTag("bookProfileEditionPlace"))
        composeTestRule.onNodeWithTag("bookProfileLanguage").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookProfileGenresTitle").assertIsDisplayed()
        testBook.genres.forEach { genre ->
            composeTestRule.onNodeWithTag("bookProfileGenre${genre.Genre}").assertIsDisplayed()
        }
        composeTestRule.onNodeWithTag("bookProfileISBN").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookProfileDate").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookProfileVolume").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookProfileIssue").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookProfileEditorial").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookProfileEditionPlace").assertIsDisplayed()
    }

    @Test
    fun iconsAreClickable(){
        composeTestRule.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            BookProfileScreen(bookId = testBookId, booksRepository = mockBookRepo, navController = navigationActions, currentUserId = currentUserId)
        }

        composeTestRule.onNodeWithTag("bookProfileImageLeft").assertHasClickAction()
        composeTestRule.onNodeWithTag("bookProfileImageRight").assertHasClickAction()
    }

    @Test
    fun pictureChangesOnIconClick(){
        composeTestRule.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            BookProfileScreen(bookId = testBookId, booksRepository = mockBookRepo, navController = navigationActions, currentUserId = currentUserId)
        }

        // Verify the first picture is displayed
        composeTestRule.onNodeWithTag("bookProfileImage Isabel La Catolica").assertIsDisplayed()

        // Perform a click action on the icon
        composeTestRule.onNodeWithTag("bookProfileImageRight").performClick()

        // Verify the next picture is displayed
        composeTestRule.onNodeWithTag("bookProfileImage Felipe II").assertIsDisplayed()
    }
}