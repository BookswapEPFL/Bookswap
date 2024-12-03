package com.android.bookswap.model

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class UserBookViewModelTest {

    private lateinit var booksRepository: BooksRepository
    private lateinit var userBookViewModel: UserBookViewModel

    @Before
    fun setup() {
        booksRepository = mockk()
        userBookViewModel = UserBookViewModel(booksRepository)
    }

    @Test
    fun `getBooks should return list of books when all calls are successful`() = runTest {
        val bookId1 = UUID.randomUUID()
        val bookId2 = UUID.randomUUID()
        val bookList = listOf(bookId1, bookId2)
        val dataBook1 = DataBook(
            uuid = bookId1,
            title = "Book One",
            author = "Author One",
            description = "Description One",
            rating = 4,
            photo = "photo1.jpg",
            language = BookLanguages.ENGLISH,
            isbn = "1234567890",
            genres = listOf(BookGenres.FICTION),
            userId = UUID.randomUUID()
        )
        val dataBook2 = DataBook(
            uuid = bookId2,
            title = "Book Two",
            author = "Author Two",
            description = "Description Two",
            rating = 5,
            photo = "photo2.jpg",
            language = BookLanguages.SPANISH,
            isbn = "0987654321",
            genres = listOf(BookGenres.NONFICTION),
            userId = UUID.randomUUID()
        )

        coEvery { booksRepository.getBook(eq(bookId1), any(), any()) } answers {
            secondArg<(DataBook) -> Unit>().invoke(dataBook1)
        }
        coEvery { booksRepository.getBook(eq(bookId2), any(), any()) } answers {
            secondArg<(DataBook) -> Unit>().invoke(dataBook2)
        }

        // Get books
        val result = userBookViewModel.getBooks(bookList)

        // Assert
        Assert.assertEquals(listOf(dataBook1, dataBook2), result)
    }

    @Test
    fun `getBooks should throw exception when one or more calls fail`() = runTest {
        val bookId1 = UUID.randomUUID()
        val bookId2 = UUID.randomUUID()
        val bookList = listOf(bookId1, bookId2)
        val exceptionMessage = "Book not found"

        coEvery { booksRepository.getBook(eq(bookId1), any(), any()) } answers {
            secondArg<(DataBook) -> Unit>().invoke(
                DataBook(
                    uuid = bookId1,
                    title = "Book One",
                    author = "Author One",
                    description = "Description One",
                    rating = 4,
                    photo = "photo1.jpg",
                    language = BookLanguages.ENGLISH,
                    isbn = "1234567890",
                    genres = listOf(BookGenres.FICTION),
                    userId = UUID.randomUUID()
                )
            )
        }

        coEvery { booksRepository.getBook(eq(bookId2), any(), any()) } answers {
            thirdArg<(Exception) -> Unit>().invoke(Exception(exceptionMessage))
        }

        // Get Books & Assert
        try {
            userBookViewModel.getBooks(bookList)
            Assert.fail("Exception expected")
        } catch (e: Exception) {
            Assert.assertTrue(e.message!!.contains("Errors occurred"))
        }
    }
}
