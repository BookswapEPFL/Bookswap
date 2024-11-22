package com.android.bookswap.endtoend

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.MainActivity
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.resources.C
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import io.mockk.verify
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddBooksEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockMessageRepository: MessageRepository
  private lateinit var mockBookRepository: BooksRepository
  private lateinit var mockUserRepository: UsersRepository
  private lateinit var mockPhotoStorage: PhotoFirebaseStorageSource

  private lateinit var mockedBook: DataBook

  @Before
  fun setUp() {

    mockMessageRepository = mockk()
    mockBookRepository = mockk()
    mockUserRepository = mockk()
    mockPhotoStorage = mockk()

    every { mockBookRepository.addBook(any(), any()) } just runs

    val testUUID = UUID.randomUUID()
    every { mockBookRepository.getNewUUID() } returns testUUID

    mockedBook =
        DataBook(
            uuid = testUUID,
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            description = "A classic novel set in the Jazz Age.",
            rating = 5,
            isbn = "9780743273565",
            photo = "https://example.com/greatgatsby.jpg",
            language = BookLanguages.ENGLISH)

    mockkConstructor(GoogleBookDataSource::class)
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN("9780743273565", any()) } answers
        {
          val callback = secondArg<(Result<DataBook>) -> Unit>()
          callback(Result.success(mockedBook)) // Simulation de succès avec `mockedBook`
        }

    composeTestRule.setContent {
      MainActivity()
          .BookSwapApp(
              mockMessageRepository,
              mockBookRepository,
              mockUserRepository,
              startDestination = C.Route.NEW_BOOK,
              photoStorage = mockPhotoStorage)
    }
  }

  @Test
  fun testAddBook() {

    composeTestRule.onNodeWithTag(C.Tag.new_book_choice_screen_container).assertExists()

    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_ISBN + C.Tag.NewBookChoice.btnWIcon.button)
        .performClick()
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn).performTextInput("9780743273565")
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.search).performClick()

    verify { anyConstructed<GoogleBookDataSource>().getBookFromISBN("9780743273565", any()) }
    verify { mockBookRepository.addBook(any(), any()) }

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.back_button).performClick()

    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_MANUALLY + C.Tag.NewBookChoice.btnWIcon.button)
        .performClick()
    composeTestRule.onNodeWithTag(C.Tag.new_book_manual_screen_container).assertExists()

    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.title).performTextInput("Test Book Title")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.author).performTextInput("Author Name")
    composeTestRule
        .onNodeWithTag(C.Tag.NewBookManually.synopsis)
        .performTextInput("This is a test description for the book.")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.rating).performTextInput("5")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.isbn).performTextInput("9780743273565")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.photo).performTextInput("photo_url_test")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performTextInput("English")

    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.genres).performClick()
    composeTestRule.onNode(hasText("Fiction")).performClick()

    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).performClick()

    verify {
      mockBookRepository.addBook(
          match { book ->
            book.title == "The Great Gatsby" &&
                book.author == "F. Scott Fitzgerald" &&
                book.description == "A classic novel set in the Jazz Age." &&
                book.rating == 5 &&
                book.isbn == "9780743273565" &&
                book.photo == "https://example.com/greatgatsby.jpg" &&
                book.language == BookLanguages.ENGLISH
          },
          any())
    }
  }
}
