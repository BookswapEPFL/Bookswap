package com.android.bookswap.model.edit

import android.content.Context
import android.widget.Toast
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.*
import java.util.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class EditBookViewModelTest {

  private lateinit var booksRepository: BooksRepository
  private lateinit var navigation: NavigationActions
  private lateinit var context: Context
  private lateinit var viewModel: EditBookViewModel
  private lateinit var mockUserViewModel: UserViewModel

  private val user = DataUser(UUID.randomUUID())

  private val book =
      DataBook(
          uuid = UUID(1, 1),
          title = "Test Title",
          author = "Test Author",
          description = "Test Description",
          rating = 5,
          photo = "Test photo URL",
          language = BookLanguages.ENGLISH,
          isbn = "123456789",
          genres = listOf(BookGenres.FICTION),
          userId = user.userUUID,
          archived = false,
          exchange = true)

  private val uuid = UUID.randomUUID()

  @Before
  fun setup() {
    mockUserViewModel = mockk(relaxed = true)
    booksRepository = mockk()
    navigation = mockk()
    context = mockk()
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
    every { booksRepository.deleteBook(uuid, any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }

    viewModel.deleteBook(context, uuid)

    verify { booksRepository.deleteBook(uuid, any()) }
    verify { navigation.goBack() }
  }

  @Test
  fun `deleteBook don't go back when deletion fails`() {
    // Arrange
    every { booksRepository.deleteBook(uuid, any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.failure(Exception("Deletion failed")))
        }

    // Act
    viewModel.deleteBook(context, uuid)

    // Assert
    verify { booksRepository.deleteBook(uuid, any()) }
    verify(exactly = 0) { navigation.goBack() }
  }
}
