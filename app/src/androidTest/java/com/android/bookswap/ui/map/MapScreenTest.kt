package com.android.bookswap.ui.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.ui.navigation.NavigationActions
import java.util.UUID
import org.junit.Rule
import org.junit.Test

val books =
    listOf(
        DataBook(
            uuid = UUID(1000, 1000),
            title = "Book 1",
            author = "Author 1",
            description = "Description of Book 1",
            rating = 5,
            photo = "url_to_photo_1",
            language = BookLanguages.ENGLISH,
            isbn = "123-456-789",
            genres = listOf(BookGenres.FICTION, BookGenres.NONFICTION)),
        DataBook(
            uuid = UUID(2000, 1000),
            title = "Book 2",
            author = "Author 2",
            description = "Description of Book 2",
            rating = 4,
            photo = "url_to_photo_2",
            language = BookLanguages.FRENCH,
            isbn = "234-567-890",
            genres = listOf(BookGenres.FICTION)),
        DataBook(
            uuid = UUID(1000, 2000),
            title = "Book 3",
            author = "Author 3",
            description = "Description of Book 3",
            rating = 3,
            photo = "url_to_photo_3",
            language = BookLanguages.GERMAN,
            isbn = "345-678-901",
            genres = listOf(BookGenres.HORROR)))

class MapScreenTest {
  private val user =
      listOf(DataUser(bookList = listOf(UUID(1000, 1000), UUID(2000, 1000), UUID(1000, 2000))))

  private val userWithoutBooks = listOf(DataUser(bookList = emptyList()))
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, BookFilter(), MockBooksRepository(), 0)
    }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapBoxMarker").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapBoxMarkerList").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBox").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBoxTitle").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBoxAuthor").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListDivider").assertCountEquals(2)

    // components of Draggable Menu
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuStructure").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuHandle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuHandleDivider").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBox").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxImage").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxTitle").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxAuthor").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxRating").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxStar").assertCountEquals(12)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxEmptyStar").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxTag").assertCountEquals(3)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxDivider").assertCountEquals(3)
  }

  @Test
  fun noMarkerDisplayedForUserWithoutBooks() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(userWithoutBooks, navigationActions, BookFilter(), MockBooksRepository(), 0)
    }

    composeTestRule.onNodeWithTag("mapBoxMarker").assertIsNotDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBox").assertCountEquals(0) // No books
  }

  @Test
  fun noBookDisplayedInDraggableMenuForAllUsersWithNoBook() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(userWithoutBooks, navigationActions, BookFilter(), MockBooksRepository())
    }

    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBox").assertCountEquals(0) // No books
  }

  @Test
  fun emptyUserListDoesNotShowMarkers() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(listOf(), navigationActions, BookFilter(), MockBooksRepository())
    }

    // Assert that the map is displayed but no marker and info window is shown
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarker").assertCountEquals(0) // No marker info
  }

  @Test
  fun emptyUserListGiveEmptyDraggableMenu() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(userWithoutBooks, navigationActions, BookFilter(), MockBooksRepository())
    }

    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBox").assertCountEquals(0) // No books
  }

  @Test
  fun noUserSelectedInitially() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, BookFilter(), MockBooksRepository())
    }

    // Assert that no info window is displayed when no user is selected
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarker").assertCountEquals(0) // No info window
  }

  @Test
  fun draggableMenu_canBeDraggedVertically() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, BookFilter(), MockBooksRepository(), 0)
    }

    // Ensure the DraggableMenu is initially displayed
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()

    // Simulate a drag gesture by swiping up (closing the menu)
    composeTestRule.onNodeWithTag("mapDraggableMenu").performTouchInput {
      swipeUp(startY = bottom, endY = top, durationMillis = 500)
    }

    // Assert that after swiping, the menu is still displayed but in a new position
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()

    // Simulate dragging the menu back down (opening the menu)
    composeTestRule.onNodeWithTag("mapDraggableMenu").performTouchInput {
      swipe(start = Offset(0f, 100f), end = Offset(0f, -500f), durationMillis = 500)
    }

    // Assert that after swiping, the menu is still displayed but in a new position
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
  }

  @Test
  fun filterButtonIsDisplayed() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, BookFilter(), MockBooksRepository(), 0)
    }
    composeTestRule.onNodeWithTag("filterButton").assertIsDisplayed()
  }

  @Test
  fun bookChangedWhenFilterApplied() {
    val bookFilter = BookFilter()
    bookFilter.setGenres(listOf("Horror"))
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, bookFilter, MockBooksRepository(), 0)
    }
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBox").assertCountEquals(1)
  }

  class MockBooksRepository : BooksRepository {
    private var isBookAdded = false
    private var isBookFetched = false
    private var isBookUpdated = false
    private var isBookDeleted = false
    private var shouldFail = false

    override fun init(OnSucess: () -> Unit) {
      if (!shouldFail) {
        OnSucess()
      }
    }

    override fun getNewUid(): UUID {
      return UUID.randomUUID()
    }

    override fun getBook(OnSucess: (List<DataBook>) -> Unit, onFailure: (Exception) -> Unit) {
      if (!shouldFail) {
        isBookFetched = true
        OnSucess(books)
      } else {
        onFailure(Exception("Failed to fetch books"))
      }
    }

    override fun addBook(dataBook: DataBook, OnSucess: () -> Unit, onFailure: (Exception) -> Unit) {
      if (!shouldFail) {
        isBookAdded = true
        OnSucess()
      } else {
        onFailure(Exception("Failed to add book"))
      }
    }

    override fun updateBook(
        dataBook: DataBook,
        OnSucess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (!shouldFail) {
        isBookUpdated = true
        OnSucess()
      } else {
        onFailure(Exception("Failed to update book"))
      }
    }

    override fun deleteBooks(
        id: String,
        dataBook: DataBook,
        OnSucess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (!shouldFail) {
        isBookDeleted = true
        OnSucess()
      } else {
        onFailure(Exception("Failed to delete book"))
      }
    }
  }
}
