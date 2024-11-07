package com.android.bookswap.model.map

import android.location.Location
import android.util.Log
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.ui.map.UserBooksWithLocation
import java.util.Timer
import kotlin.concurrent.schedule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

const val REFRESH_TIME_PERIOD = 5000L

class BookManager(
    private val geolocation: IGeolocation,
    private val booksRepository: BooksRepository,
    // TODO replace the listUser by a User repository to retrieve the users from the database
    listUser: List<DataUser>,
    private val bookFilter: BookFilter
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
  private val timer = Timer()

  init {
    timer.schedule(0, REFRESH_TIME_PERIOD) { fetchBooksFromRepository() }
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
                  val result = FloatArray(1)
                  Location.distanceBetween(
                      latitude, longitude, user.latitude, user.longitude, result)
                  user to result[0].toDouble()
                }
            userDistance.sortedBy { it.second }
          }
          .collect { sortedUserDistance -> _allUserDistance.value = sortedUserDistance }
    }
  }
}
