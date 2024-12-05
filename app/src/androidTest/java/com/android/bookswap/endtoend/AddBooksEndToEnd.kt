package com.android.bookswap.endtoend

import android.content.Context
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.MainActivity
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.add.AddToBookViewModel
import com.android.bookswap.model.chat.OfflineMessageStorage
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
  private lateinit var mockMessageStorage: OfflineMessageStorage
  private lateinit var mockContext: Context

  private lateinit var mockedBook: DataBook

  @Before
  fun setUp() {

    mockMessageRepository = mockk()
    mockBookRepository = mockk()
    mockUserRepository = mockk()
    mockPhotoStorage = mockk()
    mockMessageStorage = mockk()
    mockContext = mockk()

    every { mockBookRepository.addBook(any(), any()) } just runs

    val testUUID = UUID.randomUUID()
    every { mockBookRepository.getNewUUID() } returns testUUID

    mockedBook =
        DataBook(
            testUUID,
            "The Great Gatsby",
            "F. Scott Fitzgerald",
            "A classic novel set in the Jazz Age.",
            5,
            "https://example.com/greatgatsby.jpg",
            BookLanguages.FRENCH,
            "9780743273565",
            emptyList(),
            testUUID,
            false,
            false)

    mockkConstructor(GoogleBookDataSource::class)
    every {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN("9780743273565", any(), any())
    } answers
        {
          val callback = thirdArg<(Result<DataBook>) -> Unit>()
          callback(Result.success(mockedBook))
        }

    every { mockUserRepository.getUser(uuid = any(), any()) } just runs

    composeTestRule.setContent {
      MainActivity()
          .BookSwapApp(
              mockMessageRepository,
              mockBookRepository,
              mockUserRepository,
              C.Route.NEW_BOOK,
              mockPhotoStorage,
              mockMessageStorage,
              context = mockContext)
    }
  }

  @Test
  fun testAddBook() {

    composeTestRule.onNodeWithTag(C.Tag.new_book_choice_screen_container).assertExists()

    composeTestRule.onNodeWithTag("From ISBN" + C.Tag.NewBookChoice.btnWIcon.button).performClick()
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.isbn).performTextInput("9780743273565")
    composeTestRule.onNodeWithTag(C.Tag.NewBookISBN.search).performClick()

    verify {
      anyConstructed<GoogleBookDataSource>().getBookFromISBN(eq("9780743273565"), any(), any())
    }
    verify { mockBookRepository.addBook(any(), any()) }

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.back_button).performClick()

    composeTestRule.onNodeWithTag("Manually" + C.Tag.NewBookChoice.btnWIcon.button).performClick()
    composeTestRule.onNodeWithTag(C.Tag.new_book_manual_screen_container).assertExists()

      composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_field).performClick()
      composeTestRule.onNode(hasText("Fiction")).performClick()
      composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_field).performClick()

      composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.title_field).performClick()

      composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.title_field).performTextInput("Test Book Title")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.author_field).performTextInput("Author Name")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.description_field)
        .performTextInput("This is a test description for the book.")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.rating_field).performTextInput("5")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.isbn_field).performTextInput("9780743273565")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.photo_field).performTextInput("photo_url_test")
      composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.photo_field).performClick()



    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.scrollable).performScrollToNode(hasTestTag(C.Tag.BookEntryComp.language_field))



    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).performClick()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_menu + "_French").performClick()
      composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).performClick()

    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.action_buttons).performClick()


    mockkConstructor(AddToBookViewModel::class)

    verify {
      mockBookRepository.addBook(
          match { book ->
            book.title == "Test Book Title" &&
                book.author == "Author Name" &&
                book.description == "This is a test description for the book." &&
                book.rating == 5 &&
                book.isbn == "9780743273565" &&
                book.photo == "photo_url_test" &&
                book.language == BookLanguages.FRENCH
          },
          any())
    }
  }
}
