package com.android.bookswap.ui.bookAddition

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.ui.books.add.BookAdditionChoiceScreen
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookAdditionChoiceScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockNavigationActions: NavigationActions

  @Before
  fun setUp() {
    mockNavigationActions = mockk()
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookAdditionChoiceScreen(navigationActions)
    }
    composeTestRule.onNodeWithTag("button_Manually").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_From ISBN").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_From Photo").assertIsDisplayed()
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookAdditionChoiceScreen(navigationActions)
    }
    composeTestRule.onNodeWithTag("button_Manually").assertHasClickAction()
    composeTestRule.onNodeWithTag("button_From ISBN").assertHasClickAction()
    composeTestRule.onNodeWithTag("button_From Photo").assertHasClickAction()
  }
}
