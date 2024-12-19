package com.android.bookswap.model.edit

import android.content.Context
import android.widget.Toast
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.isNetworkAvailable
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.*
import java.util.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any

class EditBookViewModelTest {

  private lateinit var booksRepository: BooksRepository
  private lateinit var navigation: NavigationActions
  private lateinit var context: Context
  private lateinit var viewModel: EditBookViewModel
  private lateinit var mockUserViewModel: UserViewModel

  private val uuidBook = UUID.randomUUID()
  private val uuidUser = UUID.randomUUID()
  private val user = DataUser(userUUID = uuidUser, firstName = "Corin", bookList = listOf(uuidBook))

  private val book =
      DataBook(
          uuid = uuidBook,
          title = "Test Title",
          author = "Test Author",
          description = "Test Description",
          rating = 5,
          photo = "Test photo URL",
          language = BookLanguages.ENGLISH,
          isbn = "123456789",
          genres = listOf(BookGenres.FICTION),
          userId = uuidUser,
          archived = false,
          exchange = true)

  @Before
  fun setup() {
    val mockUserRepository: UsersRepository = mockk()

    mockUserViewModel = spyk(UserViewModel(uuidUser, mockUserRepository, user))
    every { mockUserViewModel.getUser(any()) } returns user
    every { mockUserViewModel.uuid } returns uuidUser
    every { mockUserViewModel.getUser() } returns user
    every {
      mockUserViewModel.updateUser(
          any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs

    mockkStatic(::isNetworkAvailable)
    every { isNetworkAvailable(any()) } returns true
    booksRepository = mockk()
    navigation = mockk()
    context = mockk(relaxed = true)
    viewModel = EditBookViewModel(booksRepository, navigation, mockUserViewModel)

    every { mockUserViewModel.getUser() } returns user

    // Mock Toast.makeText
    mockkStatic(Toast::class)
    val mockToast = mockk<Toast>(relaxed = true)
    every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

    every { navigation.goBack() } just Runs
  }

  @After
  fun tearDown() {
    // Clear all mocks to avoid problem with gradle task "check"
    unmockkAll()
  }

  @Test
  fun `updateDataBook updates book successfully`() {
    every { booksRepository.updateBook(any(), any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }
    viewModel.updateDataBook(
        context,
        book.uuid,
        book.title,
        book.author!!,
        book.description!!,
        book.rating!!.toString(),
        book.photo!!,
        book.language,
        book.isbn!!,
        book.genres,
        book.archived,
        book.exchange)

    verify {
      booksRepository.updateBook(
          match { updatedBook ->
            updatedBook.uuid == book.uuid &&
                updatedBook.title == book.title &&
                updatedBook.author == book.author &&
                updatedBook.description == book.description &&
                updatedBook.rating == book.rating &&
                updatedBook.photo == book.photo &&
                updatedBook.language == book.language &&
                updatedBook.isbn == book.isbn &&
                updatedBook.genres == book.genres &&
                updatedBook.archived == book.archived &&
                updatedBook.exchange == book.exchange
          },
          any())
    }

    verify { navigation.goBack() }
  }

  @Test
  fun `updateDataBook doesn't navigate back on failure`() {
    every { booksRepository.updateBook(any(), any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.failure(Exception("Updating failed")))
        }
    viewModel.updateDataBook(
        context,
        book.uuid,
        book.title,
        book.author!!,
        book.description!!,
        book.rating!!.toString(),
        book.photo!!,
        book.language,
        book.isbn!!,
        book.genres,
        book.archived,
        book.exchange)

    verify(exactly = 0) { navigation.goBack() }
  }

  @Test
  fun `deleteBook deletes book successfully`() {
    every { booksRepository.deleteBook(uuidBook, any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }

    viewModel.deleteBook(context, uuidBook)

    verify { booksRepository.deleteBook(uuidBook, any()) }
    verify { mockUserViewModel.updateUser(firstName = "Corin", bookList = emptyList()) }
    verify { navigation.goBack() }
  }

  @Test
  fun `deleteBook don't go back when deletion fails`() {
    // Arrange
    every { booksRepository.deleteBook(uuidBook, any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.failure(Exception("Deletion failed")))
        }

    // Act
    viewModel.deleteBook(context, uuidBook)

    // Assert
    verify { booksRepository.deleteBook(uuidBook, any()) }
    verify(exactly = 0) { navigation.goBack() }
  }
}
