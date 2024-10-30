package com.android.bookswap.ui.books.edit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class EditBookScreenTest {

    @MockK
    private lateinit var booksRepository: BooksFirestoreRepository

    @MockK
    private lateinit var navigationActions: NavigationActions

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleBook = DataBook(
        uuid = UUID.randomUUID(),
        title = "Sample Book",
        author = "Sample Author",
        description = "Sample Description",
        rating = 4,
        photo = "sample_photo_url",
        language = BookLanguages.ENGLISH,
        isbn = "123456789",
        genres = listOf(BookGenres.FANTASY)
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { navigationActions.currentRoute() } returns "EDIT_BOOK"
    }

    @Test
    fun displayAllComponents() {
        composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

        composeTestRule.onNodeWithTag("editBookScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("editBookTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("editBookTitle").assertTextEquals("Edit your Book")
        composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookSave").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookSave").assertTextEquals("Save")
        composeTestRule.onNodeWithTag("bookDelete").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bookDelete").assertTextEquals("Delete")

        composeTestRule.onNodeWithTag("inputBookTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("inputBookAuthor").assertIsDisplayed()
        composeTestRule.onNodeWithTag("inputBookDescription").assertIsDisplayed()
        composeTestRule.onNodeWithTag("inputBookRating").assertIsDisplayed()
        composeTestRule.onNodeWithTag("inputBookPhoto").assertIsDisplayed()
        composeTestRule.onNodeWithTag("inputBookLanguage").assertIsDisplayed()
    }

    @Test
    fun inputsHaveInitialValue() {
        composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

        composeTestRule.onNodeWithTag("inputBookTitle").assertTextContains(sampleBook.title)
        composeTestRule.onNodeWithTag("inputBookAuthor").assertTextContains(sampleBook.author ?: "")
        composeTestRule.onNodeWithTag("inputBookDescription").assertTextContains(sampleBook.description ?: "")
        composeTestRule.onNodeWithTag("inputBookPhoto").assertTextContains(sampleBook.photo ?: "")
        composeTestRule.onNodeWithTag("inputBookLanguage").assertTextContains(sampleBook.language.toString())
    }

    @Test
    fun genreDropdownWorks() {
        composeTestRule.setContent { EditBookScreen(booksRepository, navigationActions, sampleBook) }

        // opens genre dropdown and select a genre
        composeTestRule.onNodeWithTag("GenreDropdown").performClick()
        composeTestRule.onNodeWithTag("GenreDropdownItem_Fantasy").performClick()

        // verify the selected genre
        composeTestRule.onNodeWithTag("SelectedGenre").assertTextContains("Fantasy")
    }
}
