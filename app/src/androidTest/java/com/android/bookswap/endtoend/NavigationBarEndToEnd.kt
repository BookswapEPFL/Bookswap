package com.android.bookswap.endtoend

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.bookswap.MainActivity
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.ui.navigation.Route
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationBarEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    composeTestRule.setContent {
      val db = FirebaseFirestore.getInstance()

      val messageRepository = MessageFirestoreSource(db)
      MainActivity().BookSwapApp(messageRepository, MockBooksRepository(), Route.MAP)
    }
  }

  @Test
  fun testNavigationBar() {
    // Click on the Add Book tab and check if the AddToBookScreen is displayed
    composeTestRule.onNodeWithTag("New Book").assertExists().performClick()
    composeTestRule.onNodeWithTag("addBookChoiceScreen").assertExists()

    // Click on the Chat tab and check if the ListChatScreen is displayed
    composeTestRule.onNodeWithTag("Chat").assertExists().performClick()
    composeTestRule.onNodeWithTag("chat_listScreen").assertExists()

    // Click on the Map tab and check if the MapScreen is displayed
    composeTestRule.onNodeWithTag("Map").assertExists().performClick()
    composeTestRule.onNodeWithTag("mapScreen").assertExists()
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
        OnSucess(emptyList())
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
