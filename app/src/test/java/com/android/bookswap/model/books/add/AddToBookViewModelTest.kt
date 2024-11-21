package com.android.bookswap.model.books.add

import android.content.Context
import android.widget.Toast
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.add.AddToBookViewModel
import io.mockk.*
import java.util.*
import org.junit.Before
import org.junit.Test

class AddToBookViewModelTest {

    private lateinit var booksRepository: BooksRepository
    private lateinit var context: Context
    private lateinit var viewModel: AddToBookViewModel

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
        context = mockk()
        viewModel = AddToBookViewModel(booksRepository)

        // Mock Toast.makeText
        mockkStatic(Toast::class)
        val mockToast = mockk<Toast>(relaxed = true)
        every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

        every { booksRepository.addBook(any(), any()) } answers
                {
                    val callback = secondArg<(Result<Unit>) -> Unit>()
                    callback(Result.success(Unit))
                }
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns UUID(1,1)
    }

    @Test
    fun `updateDataBook updates book successfully`() {
        viewModel.saveDataBook(
            context,
            book.title,
            book.author!!,
            book.description!!,
            book.rating!!.toString(),
            book.photo!!,
            book.language.toString(),
            book.isbn!!,
            book.genres)

        verify { booksRepository.addBook(eq(book), any()) }
    }

    @Test
    fun `updateDataBook shows error on invalid language`() {
        viewModel.saveDataBook(
            context,
            book.title,
            book.author!!,
            book.description!!,
            book.rating!!.toString(),
            book.photo!!,
            "Test",
            book.isbn!!,
            book.genres)

        verify(exactly = 0) { booksRepository.addBook(any(), any()) }
    }
}
