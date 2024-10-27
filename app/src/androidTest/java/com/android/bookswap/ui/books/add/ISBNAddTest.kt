package com.android.bookswap.ui.books.add

import android.widget.Toast
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.TopLevelDestination
import com.android.bookswap.utils.matchDataBook
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.Runs
import io.mockk.andThenJust
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import java.util.UUID
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ISBNAddTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var toastMock: Toast

  @Before
  fun init() {
    mockkStatic(Toast::class)
    toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock
  }

  @Test
  fun elementsAreDisplayed() {
    composeTestRule.setContent {
      val mockNavigationActions: NavigationActions = mockk()
      val mockBooksRepository: BooksRepository = mockk()

      AddISBNScreen(mockNavigationActions, mockBooksRepository)
    }

    val isbnField = composeTestRule.onNodeWithTag("isbn_field")
    isbnField.assertIsDisplayed()
    Assert.assertEquals(
        "ISBN*", isbnField.fetchSemanticsNode().config[SemanticsProperties.Text][0].text)

    composeTestRule.onNodeWithTag("isbn_searchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("isbn_searchButton").assertHasClickAction()
  }

  @Suppress("TestFunctionName")
  @Test
  fun ISBNFieldWork() {
    composeTestRule.setContent {
      val mockNavigationActions: NavigationActions = mockk()
      val mockBooksRepository: BooksRepository = mockk()
      AddISBNScreen(mockNavigationActions, mockBooksRepository)
    }
    val isbnField = composeTestRule.onNodeWithTag("isbn_field")

    isbnField.performTextInput("testEmpty")
    Assert.assertEquals(
        "", isbnField.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)

    isbnField.performTextClearance()
    isbnField.performTextInput("12845")
    Assert.assertEquals(
        "12845", isbnField.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
  }

  @Suppress("TestFunctionName")
  @Test
  fun ISBNRequestSucceeded() {
    val dataBook =
        DataBook(
            uuid = UUID.randomUUID(),
            title = "Flowers for Algernon",
            author = null,
            description = null,
            rating = null,
            photo = null,
            language = BookLanguages.OTHER,
            isbn = "9780435123437")

    // Mock call to api
    mockkConstructor(GoogleBookDataSource::class)
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(dataBook.isbn!!, any()) } answers
        {
          secondArg<(Result<DataBook>) -> Unit>()(Result.success(dataBook))
        } andThenJust
        Runs

    // Mock call to repository
    val mockBooksRepository: BooksRepository = mockk()
    every { mockBooksRepository.addBook(matchDataBook(dataBook), any(), any()) } answers
        {
          secondArg<() -> Unit>()()
        } andThenJust
        Runs

    // Mock the navigation
    val mockNavigationActions: NavigationActions = mockk()
    every { mockNavigationActions.navigateTo(any(TopLevelDestination::class)) } just Runs

    composeTestRule.setContent { AddISBNScreen(mockNavigationActions, mockBooksRepository) }

    composeTestRule.onNodeWithTag("isbn_field").performTextInput(dataBook.isbn!!)
    composeTestRule.onNodeWithTag("isbn_searchButton").performClick()

    verify {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(dataBook.isbn!!, any())
    } // Api is called
    verify { mockBooksRepository.addBook(matchDataBook(dataBook), any(), any()) } // Book is added
    verify {
      mockNavigationActions.navigateTo(any(TopLevelDestination::class))
    } // Navigation is called when book is added
  }

  @Suppress("TestFunctionName")
  @Test
  fun ISBNAPIRequestFailed() {
    // Mock bad call to api
    mockkConstructor(GoogleBookDataSource::class)
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(any(), any()) } answers
        {
          secondArg<(Result<DataBook>) -> Unit>()(Result.failure(IllegalArgumentException()))
        } andThenJust
        Runs

    // Mock the navigation
    val mockNavigationActions: NavigationActions = mockk()
    // Mock call to repository
    val mockBooksRepository: BooksRepository = mockk()

    composeTestRule.setContent { AddISBNScreen(mockNavigationActions, mockBooksRepository) }

    composeTestRule.onNodeWithTag("isbn_field").performTextInput("BAD_ISBN")
    composeTestRule.onNodeWithTag("isbn_searchButton").performClick()

    verify { anyConstructed<GoogleBookDataSource>().getBookFromISBN(any(), any()) } // Api is called
    verify { toastMock.show() }
  }

  @Suppress("TestFunctionName")
  @Test
  fun ISBNRepositoryCallFailed() {
    val dataBook =
        DataBook(
            uuid = UUID.randomUUID(),
            title = "Flowers for Algernon",
            author = null,
            description = null,
            rating = null,
            photo = null,
            language = BookLanguages.OTHER,
            isbn = "9780435123437")

    // Mock call to api
    mockkConstructor(GoogleBookDataSource::class)
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(dataBook.isbn!!, any()) } answers
        {
          secondArg<(Result<DataBook>) -> Unit>()(Result.success(dataBook))
        } andThenJust
        Runs

    // Mock failed call to repository
    val mockBooksRepository: BooksRepository = mockk()
    every { mockBooksRepository.addBook(matchDataBook(dataBook), any(), any()) } answers
        {
          thirdArg<(Exception) -> Unit>()(Exception("Error message"))
        } andThenJust
        Runs

    // Mock the navigation
    val mockNavigationActions: NavigationActions = mockk()

    composeTestRule.setContent { AddISBNScreen(mockNavigationActions, mockBooksRepository) }

    composeTestRule.onNodeWithTag("isbn_field").performTextInput(dataBook.isbn!!)
    composeTestRule.onNodeWithTag("isbn_searchButton").performClick()

    verify {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(dataBook.isbn!!, any())
    } // Api is called
    verify {
      mockBooksRepository.addBook(matchDataBook(dataBook), any(), any())
    } // Book repository is called
    verify { toastMock.show() } // Error is displayed
  }
}
