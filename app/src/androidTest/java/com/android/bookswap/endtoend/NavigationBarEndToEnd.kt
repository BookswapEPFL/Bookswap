package com.android.bookswap.endtoend

import android.Manifest
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import com.android.bookswap.MainActivity
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.model.chat.OfflineMessageStorage
import com.android.bookswap.resources.C
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class NavigationBarEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val grantPermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_BACKGROUND_LOCATION)

  private lateinit var mockBookRepository: BooksRepository
  private lateinit var mockUserRepository: UsersRepository
  private lateinit var mockPhotoStorage: PhotoFirebaseStorageSource
  private lateinit var mockMessageStorage: OfflineMessageStorage
  private lateinit var mockContext: Context
  private lateinit var userVM: UserViewModel
  
  private val standardUser =
	DataUser(
	  UUID.randomUUID(),
	  "M.",
	  "John",
	  "Doe",
	  "John.Doe@example.com",
	  "+41223456789",
	  0.0,
	  0.0,
	  "dummyPic.png")

  @Before
  fun setUp() {
    mockPhotoStorage = mockk()
    mockMessageStorage = mockk()
    mockContext = mockk()
	
	mockBookRepository = mockk()
    every { mockBookRepository.getBook(any()) } just runs
	
    mockUserRepository = mockk()
    every { mockUserRepository.getUsers(any()) } just runs

    mockkConstructor(ContactViewModel::class)
    every { anyConstructed<ContactViewModel>().updateMessageBoxMap() } just runs
	
	userVM = mockk(relaxed = true)
	every { userVM.getUser(any()) } returns standardUser
	every { userVM.uuid } returns standardUser.userUUID
	every { userVM.updateAddress(any<Double>(), any<Double>(), any<Context>()) } just runs

    composeTestRule.setContent {
      val db: FirebaseFirestore = mockk(relaxed = true)

      val messageRepository = MessageFirestoreSource(db)
      MainActivity()
          .BookSwapApp(
              messageRepository,
              mockBookRepository,
              mockUserRepository,
              C.Route.MAP,
              mockPhotoStorage,
              mockMessageStorage,
              context = mockContext)
    }
  }

  @Test
  fun testNavigationBar() {
    // Click on the Add Book tab and check if the AddToBookScreen is displayed
    composeTestRule
        .onNodeWithTag(C.Route.NEW_BOOK + C.Tag.BottomNavMenu.nav_item)
        .assertExists()
        .performClick()
    composeTestRule.onNodeWithTag(C.Tag.new_book_choice_screen_container).assertExists()

    // Click on the Chat tab and check if the ListChatScreen is displayed
    composeTestRule
        .onNodeWithTag(C.Route.CHAT_LIST + C.Tag.BottomNavMenu.nav_item)
        .assertExists()
        .performClick()
    composeTestRule.onNodeWithTag(C.Tag.chat_list_screen_container).assertExists()

    // Click on the Map tab and check if the MapScreen is displayed
    composeTestRule
        .onNodeWithTag(C.Route.MAP + C.Tag.BottomNavMenu.nav_item)
        .assertExists()
        .performClick()
    composeTestRule.onNodeWithTag(C.Tag.map_screen_container).assertExists()
  }
}
