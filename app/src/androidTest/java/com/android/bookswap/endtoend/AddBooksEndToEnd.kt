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
import com.android.bookswap.ui.navigation.Route
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

    mockedBook =
        DataBook(
            uuid = UUID.randomUUID(),
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
              startDestination = Route.NEWBOOK,
              photoStorage = mockPhotoStorage)
    }
  }

  @Test
  fun testAddBook() {

    composeTestRule.onNodeWithTag("addBookChoiceScreen").assertExists()

    composeTestRule.onNodeWithTag("button_From ISBN").performClick()
    composeTestRule.onNodeWithTag("isbn_field").assertExists()
    composeTestRule.onNodeWithTag("isbn_field").performTextInput("9780743273565")
    composeTestRule.onNodeWithTag("isbn_searchButton").performClick()

    verify { anyConstructed<GoogleBookDataSource>().getBookFromISBN("9780743273565", any()) }
    verify { mockBookRepository.addBook(any(), any()) }

    composeTestRule.onNodeWithTag("backButton").performClick()

    composeTestRule.onNodeWithTag("button_Manually").performClick()
    composeTestRule.onNodeWithTag("addBookScreen").assertExists()

    composeTestRule.onNodeWithTag("title_field").performTextInput("Test Book Title")
    composeTestRule.onNodeWithTag("author_field").performTextInput("Author Name")
    composeTestRule
        .onNodeWithTag("description_field")
        .performTextInput("This is a test description for the book.")
    composeTestRule.onNodeWithTag("rating_field").performTextInput("5")
    composeTestRule.onNodeWithTag("isbn_field").performTextInput("9780743273565")
    composeTestRule.onNodeWithTag("photo_field").performTextInput("photo_url_test")
    composeTestRule.onNodeWithTag("language_field").performTextInput("English")

    composeTestRule.onNodeWithTag("genre_field").performClick()
    composeTestRule.onNode(hasText("Fiction")).performClick()

    composeTestRule.onNodeWithTag("save_button").performClick()

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
