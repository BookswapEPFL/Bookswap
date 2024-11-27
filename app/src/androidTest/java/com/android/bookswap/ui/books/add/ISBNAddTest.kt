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
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.Runs
import io.mockk.andThenJust
import io.mockk.every
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
  private lateinit var mockUserVM: UserViewModel

  @Before
  fun init() {
    mockkStatic(Toast::class)
    toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock

    mockUserVM = mockk(relaxed = true)
    every { mockUserVM.getUser() } returns
        DataUser(
            UUID.randomUUID(),
            "Hello",
            "John",
            "Doe",
            "eleanorroosevelt@myownpersonaldomain.com",
            "+1234567890",
            0.0,
            0.0,
            "https://www.example.com/profile.jpg",
            listOf(UUID.randomUUID()),
            "googleUID",
            listOf("contact1", "contact2"))

    mockkConstructor(DataUser::class)

    every { anyConstructed<DataUser>().greeting } returns "Hello"
    every { anyConstructed<DataUser>().firstName } returns "John"
    every { anyConstructed<DataUser>().lastName } returns "Doe"
    every { anyConstructed<DataUser>().email } returns "john.doe@example.com"
    every { anyConstructed<DataUser>().phoneNumber } returns "+1234567890"
    every { anyConstructed<DataUser>().latitude } returns 0.0
    every { anyConstructed<DataUser>().longitude } returns 0.0
    every { anyConstructed<DataUser>().profilePictureUrl } returns "mockPicUrl.png"
    every { anyConstructed<DataUser>().bookList } returns emptyList()
    every { anyConstructed<DataUser>().googleUid } returns "mockGoogleUid"
    every { anyConstructed<DataUser>().contactList } returns emptyList()
  }

  @Test
  fun elementsAreDisplayed() {
    composeTestRule.setContent {
      val mockNavigationActions: NavigationActions = mockk()
      val mockBooksRepository: BooksRepository = mockk()
      AddISBNScreen(mockNavigationActions, mockBooksRepository, mockUserVM)
    }

    val isbnField = composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn)
    isbnField.assertIsDisplayed()
    Assert.assertEquals(
        "ISBN*", isbnField.fetchSemanticsNode().config[SemanticsProperties.Text][0].text)

    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.search).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.search).assertHasClickAction()
  }

  @Suppress("TestFunctionName")
  @Test
  fun ISBNFieldWork() {
    composeTestRule.setContent {
      val mockNavigationActions: NavigationActions = mockk()
      val mockBooksRepository: BooksRepository = mockk()
      AddISBNScreen(mockNavigationActions, mockBooksRepository, mockUserVM)
    }
    val isbnField = composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn)

    isbnField.performTextInput("testEmpty")
    Assert.assertEquals(
        "", isbnField.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)

    isbnField.performTextClearance()
    isbnField.performTextInput("12845")
    Assert.assertEquals(
        "12845", isbnField.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
  }
  /*
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


      // Mock call to repository
      val mockBooksRepository: BooksRepository = mockk()
      every { mockBooksRepository.addBook(matchDataBook(dataBook), any()) } answers
          {
            secondArg<(Result<Unit>) -> Unit>()(Result.success(Unit))
          } andThenJust
          Runs

    // Mock call to api
    mockkConstructor(GoogleBookDataSource::class)
    every {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(bookISBN, userUUID, any())
    } answers { thirdArg<(Result<DataBook>) -> Unit>()(Result.success(dataBook)) } andThenJust Runs


      // Mock the navigation
      val mockNavigationActions: NavigationActions = mockk()
      every { mockNavigationActions.navigateTo(any(TopLevelDestination::class)) } just Runs

      composeTestRule.setContent { AddISBNScreen(mockNavigationActions, mockBooksRepository, mockUserVM) }

      composeTestRule.onNodeWithTag("isbn_field").performTextInput(dataBook.isbn!!)
      composeTestRule.onNodeWithTag("isbn_searchButton").performClick()

    composeTestRule.setContent {
      AddISBNScreen(mockNavigationActions, mockBooksRepository, userId = userUUID)
    }

    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn).performTextInput(bookISBN)
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.search).performClick()

    verify {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(bookISBN, userUUID, any())
    } // Api is called
    verify { mockBooksRepository.addBook(matchDataBook(dataBook), any()) } // Book is added
    verify {
      mockNavigationActions.navigateTo(any(TopLevelDestination::class))
    } // Navigation is called when book is added
  }

  */

  @Suppress("TestFunctionName")
  @Test
  fun ISBNAPIRequestFailed() {
    // Mock bad call to api
    mockkConstructor(GoogleBookDataSource::class)
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(any(), any(), any()) } answers
        {
          thirdArg<(Result<DataBook>) -> Unit>()(Result.failure(IllegalArgumentException()))
        } andThenJust
        Runs

    // Mock the navigation
    val mockNavigationActions: NavigationActions = mockk()
    // Mock call to repository
    val mockBooksRepository: BooksRepository = mockk()

    composeTestRule.setContent {
      AddISBNScreen(mockNavigationActions, mockBooksRepository, mockUserVM)
    }

    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn).performTextInput("BAD_ISBN")
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.search).performClick()

    verify {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(any(), any(), any())
    } // Api is called
    verify { toastMock.show() }
  }

  @Suppress("TestFunctionName")
  @Test
  fun ISBNRepositoryCallFailed() {
    val bookUUID = UUID.randomUUID()
    val userUUID = mockUserVM.getUser().userUUID
    val bookISBN = "9780435123437"
    val dataBook =
        DataBook(
            bookUUID,
            "Flowers for Algernon",
            null,
            null,
            null,
            null,
            BookLanguages.OTHER,
            bookISBN,
            emptyList(),
            userUUID)

    // Mock call to api
    mockkConstructor(GoogleBookDataSource::class)
    every {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(bookISBN, userUUID, any())
    } answers { thirdArg<(Result<DataBook>) -> Unit>()(Result.success(dataBook)) } andThenJust Runs

    // Mock failed call to repository
    val mockBooksRepository: BooksRepository = mockk()
    every { mockBooksRepository.addBook(any(), any()) } answers
        {
          secondArg<(Result<Unit>) -> Unit>()(Result.failure(Exception("Error message")))
        } andThenJust
        Runs

    // Mock the navigation
    val mockNavigationActions: NavigationActions = mockk()

    composeTestRule.setContent {
      AddISBNScreen(mockNavigationActions, mockBooksRepository, mockUserVM)
    }

    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn).performTextInput(bookISBN)
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.search).performClick()

    verify {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(bookISBN, mockUserVM.getUser().userUUID, any())
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(bookISBN, userUUID, any())
    } // Api is called
    verify { mockBooksRepository.addBook(any(), any()) } // Book repository is called
    verify { toastMock.show() } // Error is displayed
  }
}
