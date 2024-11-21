package com.android.bookswap.model.map

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.UserBooksWithLocation
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.BooksFirestoreSource
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import junit.framework.TestCase.assertEquals
import kotlin.math.abs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BookManagerViewModelTest {

  private val user1 =
      DataUser(
          userUUID = UUID(1, 1),
          bookList = listOf(UUID(1, 2), UUID(2, 1)),
          longitude = 50.0,
          latitude = 50.0)
  private val user2 =
      DataUser(
          userUUID = UUID(2, 2), bookList = listOf(UUID(1, 1)), longitude = 0.0, latitude = 0.0)
  private val users = listOf(user2, user1)

  private val book1 =
      DataBook(
          uuid = UUID(1, 2),
          title = "Book 1",
          author = "Author 1",
          description = "Description of Book 1",
          rating = 5,
          photo = "url_to_photo_1",
          language = BookLanguages.ENGLISH,
          isbn = "123-456-789",
          genres = listOf(BookGenres.FICTION, BookGenres.HORROR))

  private val book2 =
      DataBook(
          uuid = UUID(2, 1),
          title = "Book 2",
          author = "Author 2",
          description = "Description of Book 2",
          rating = 4,
          photo = "url_to_photo_2",
          language = BookLanguages.GERMAN,
          isbn = "234-567-890",
          genres = listOf(BookGenres.FICTION))

  private val book3 =
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

  private val books = listOf(book3, book1, book2)

  private val userBooksWithLocation1 =
      UserBooksWithLocation(user1.userUUID, user1.longitude, user1.latitude, listOf(book1, book2))

  private val userBooksWithLocation2 =
      UserBooksWithLocation(user2.userUUID, user2.longitude, user2.latitude, listOf(book3))

  private val userBooksWithLocation = listOf(userBooksWithLocation2, userBooksWithLocation1)

  private val filteredBooksWithLocation = listOf(userBooksWithLocation2)

  private val geolocation1 = listOf(0.0, 0.0)
  private val geolocation2 = listOf(100.0, 100.0)

  private val sortingTest: (Double, Double, Double, Double) -> Double =
      { startLatitude, _, endLatitude, _ ->
        abs(endLatitude - startLatitude)
      }

  private val mockBookRepository: BooksFirestoreSource = mockk()
  private val mockUsersRepository: UsersRepository = mockk()
  private val mockGeolocation1: IGeolocation = mockk()
  private val mockGeolocation2: IGeolocation = mockk()
  private val mockBookFilter: BookFilter = mockk()
  private val mockBookFilterEmpty: BookFilter = mockk()

  @Before
  fun setup() {
    every { mockBookRepository.getBook(any()) } answers
        {
          firstArg<(Result<List<DataBook>>) -> Unit>().invoke(Result.success(books))
        }
    every { mockUsersRepository.getUsers(any()) } answers
        {
          firstArg<(Result<List<DataUser>>) -> Unit>().invoke(Result.success(users))
        }

    every { mockGeolocation1.longitude } answers { MutableStateFlow(geolocation1[0]) }
    every { mockGeolocation1.latitude } answers { MutableStateFlow(geolocation1[1]) }

    every { mockGeolocation2.longitude } answers { MutableStateFlow(geolocation2[0]) }
    every { mockGeolocation2.latitude } answers { MutableStateFlow(geolocation2[1]) }

    every { mockBookFilter.genresFilter } answers { MutableStateFlow(listOf(BookGenres.HORROR)) }
    every { mockBookFilter.languagesFilter } answers
        {
          MutableStateFlow(listOf(BookLanguages.GERMAN))
        }
    every { mockBookFilter.filterBooks(any()) } answers { listOf(book3) }

    every { mockBookFilterEmpty.genresFilter } answers { MutableStateFlow(emptyList()) }
    every { mockBookFilterEmpty.languagesFilter } answers { MutableStateFlow(emptyList()) }
    every { mockBookFilterEmpty.filterBooks(any()) } answers { firstArg() }
  }

  @Test
  fun defaultCaseNoFilterOrSortingNecessary() = runTest {
    val bookManagerViewModel =
        BookManagerViewModel(
            mockGeolocation1, mockBookRepository, mockUsersRepository, mockBookFilterEmpty) {
                _,
                _,
                _,
                _ ->
              0.0
            }
    bookManagerViewModel.startUpdatingBooks()
    bookManagerViewModel.filteredBooks.first { it != emptyList<DataBook>() }
    bookManagerViewModel.filteredUsers.first { it != emptyList<UserBooksWithLocation>() }
    assertEquals(books, bookManagerViewModel.filteredBooks.value)
    assertEquals(userBooksWithLocation, bookManagerViewModel.filteredUsers.value)
    bookManagerViewModel.stopUpdatingBooks()
  }

  @Test
  fun returnFilteredListOfBooks() = runTest {
    val bookManagerViewModel =
        BookManagerViewModel(
            mockGeolocation1, mockBookRepository, mockUsersRepository, mockBookFilter, sortingTest)
    bookManagerViewModel.startUpdatingBooks()
    bookManagerViewModel.filteredBooks.first { it != emptyList<DataBook>() }
    bookManagerViewModel.filteredUsers.first { it != emptyList<UserBooksWithLocation>() }
    assertEquals(listOf(book3), bookManagerViewModel.filteredBooks.value)
    assertEquals(filteredBooksWithLocation, bookManagerViewModel.filteredUsers.value)
    bookManagerViewModel.stopUpdatingBooks()
  }

  @Test
  fun startUpdatingWorksAfterStop() = runTest {
    val bookManagerViewModel =
        BookManagerViewModel(
            mockGeolocation1, mockBookRepository, mockUsersRepository, mockBookFilter, sortingTest)
    every { mockBookFilter.filterBooks(any()) } answers { emptyList() }
    bookManagerViewModel.startUpdatingBooks()
    bookManagerViewModel.stopUpdatingBooks()
    every { mockBookFilter.filterBooks(any()) } answers { listOf(book3) }
    bookManagerViewModel.startUpdatingBooks()
    bookManagerViewModel.filteredBooks.first { it != emptyList<DataBook>() }
    bookManagerViewModel.filteredUsers.first { it != emptyList<UserBooksWithLocation>() }
    assertEquals(listOf(book3), bookManagerViewModel.filteredBooks.value)
    assertEquals(filteredBooksWithLocation, bookManagerViewModel.filteredUsers.value)
    bookManagerViewModel.stopUpdatingBooks()
  }

  @Test
  fun sortTheUsers() = runTest {
    val bookManagerViewModel =
        BookManagerViewModel(
            mockGeolocation2,
            mockBookRepository,
            mockUsersRepository,
            mockBookFilterEmpty,
            sortingTest)
    bookManagerViewModel.startUpdatingBooks()
    bookManagerViewModel.filteredBooks.first { it != emptyList<DataBook>() }
    bookManagerViewModel.filteredUsers.first { it != emptyList<UserBooksWithLocation>() }
    assertEquals(listOf(book1, book2, book3), bookManagerViewModel.filteredBooks.value)
    assertEquals(
        listOf(userBooksWithLocation1, userBooksWithLocation2),
        bookManagerViewModel.filteredUsers.value)
    bookManagerViewModel.stopUpdatingBooks()
  }
}
