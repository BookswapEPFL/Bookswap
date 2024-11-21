package com.android.bookswap.model.books.edit

import android.content.Context
import android.widget.Toast
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.*
import java.util.*
import org.junit.Before
import org.junit.Test

class EditBookViewModelTest {

  private lateinit var booksRepository: BooksRepository
  private lateinit var navigation: NavigationActions
  private lateinit var context: Context
  private lateinit var viewModel: EditBookViewModel

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
          genres = listOf(BookGenres.FICTION))

  @Before
  fun setup() {
    booksRepository = mockk()
    navigation = mockk()
    context = mockk()
    viewModel = EditBookViewModel(booksRepository, navigation)

    // Mock Toast.makeText
    mockkStatic(Toast::class)
    val mockToast = mockk<Toast>(relaxed = true)
    every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

    every { booksRepository.updateBook(any(), any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }

    every { navigation.goBack() } just Runs
  }

  @Test
  fun `updateDataBook updates book successfully`() {
    viewModel.updateDataBook(
        context,
        book.uuid,
        book.title,
        book.author!!,
        book.description!!,
        book.rating!!.toString(),
        book.photo!!,
        book.language.toString(),
        book.isbn!!,
        book.genres)

    verify { booksRepository.updateBook(eq(book), any()) }
    verify { navigation.goBack() }
  }

  @Test
  fun `updateDataBook shows error on invalid language`() {
    viewModel.updateDataBook(
        context,
        book.uuid,
        book.title,
        book.author!!,
        book.description!!,
        book.rating!!.toString(),
        book.photo!!,
        "Test",
        book.isbn!!,
        book.genres)

    verify(exactly = 0) { booksRepository.updateBook(any(), any()) }
  }

  @Test
  fun `deleteBooks deletes book successfully`() {
    val uuid = UUID.randomUUID()
    every { booksRepository.deleteBooks(uuid, any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }

    viewModel.deleteBooks(context, uuid)

    verify { booksRepository.deleteBooks(uuid, any()) }
    verify { navigation.goBack() }
  }

  @Test
  fun `deleteBooks shows error when deletion fails`() {
    // Arrange
    val uuid = UUID.randomUUID()
    every { booksRepository.deleteBooks(uuid, any()) } answers
        {
          val callback = secondArg<(Result<Unit>) -> Unit>()
          callback(Result.failure(Exception("Deletion failed")))
        }

    // Act
    viewModel.deleteBooks(context, uuid)

    // Assert
    verify { booksRepository.deleteBooks(uuid, any()) }
    verify(exactly = 0) { navigation.goBack() }
  }
}
