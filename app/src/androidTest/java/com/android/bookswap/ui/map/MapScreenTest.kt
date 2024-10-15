package com.android.bookswap.ui.map

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
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
  }

  @Test
  fun noMarkerDisplayedForUserWithoutBooks() {
    composeTestRule.setContent { MapScreen(userWithoutBooks, userWithoutBooks[0]) }

    // Assert that the marker info window is displayed, but without book entries
    composeTestRule.onNodeWithTag("mapBoxMarker").assertIsNotDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarkerListBox").assertCountEquals(0) // No books
  }

  @Test
  fun emptyUserListDoesNotShowMarkers() {
    composeTestRule.setContent { MapScreen(listOf(), null) }

    // Assert that the map is displayed but no marker and info window is shown
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarker").assertCountEquals(0) // No marker info
  }

  @Test
  fun noUserSelectedInitially() {
    composeTestRule.setContent { MapScreen(user, null) }

    // Assert that no info window is displayed when no user is selected
    composeTestRule.onNodeWithTag("mapGoogleMap").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("mapBoxMarker").assertCountEquals(0) // No info window
  }
}
