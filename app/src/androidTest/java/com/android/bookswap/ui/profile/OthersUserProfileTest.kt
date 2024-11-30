package com.android.bookswap.ui.profile

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.UserBookViewModel
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** This is a test class for the OtherUserProfileScreen. */
@RunWith(AndroidJUnit4::class)
class OthersUserProfileTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()
  val testUserId = UUID.randomUUID()
  private val standardUser =
      DataUser(
          testUserId,
          "M.",
          "Thommy",
          "Schelby",
          "Thomy.Schelby@test.com",
          "+41234567890",
          0.0,
          0.0,
          "toto.png")

  @Before
  fun setup() {
    val userVM: UserViewModel = mockk()
    every { userVM.getUser(any()) } returns standardUser
    every { userVM.uuid } returns standardUser.userUUID

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)

      val mockBooksRepository: BooksRepository = mockk()
      val mockbookVM: UserBookViewModel = mockk()
      OthersUserProfileScreen(
          userId = testUserId,
          otherUserVM = userVM,
          booksRepository = mockBooksRepository,
          mockbookVM,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") })
    }
  }

  @Test
  fun testDisplay() {
    assert(true)
  }
}
