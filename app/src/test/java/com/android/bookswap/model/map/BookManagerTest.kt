package com.android.bookswap.model.map

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class BookManagerTest {
  private val users = listOf(
    DataUser(bookList = listOf(
      UUID(1,2),
      UUID(2,1)
    ),
      longitude = -5.0,
      latitude = 0.0),
    DataUser(bookList = listOf(
      UUID(1,1)
    ),
      longitude = 1.0,
      latitude = 2.0)
    )

  private val books = listOf(
    DataBook(
      uuid = UUID(1, 2),
      title = "Book 1",
      author = "Author 1",
      description = "Description of Book 1",
      rating = 5,
      photo = "url_to_photo_1",
      language = BookLanguages.ENGLISH,
      isbn = "123-456-789",
      genres = listOf(BookGenres.FICTION, BookGenres.HORROR)),
    DataBook(
      uuid = UUID(2, 1),
      title = "Book 2",
      author = "Author 2",
      description = "Description of Book 2",
      rating = 4,
      photo = "url_to_photo_2",
      language = BookLanguages.FRENCH,
      isbn = "234-567-890",
      genres = listOf(BookGenres.FICTION)),
    DataBook(
      uuid = UUID(1, 1),
      title = "Book 3",
      author = "Author 3",
      description = "Description of Book 3",
      rating = 4,
      photo = "url_to_photo_3",
      language = BookLanguages.GERMAN,
      isbn = "234-567-890",
      genres = listOf(BookGenres.NONFICTION, BookGenres.HORROR))
  )

  private val geolocation1 = listOf(3.5,8.0)
  private val geolocation2 = listOf(-10.0,-10.0)

  private lateinit var mockBookRepository: BooksFirestoreRepository
  private lateinit var mockGeolocation1: IGeolocation
  private lateinit var mockGeolocation2: IGeolocation
  private lateinit var mockBookFilter: BookFilter
  private lateinit var mockBookFilterEmpty: BookFilter
  private lateinit var bookManager: BookManager

  @Before
  fun setup() {
    mockBookRepository = mockk()
    every { mockBookRepository.getBook(any(), any()) } answers {
      firstArg<(List<DataBook>) -> Unit>().invoke(books)
    }

    mockGeolocation1 = mockk()
    every { mockGeolocation1.longitude } answers {MutableStateFlow(geolocation1[0])}
    every { mockGeolocation1.latitude } answers {MutableStateFlow(geolocation1[1])}

    mockGeolocation2 = mockk()
    every { mockGeolocation2.longitude } answers {MutableStateFlow(geolocation2[0])}
    every { mockGeolocation2.latitude } answers {MutableStateFlow(geolocation2[1])}

    mockBookFilter = mockk()
    every { mockBookFilter.genresFilter } answers { MutableStateFlow(listOf(BookGenres.HORROR))}
    every { mockBookFilter.languagesFilter } answers { MutableStateFlow(listOf(BookLanguages.FRENCH))}

    mockBookFilterEmpty = mockk()
    every { mockBookFilterEmpty.genresFilter } answers { MutableStateFlow(emptyList())}
    every { mockBookFilterEmpty.languagesFilter } answers { MutableStateFlow(emptyList())}
  }

  @Test
  fun listMarkerBookChangedWhenFilterApplied() = runTest {
    bookManager = BookManager(mockGeolocation1, mockBookRepository, users, mockBookFilterEmpty)
    assertEquals(books, bookManager.filteredBooks.value)
  }
}
