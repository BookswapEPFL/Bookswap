package com.android.bookswap.ui.books.add

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.resources.C
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
    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_MANUALLY + C.Tag.NewBookChoice.btnWIcon.button)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_ISBN + C.Tag.NewBookChoice.btnWIcon.button)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_SCAN + C.Tag.NewBookChoice.btnWIcon.button)
        .assertIsDisplayed()
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookAdditionChoiceScreen(navigationActions)
    }
    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_MANUALLY + C.Tag.NewBookChoice.btnWIcon.button)
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_ISBN + C.Tag.NewBookChoice.btnWIcon.button)
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag(C.Screen.ADD_BOOK_SCAN + C.Tag.NewBookChoice.btnWIcon.button)
        .assertHasClickAction()
  }
}
