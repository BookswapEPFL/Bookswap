package com.android.bookswap.ui.map

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class FilterMapScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun hasRequiredComponentsScreen() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      FilterMapScreen(navigationActions, BookFilter())
    }
    composeTestRule.onNodeWithTag("filter_filterScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_filterScreenTitle").assertTextEquals("Filters")

    composeTestRule.onNodeWithTag("filter_applyButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_applyButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("filter_applyButton").assertTextEquals("Apply")
  }

  @Test
  fun testButtonBlock() {
    composeTestRule.setContent { ButtonBlock(listOf("test"), listOf()) {} }
    composeTestRule.onNodeWithTag("filter_buttonFilter_test").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_buttonFilter_test").assertHasClickAction()
    composeTestRule.onNodeWithTag("filter_buttonFilter_test").assertTextEquals("test")
  }
}
