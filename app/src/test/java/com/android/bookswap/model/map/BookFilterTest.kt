package com.android.bookswap.model.map

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import java.util.UUID
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class BookFilterTest {
  private val bookFilter = BookFilter()
  private val bookList =
      listOf(
          DataBook(
              uuid = UUID.randomUUID(),
              title = "Book 1",
              author = "Author 1",
              description = "Description of Book 1",
              rating = 5,
              photo = null,
              language = BookLanguages.ENGLISH,
              isbn = null,
              genres = listOf(BookGenres.FANTASY),
              userId = UUID.randomUUID()
              ),
          DataBook(
              uuid = UUID.randomUUID(),
              title = "Book 2",
              author = "Author 2",
              description = "Description of Book 2",
              rating = 4,
              photo = null,
              language = BookLanguages.FRENCH,
              isbn = null,
              genres = listOf(BookGenres.FICTION),
              userId = UUID.randomUUID()
          ),
          DataBook(
              uuid = UUID.randomUUID(),
              title = "Book 3",
              author = "Author 3",
              description = "Description of Book 3",
              rating = null,
              photo = null,
              language = BookLanguages.GERMAN,
              isbn = null,
              genres = listOf(BookGenres.SCIENCEFICTION, BookGenres.AUTOBIOGRAPHY),
              userId = UUID.randomUUID())
      )

  @Before fun setUp() {}

  @Test
  fun `empty filter should return all books`() {
    val filteredBooks = bookFilter.filterBooks(bookList)
    assertEquals(bookList, filteredBooks)
  }

  @Test
  fun `filter by genre should return matching books`() {
    bookFilter.setGenres(listOf("Fiction"))
    val filteredBooks = bookFilter.filterBooks(bookList)
    assertEquals(listOf(bookList[1]), filteredBooks)

    bookFilter.setGenres(listOf("Fantasy", "Science-Fiction"))
    val filteredBooks2 = bookFilter.filterBooks(bookList)
    assertEquals(listOf(bookList[0], bookList[2]), filteredBooks2)
  }

  @Test
  fun `filter by language should return matching books`() {
    bookFilter.setLanguages(listOf("French"))
    val filteredBooks = bookFilter.filterBooks(bookList)
    assertEquals(listOf(bookList[1]), filteredBooks)
  }

  @Test
  fun `filter by genre and language should return matching books`() {
    bookFilter.setGenres(listOf("Science-Fiction"))
    bookFilter.setLanguages(listOf("German"))
    val filteredBooks = bookFilter.filterBooks(bookList)
    assertEquals(listOf(bookList[2]), filteredBooks)
  }
}
