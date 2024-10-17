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
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import java.util.UUID
import org.junit.Rule
import org.junit.Test

class MapScreenTest {
  private val user =
      listOf(
          TempUser(
              latitude = 0.0,
              longitude = 0.0,
              listBook =
                  listOf(
                      DataBook(
                          uuid = UUID.randomUUID(),
                          title = "Book 1",
                          author = "Author 1",
                          description = "Description of Book 1",
                          rating = 5,
                          photo = "url_to_photo_1",
                          language = BookLanguages.ENGLISH,
                          isbn = "123-456-789"),
                      DataBook(
                          uuid = UUID.randomUUID(),
                          title = "Book 2",
                          author = "Author 2",
                          description = "Description of Book 2",
                          rating = 4,
                          photo = "url_to_photo_2",
                          language = BookLanguages.FRENCH,
                          isbn = "234-567-890"),
                      DataBook(
                          uuid = UUID.randomUUID(),
                          title = "Book 3",
                          author = "Author 3",
                          description = "Description of Book 3",
                          rating = 3,
                          photo = "url_to_photo_3",
                          language = BookLanguages.GERMAN,
                          isbn = "345-678-901"))))

  private val userWithoutBooks =
      listOf(TempUser(latitude = 1.0, longitude = 1.0, listBook = emptyList()))
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { MapScreen(user, user[0]) }
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
    composeTestRule.setContent { MapScreen(userWithoutBooks, userWithoutBooks[0]) }

    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag("mapBoxMarker").assertIsNotDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBox").assertCountEquals(0) // No books
  }

  @Test
  fun noBookDisplayedInDraggableMenuForAllUsersWithNoBook() {
    composeTestRule.setContent { MapScreen(userWithoutBooks) }

    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBox").assertCountEquals(0) // No books
  }

  @Test
  fun emptyUserListDoesNotShowMarkers() {
    composeTestRule.setContent { MapScreen(listOf(), null) }

    // Assert that the map is displayed but no marker and info window is shown
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarker").assertCountEquals(0) // No marker info
  }

  @Test
  fun emptyUserListGiveEmptyDraggableMenu() {
    composeTestRule.setContent { MapScreen(userWithoutBooks) }

    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag("mapDraggableMenu").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapDraggableMenuBookBox").assertCountEquals(0) // No books
  }

  @Test
  fun noUserSelectedInitially() {
    composeTestRule.setContent { MapScreen(user, null) }

    // Assert that no info window is displayed when no user is selected
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarker").assertCountEquals(0) // No info window
  }

  @Test
  fun draggableMenu_canBeDraggedVertically() {
    composeTestRule.setContent { MapScreen(user, user[0]) }

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
}
