package com.android.bookswap.model.map

import android.location.Location
import android.util.Log
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.ui.map.UserBooksWithLocation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

const val REFRESH_TIME_PERIOD = 5000L

/**
 * The `BookManager` class is responsible for managing book data and user data with location
 * information, fetching data from the `BooksRepository`, computing distances between the current
 * location and user locations, and filtering books based on user preferences. The list of all books
 * can be obtained with the filteredBooks and the list of the list of books with the location of its
 * owner with the filteredUsers
 *
 * @param geolocation the geolocation of the current user
 * @param booksRepository an instance of [BooksRepository] to retrieve the books from the database.
 * @param listUser list of users [DataUser], will be replaced in the future by an instance of
 *   [UserFirestoreSource]
 * @param bookFilter an instance of [BookFilter] that manages the filter that needs to be applied.
 * @param isTesting if true, BookManager use the given computation method for distances.
 * @param testComputingDistance a computation method for distances for testing
 */
class BookManager(
    private val geolocation: IGeolocation,
    private val booksRepository: BooksRepository,
    // TODO replace the listUser by a User repository to retrieve the users from the database
    listUser: List<DataUser>,
    private val bookFilter: BookFilter,
    // For the unit tests, the Android framework cannot be interacted with. The
    // Location.distanceBetween needs to be replaced for testing.
    private val isTesting: Boolean = false,
    private val testComputingDistance: (Double, Double, Double, Double) -> Double = { _, _, _, _ ->
      Double.NaN
    }
) {
  // Internal MutableStateFlows to manage dynamic data
  private val _allBooks = MutableStateFlow<List<DataBook>>(emptyList())
  private val _allUsers = MutableStateFlow(listUser)
  private val _allUserDistance = MutableStateFlow<List<Pair<DataUser, Double>>>(emptyList())
  private val _filteredBooks = MutableStateFlow<List<DataBook>>(emptyList())
  private val _filteredUsers = MutableStateFlow<List<UserBooksWithLocation>>(emptyList())

  // Public StateFlows for UI to observe
  val filteredBooks: StateFlow<List<DataBook>> = _filteredBooks.asStateFlow()
  val filteredUsers: StateFlow<List<UserBooksWithLocation>> = _filteredUsers.asStateFlow()

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  init {
    scope.launch {
      while (true) {
        fetchBooksFromRepository()
        delay(REFRESH_TIME_PERIOD)
      }
    }
    computeDistanceOfUsers()
    combineFlowsAndFilterBooks()
  }

  // Fetch books from the repository and update `_allBooks`
  private fun fetchBooksFromRepository() {
    booksRepository.getBook(
        OnSucess = { books -> _allBooks.value = books },
        onFailure = { error -> Log.e("BookManager", "Failed to fetch books: ${error.message}") })
  }

  // Combine books and filter flows and apply filtering logic
  private fun combineFlowsAndFilterBooks() {
    scope.launch {
      combine(_allBooks, _allUserDistance, bookFilter.genresFilter, bookFilter.languagesFilter) {
              books,
              userDistance,
              _,
              _ ->
            val userBooksWithLocation =
                userDistance.map { user ->
                  UserBooksWithLocation(
                      longitude = user.first.longitude,
                      latitude = user.first.latitude,
                      books =
                          bookFilter.filterBooks(books).filter { book ->
                            book.uuid in user.first.bookList
                          })
                }

            _filteredBooks.value = userBooksWithLocation.flatMap { it.books }
            _filteredUsers.value = userBooksWithLocation
          }
          .collect()
    }
  }

  private fun computeDistanceOfUsers() {
    scope.launch {
      combine(_allUsers, geolocation.latitude, geolocation.longitude) { users, latitude, longitude
            ->
            val userDistance =
                users.map { user ->
                  user to computeDistance(latitude, longitude, user.latitude, user.longitude)
                }
            userDistance.sortedBy { it.second }
          }
          .collect { sortedUserDistance -> _allUserDistance.value = sortedUserDistance }
    }
  }

  private fun computeDistance(
      startLatitude: Double,
      startLongitude: Double,
      endLatitude: Double,
      endLongitude: Double
  ): Double {
    if (isTesting) {
      return testComputingDistance(startLatitude, startLongitude, endLatitude, endLongitude)
    } else {
      val result = FloatArray(1)
      Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result)
      return result[0].toDouble()
    }
  }
}
