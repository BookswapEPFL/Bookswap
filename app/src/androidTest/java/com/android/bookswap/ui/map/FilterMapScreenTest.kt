package com.android.bookswap.ui.map

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.model.map.BookFilter
import com.android.bookswap.resources.C
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
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertTextEquals("Filters")

    composeTestRule.onNodeWithTag(C.Tag.MapFilter.apply).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.MapFilter.apply).assertHasClickAction()
    composeTestRule.onNodeWithTag(C.Tag.MapFilter.apply).assertTextEquals("Apply")
  }

  @Test
  fun testButtonBlock() {
    composeTestRule.setContent { ButtonBlock(listOf("test"), listOf()) {} }
    composeTestRule.onNodeWithTag("test" + C.Tag.MapFilter.filter).assertIsDisplayed()
    composeTestRule.onNodeWithTag("test" + C.Tag.MapFilter.filter).assertHasClickAction()
    composeTestRule.onNodeWithTag("test" + C.Tag.MapFilter.filter).assertTextEquals("test")
  }

  @Test
  fun testButtonBlockWithSelected() {
    val selected = mutableListOf("test1")

    composeTestRule.setContent {
      ButtonBlock(listOf("test1", "test2", "test3", "test4"), selected) { newSelection ->
        selected.clear()
        selected.addAll(newSelection)
      }
    }

    composeTestRule
        .onNodeWithTag("test1" + C.Tag.MapFilter.filter)
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()

    assert(!selected.contains("test1"))

    composeTestRule
        .onNodeWithTag("test2" + C.Tag.MapFilter.filter)
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()
    assert(selected.contains("test2"))
  }
}
