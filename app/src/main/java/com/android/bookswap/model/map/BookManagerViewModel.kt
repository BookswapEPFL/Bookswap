package com.android.bookswap.model.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.UserBooksWithLocation
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.UsersRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private const val REFRESH_TIME_DELAY = 5000L
private const val RETRY_TIME_DELAY = 250L
private const val MAXIMUM_RETRIES = 3

/**
 * The `BookManagerViewModel` class is responsible for managing book data and user data with
 * location information, fetching data from the `BooksRepository`, computing distances between the
 * current location and user locations, and filtering books based on user preferences. The list of
 * all books can be obtained with the filteredBooks and the list of the list of books with the
 * location of its owner with the filteredUsers
 *
 * @param geolocation the geolocation of the current user
 * @param booksRepository an instance of [BooksRepository] to retrieve the books from the database.
 * @param userRepository an instance of [UsersRepository] to retrieve the users from the database.
 * @param bookFilter an instance of [BookFilter] that manages the filter that needs to be applied.
 * @param computingDistanceMethod optional : a computation method for distances for testing purposes
 */
class BookManagerViewModel(
    private val geolocation: IGeolocation,
    private val booksRepository: BooksRepository,
    private val userRepository: UsersRepository,
    private val bookFilter: BookFilter,
    // For the unit tests, the Android framework cannot be interacted with. The
    // Location.distanceBetween needs to be replaced for testing.
    private val computingDistanceMethod: (Double, Double, Double, Double) -> Double =
        { startLatitude, startLongitude, endLatitude, endLongitude ->
          val result = FloatArray(1)
          Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result)
          result[0].toDouble()
        }
) : ViewModel() {
  // Internal MutableStateFlows to manage dynamic data
  private val _allBooks = MutableStateFlow<List<DataBook>>(emptyList())
  private val _allUsers = MutableStateFlow<List<DataUser>>(emptyList())
  private val _allUserDistance = MutableStateFlow<List<Pair<DataUser, Double>>>(emptyList())
  private val _filteredBooks = MutableStateFlow<List<DataBook>>(emptyList())
  private val _filteredUsers = MutableStateFlow<List<UserBooksWithLocation>>(emptyList())

  // Public StateFlows for UI to observe
  val filteredBooks: StateFlow<List<DataBook>> = _filteredBooks.asStateFlow()
  val filteredUsers: StateFlow<List<UserBooksWithLocation>> = _filteredUsers.asStateFlow()

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private var fetchBooksFromRepositoryJob: Job? = null
  private var computeDistanceOfUsersJob: Job? = null
  private var combineFlowsAndFilterBooksJob: Job? = null

  /**
   * Starts updating books and user distances.
   *
   * This function launches a coroutine that continuously fetches books from the repository at a
   * fixed interval defined by `REFRESH_TIME_DELAY`. It also initiates the computation of user
   * distances and combines the flows to filter books based on user preferences.
   */
  fun startUpdatingBooks() {
    fetchBooksFromRepositoryJob =
        scope.launch {
          while (true) {
            fetchBooksFromRepository()
            delay(REFRESH_TIME_DELAY)
            computeDistanceOfUsers()
            combineFlowsAndFilterBooks()
            delay(REFRESH_TIME_DELAY)
          }
        }
  }
  /** Stops updating books by canceling the coroutine scope. */
  fun stopUpdatingBooks() {
    fetchBooksFromRepositoryJob?.cancel()
    computeDistanceOfUsersJob?.cancel()
    combineFlowsAndFilterBooksJob?.cancel()
  }

  /**
   * Fetches books and users from the repository and updates the `_allBooks` and `_allUsers` state
   * flows.
   *
   * This function attempts to fetch books and users from their respective repositories. It retries
   * the fetch operation up to `MAXIMUM_RETRIES` times if it fails. If the fetch is successful, it
   * updates the `_allBooks` and `_allUsers` state flows with the retrieved data. If all retries
   * fail, it logs an error message.
   */
  private suspend fun fetchBooksFromRepository() {
    var successBooks = false
    var successUsers = false
    var currentAttempt = 0
    var queryUser = false
    var queryBooks = false
    while ((!successBooks || !successUsers) && currentAttempt < MAXIMUM_RETRIES) {
      if (!successUsers && !queryUser) {
        queryUser = true
        userRepository.getUsers { users ->
          if (users.isSuccess) {
            _allUsers.value = users.getOrThrow()
            successUsers = true
          } else {
            Log.e("BookManagerViewModel", "Failed to fetch users.")
          }
          queryUser = false
        }
      }
      if (!successBooks && !queryBooks) {
        queryBooks = true
        booksRepository.getBooks { result ->
          result.fold(
              {
                _allBooks.value = it
                Log.d("TAG_BMVM", "${_allBooks.value.size} | ${it.size}")
                successBooks = true
              },
              { Log.e("BookManagerViewModel", "Failed to fetch books: ${it.message}") })
          queryBooks = false
        }
      }

      if ((!successBooks || !successUsers) && !queryBooks && !queryUser) {
        currentAttempt++
        delay(RETRY_TIME_DELAY)
      }
      if (currentAttempt == MAXIMUM_RETRIES) {
        Log.e("BookManagerViewModel", "All retries failed.")
      }
    }
  }

  /**
   * Combines the flows of books, user distances, and book filters, and applies the filtering logic.
   *
   * This function launches a coroutine that combines the flows of all books, user distances, genres
   * filter, and languages filter. It maps the user distances to `UserBooksWithLocation` objects,
   * filters the books based on the selected genres and languages, and updates the `_filteredBooks`
   * and `_filteredUsers` state flows with the filtered results.
   */
  private fun combineFlowsAndFilterBooks() {
    android.util.Log.d(
        "TAG",
        "COMBINE FFS| ${_allUsers.value.size} ${_allBooks.value.size}  ||  ${_filteredUsers.value.size} ${_filteredBooks.value.size}")
    combineFlowsAndFilterBooksJob =
        scope.launch {
          combine(
                  _allBooks,
                  _allUserDistance,
                  bookFilter.genresFilter,
                  bookFilter.languagesFilter) {
                      books,
                      userDistance,
                      selectedGenres,
                      selectedLanguages ->
                    val userBooksWithLocation =
                        userDistance
                            .map { user ->
                              UserBooksWithLocation(
                                  userUUID = user.first.userUUID,
                                  longitude = user.first.longitude,
                                  latitude = user.first.latitude,
                                  books = books.filter { book -> book.uuid in user.first.bookList })
                            }
                            .filter { it.books.isNotEmpty() }

                    _filteredBooks.value = userBooksWithLocation.flatMap { it.books }
                    _filteredUsers.value = userBooksWithLocation
                  }
              .collect()
        }
  }
  /**
   * Computes the distance of users from the current location.
   *
   * This function combines the flows of all users, current latitude, and current longitude,
   * calculates the distance of each user from the current location using the provided
   * `computingDistanceMethod`, sorts the users by distance, and updates the `_allUserDistance`
   * state flow with the sorted results.
   */
  private fun computeDistanceOfUsers() {
    computeDistanceOfUsersJob =
        scope.launch {
          combine(_allUsers, geolocation.userLocation.filterNotNull()) { users, userLoc ->
                val userDistance =
                    users.map { user ->
                      user to
                          computingDistanceMethod(
                              userLoc.latitude, userLoc.longitude, user.latitude, user.longitude)
                    }
                userDistance.sortedBy { it.second }
              }
              .collect { sortedUserDistance -> _allUserDistance.value = sortedUserDistance }
        }
  }
}
