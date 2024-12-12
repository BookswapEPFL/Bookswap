package com.android.bookswap.model.add

import android.content.Context
import android.widget.Toast
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.UserViewModel
import io.mockk.*
import java.util.*
import org.junit.Before
import org.junit.Test

class AddToBookViewModelTest {

  private lateinit var booksRepository: BooksRepository
  private lateinit var context: Context
  private lateinit var viewModel: AddToBookViewModel
  private lateinit var mockUserViewModel: UserViewModel

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
          userId = UUID(2, 2),
          archived = false,
          exchange = true)

  @Before
  fun setup() {
    booksRepository = mockk()
    context = mockk()
    mockUserViewModel = mockk(relaxed = true)
    viewModel = AddToBookViewModel(booksRepository, mockUserViewModel)

    // Mock Toast.makeText
    mockkStatic(Toast::class)
    val mockToast = mockk<Toast>(relaxed = true)
    every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

    mockkStatic(UUID::class)
    every { UUID.randomUUID() } returns UUID(1, 1)
  }

  @Test
  fun `updateDataBook updates book successfully`() {
    every { booksRepository.addBook(any(), any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }
    viewModel.saveDataBook(
        context,
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
      booksRepository.addBook(
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
  }
}
