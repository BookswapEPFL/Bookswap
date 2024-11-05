package com.android.bookswap.ui.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
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
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.model.map.DefaultGeolocation
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.every
import io.mockk.mockk
import com.google.maps.android.compose.CameraPositionState
import java.util.UUID
import org.junit.Before
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

val longListBook =
    List(20) {
      DataBook(
          uuid = UUID(2000, 2000),
          title = "Book 1",
          author = "Author 1",
          description = "Description of Book 1",
          rating = 5,
          photo = "url_to_photo_1",
          language = BookLanguages.ENGLISH,
          isbn = "123-456-789",
          genres = listOf(BookGenres.FICTION, BookGenres.NONFICTION))
    }

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
            genres = listOf(BookGenres.FICTION, BookGenres.HORROR)),
        DataBook(
            uuid = UUID(2000, 1000),
            title = "Book 2",
            author = "Author 2",
            description = "Description of Book 2",
            rating = 4,
            photo = "url_to_photo_2",
            language = BookLanguages.FRENCH,
            isbn = "234-567-890",
            genres = listOf(BookGenres.FICTION))) + longListBook

class MapScreenTest {
  private val user = listOf(DataUser(bookList = listOf(UUID(1000, 1000), UUID(2000, 1000))))
  private val userLongListOnly = listOf(DataUser(bookList = listOf(UUID(2000, 2000))))

  private val userWithoutBooks = listOf(DataUser(bookList = emptyList()))
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockBookRepository: BooksFirestoreRepository

  @Before
  fun setup() {
    mockBookRepository = mockk()

    every { mockBookRepository.getBook(any(), any()) } answers
        {
          firstArg<(List<DataBook>) -> Unit>().invoke(books)
        }
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, BookFilter(), mockBookRepository, 0)
    }
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapBoxMarker").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapBoxMarkerList").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBox").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBoxTitle").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBoxAuthor").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListDivider").assertCountEquals(1)

    // components of Draggable Menu
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuStructure").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuHandle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuHandleDivider").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox1").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxImage").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxTitle").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxAuthor").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxRating").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxStar").assertCountEquals(9)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxEmptyStar").assertCountEquals(1)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxTag").assertCountEquals(2)
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBoxDivider").assertCountEquals(2)
  }

  @Test
  fun noMarkerDisplayedForUserWithoutBooks() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(userWithoutBooks, navigationActions, BookFilter(), mockBookRepository, 0)
    }

    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag("mapBoxMarker").assertIsNotDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBox").assertCountEquals(0) // No books
  }

  @Test
  fun noBookDisplayedInDraggableMenuForAllUsersWithNoBook() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(userWithoutBooks, navigationActions, BookFilter(), mockBookRepository)
    }

    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox0").assertIsNotDisplayed() // No books
  }

  @Test
  fun emptyUserListDoesNotShowMarkers() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(listOf(), navigationActions, BookFilter(), mockBookRepository)
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
      MapScreen(userWithoutBooks, navigationActions, BookFilter(), mockBookRepository)
    }

    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox0").assertIsNotDisplayed() // No books
  }

  @Test
  fun noUserSelectedInitially() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, BookFilter(), mockBookRepository)
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
      MapScreen(user, navigationActions, BookFilter(), mockBookRepository, 0)
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
      MapScreen(user, navigationActions, BookFilter(), mockBookRepository, 0)
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
      MapScreen(user, navigationActions, bookFilter, mockBookRepository, 0)
    }
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox1").assertIsNotDisplayed()
  }

  @Test
  fun draggableMenuListIsScrollable() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      // Passing multiple books to ensure list needs scrolling
      MapScreen(userLongListOnly, navigationActions, BookFilter(), mockBookRepository, 0)
    }

    // Assert initial state: Only first item(s) are visible
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox19").assertIsNotDisplayed()
    // Perform scroll gesture on LazyColumn
    composeTestRule.onNodeWithTag("mapDraggableMenuStructure").performTouchInput {
      for (i in 1..19) {
        swipeUp()
      }
    }
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox0").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox19").assertIsDisplayed()
  }

  @Test
  fun listMarkerBookChangedWhenFilterApplied() {
    val bookFilter = BookFilter()
    bookFilter.setGenres(listOf("Horror"))
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, bookFilter, mockBookRepository, 0)
    }
    composeTestRule.onNodeWithTag("mapBoxMarkerListBox").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBox").assertCountEquals(1)
    composeTestRule.onNodeWithTag("mapBoxMarkerListBoxTitle").assertTextContains("Book 1")
  }

  @Test
  fun noBooksTextDisplayedWhenNoBooksFound() {
    val bookFilter = BookFilter()
    bookFilter.setGenres(listOf("Dystopian"))
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(user, navigationActions, bookFilter, mockBookRepository, 0)
    }
    composeTestRule
        .onNodeWithTag("mapDraggableMenuNoBook")
        .assertIsDisplayed()
        .assertTextContains("No books found")
    composeTestRule.onNodeWithTag("mapDraggableMenuBookBox").assertIsNotDisplayed()
  }

  @Test
  fun mapHasGeoLocation() {
    val geolocation = DefaultGeolocation()
      composeTestRule.setContent {
          val navController = rememberNavController()
          val navigationActions = NavigationActions(navController)
          MapScreen(user, navigationActions, BookFilter(), mockBookRepository, 0)
      }
    val node1 = composeTestRule.onNodeWithTag("mapGoogleMap").fetchSemanticsNode()
    val cameraPositionState: CameraPositionState? = node1.config.getOrNull(CameraPositionKey)

    assertEquals(geolocation.latitude.value, cameraPositionState?.position?.target?.latitude)
    assertEquals(geolocation.longitude.value, cameraPositionState?.position?.target?.longitude)
  }
}
