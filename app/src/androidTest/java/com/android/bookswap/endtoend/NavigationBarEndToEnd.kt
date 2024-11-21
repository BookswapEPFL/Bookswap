package com.android.bookswap.endtoend

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.bookswap.MainActivity
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.ui.navigation.Route
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationBarEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockBookRepository: BooksRepository
  private lateinit var mockUserRepository: UsersRepository
  private lateinit var mockPhotoStorage: PhotoFirebaseStorageSource

  @Before
  fun setUp() {
    mockPhotoStorage = mockk()
    mockBookRepository = mockk()
    every { mockBookRepository.getBook(any()) } just runs
    mockUserRepository = mockk()
    every { mockUserRepository.getUsers(any()) } just runs

    mockkConstructor(ContactViewModel::class)
    every { anyConstructed<ContactViewModel>().updateMessageBoxMap() } just runs

    composeTestRule.setContent {
      val db: FirebaseFirestore = mockk(relaxed = true)

      val messageRepository = MessageFirestoreSource(db)
      MainActivity()
          .BookSwapApp(
              messageRepository,
              mockBookRepository,
              mockUserRepository,
              Route.MAP,
              mockPhotoStorage)
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
}
