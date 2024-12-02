package com.android.bookswap.ui.map

import android.Manifest
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
import androidx.test.rule.GrantPermissionRule
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.UserBooksWithLocation
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.map.BookManagerViewModel
import com.android.bookswap.model.map.DefaultGeolocation
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.google.maps.android.compose.CameraPositionState
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.UUID
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

val userId = UUID.randomUUID()
val longListBook =
    List(20) {
      DataBook(
          UUID(2000, 2000),
          "Book 1",
          "Author 1",
          "Description of Book 1",
          5,
          "url_to_photo_1",
          BookLanguages.ENGLISH,
          "123-456-789",
          listOf(BookGenres.FICTION, BookGenres.NONFICTION),
          userId)
    }

val books =
    listOf(
        DataBook(
            UUID(1000, 1000),
            "Book 1",
            "Author 1",
            "Description of Book 1",
            5,
            "url_to_photo_1",
            BookLanguages.ENGLISH,
            "123-456-789",
            listOf(BookGenres.FICTION, BookGenres.HORROR),
            userId),
        DataBook(
            UUID(2000, 1000),
            "Book 2",
            "Author 2",
            "Description of Book 2",
            4,
            "url_to_photo_2",
            BookLanguages.FRENCH,
            "234-567-890",
            listOf(BookGenres.FICTION),
            userId))

class MapScreenTest {
  private val user = listOf(DataUser(bookList = listOf(UUID(1000, 1000), UUID(2000, 1000))))
  private val userLongList = listOf(DataUser(bookList = listOf(UUID(2000, 2000))))

  private val userBooksWithLocationList =
      listOf(UserBooksWithLocation(UUID.randomUUID(), user[0].longitude, user[0].latitude, books))
  private val userBooksWithLocationLongList =
      listOf(
          UserBooksWithLocation(
              UUID.randomUUID(), userLongList[0].longitude, userLongList[0].latitude, longListBook))

  private val userWithoutBooks =
      listOf(UserBooksWithLocation(UUID.randomUUID(), 0.0, 0.0, emptyList()))
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_BACKGROUND_LOCATION)

  private val mockBookManagerViewModel: BookManagerViewModel = mockk()
  private val mockUserVM: UserViewModel = mockk()

  @Before
  fun setup() {
    every { mockBookManagerViewModel.filteredBooks } returns MutableStateFlow(books)

    every { mockBookManagerViewModel.filteredUsers } returns
        MutableStateFlow(userBooksWithLocationList)
    every { mockBookManagerViewModel.startUpdatingBooks() } just runs
    every { mockBookManagerViewModel.stopUpdatingBooks() } just runs
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, 0, userVM = mockUserVM)
    }
    composeTestRule.onNodeWithTag(C.Tag.map_screen_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.Map.google_map).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.Map.Marker.info_window_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.Map.Marker.info_window_scrollable).assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag(C.Tag.Map.Marker.info_window_book_container)
        .assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.Map.Marker.book_title).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.Map.Marker.book_author).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.Map.Marker.info_window_divider).assertCountEquals(1)

    // components of Draggable Menu
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_layout).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_handle).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_handle_divider).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("1_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()
    composeTestRule.onAllNodesWithTag(C.Tag.BookDisplayComp.image).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.BookDisplayComp.title).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.BookDisplayComp.author).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.BookDisplayComp.rating).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.BookDisplayComp.filled_star).assertCountEquals(9)
    composeTestRule.onAllNodesWithTag(C.Tag.BookDisplayComp.hollow_star).assertCountEquals(1)
    composeTestRule.onAllNodesWithTag(C.Tag.BookDisplayComp.genres).assertCountEquals(2)
    composeTestRule.onAllNodesWithTag(C.Tag.BookListComp.divider).assertCountEquals(books.size - 1)

    composeTestRule.onNodeWithTag(C.Tag.Map.filter_button).assertIsDisplayed()
  }

  @Test
  fun noMarkerDisplayedForUserWithoutBooks() {
    every { mockBookManagerViewModel.filteredBooks } answers { MutableStateFlow(emptyList()) }
    every { mockBookManagerViewModel.filteredUsers } answers { MutableStateFlow(userWithoutBooks) }
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, 0, userVM = mockUserVM)
    }

    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag(C.Tag.Map.Marker.info_window_container).assertIsNotDisplayed()
    composeTestRule
        .onAllNodesWithTag(C.Tag.Map.Marker.info_window_book_container)
        .assertCountEquals(0) // No books
  }

  @Test
  fun emptyUserListDoesNotShowMarkers() {
    every { mockBookManagerViewModel.filteredBooks } answers { MutableStateFlow(emptyList()) }
    every { mockBookManagerViewModel.filteredUsers } answers { MutableStateFlow(emptyList()) }
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, userVM = mockUserVM)
    }

    // Assert that the map is displayed but no marker and info window is shown
    composeTestRule.onNodeWithTag(C.Tag.Map.google_map).assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag(C.Tag.Map.Marker.info_window_container)
        .assertCountEquals(0) // No marker info
  }

  @Test
  fun emptyBooksListGiveEmptyDraggableMenu() {
    every { mockBookManagerViewModel.filteredBooks } answers { MutableStateFlow(emptyList()) }
    every { mockBookManagerViewModel.filteredUsers } answers { MutableStateFlow(emptyList()) }
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, userVM = mockUserVM)
    }
    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_container).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.BookListComp.empty_list_text)
        .assertIsDisplayed()
        .assertTextContains("No books to display")
  }

  @Test
  fun noUserSelectedInitially() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, userVM = mockUserVM)
    }

    // Assert that no info window is displayed when no user is selected
    composeTestRule.onNodeWithTag(C.Tag.Map.google_map).assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag(C.Tag.Map.Marker.info_window_container)
        .assertCountEquals(0) // No info window
  }

  @Test
  fun draggableMenu_canBeDraggedVertically() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, 0, userVM = mockUserVM)
    }
    // Ensure the DraggableMenu is initially displayed
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_container).assertIsDisplayed()

    // Simulate a drag gesture by swiping up (closing the menu)
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_container).performTouchInput {
      swipeUp(startY = bottom, endY = top, durationMillis = 500)
    }

    // Assert that after swiping, the menu is still displayed but in a new position
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_container).assertIsDisplayed()

    // Simulate dragging the menu back down (opening the menu)
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_container).performTouchInput {
      swipe(start = Offset(0f, 100f), end = Offset(0f, -500f), durationMillis = 500)
    }

    // Assert that after swiping, the menu is still displayed but in a new position
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_container).assertIsDisplayed()
  }

  @Test
  fun draggableMenuListIsScrollable() {

    every { mockBookManagerViewModel.filteredBooks } answers { MutableStateFlow(longListBook) }
    every { mockBookManagerViewModel.filteredUsers } answers
        {
          MutableStateFlow(userBooksWithLocationLongList)
        }
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, 0, userVM = mockUserVM)
    }

    // Assert initial state: Only first item(s) are visible
    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("19_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsNotDisplayed()
    // Perform scroll gesture on LazyColumn
    composeTestRule.onNodeWithTag(C.Tag.Map.bottom_drawer_layout).performTouchInput {
      for (i in 1..19) {
        swipeUp()
      }
    }
    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag("19_" + C.Tag.BookDisplayComp.book_display_container)
        .assertIsDisplayed()
  }

  @Test
  fun mapHasGeoLocation() {
    val geolocation = DefaultGeolocation()
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MapScreen(mockBookManagerViewModel, navigationActions, 0, userVM = mockUserVM)
    }
    val node1 = composeTestRule.onNodeWithTag(C.Tag.Map.google_map).fetchSemanticsNode()
    val cameraPositionState: CameraPositionState? = node1.config.getOrNull(CameraPositionKey)

    assertEquals(geolocation.userLocation.value.latitude, cameraPositionState?.position?.target?.latitude)
    assertEquals(geolocation.userLocation.value.longitude, cameraPositionState?.position?.target?.longitude)
  }
}
