package com.android.bookswap.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.resources.C
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavigationMenuTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocked Top Level Destinations from your updated structure
  private val mockTabs =
      listOf(TopLevelDestinations.CHAT, TopLevelDestinations.NEW_BOOK, TopLevelDestinations.MAP)

  @Test
  fun bottomNavigation_isDisplayed() {
    composeTestRule.setContent { BottomNavigationMenu({}, mockTabs, C.Route.CHAT_LIST) }

    // Check if the bottom navigation is displayed
    composeTestRule.onNodeWithTag(C.Tag.bottom_navigation_menu_container).assertIsDisplayed()
  }

  @Test
  fun bottomNavigation_hasCorrectNumberOfTabs() {
    composeTestRule.setContent { BottomNavigationMenu({}, mockTabs, C.Route.CHAT_LIST) }

    // Check if the correct number of tabs is displayed
    mockTabs.forEach { tab ->
      composeTestRule.onNodeWithTag(tab.route + C.Tag.BottomNavMenu.nav_item).assertExists()
    }
  }

  @Test
  fun bottomNavigation_selectedItemIsHighlighted() {
    composeTestRule.setContent { BottomNavigationMenu({}, mockTabs, C.Route.MAP) }

    // Verify that the selected item is 'Map'
    composeTestRule.onNodeWithTag(C.Route.MAP + C.Tag.BottomNavMenu.nav_item).assertIsSelected()
  }

  @Test
  fun bottomNavigation_onTabClickInvokesCallback() {
    var selectedTab: TopLevelDestination? = null

    composeTestRule.setContent {
      BottomNavigationMenu({ selectedTab = it }, mockTabs, C.Route.CHAT_LIST)
    }

    // Perform a click on the "New Book" tab
    composeTestRule.onNodeWithTag(C.Route.NEW_BOOK + C.Tag.BottomNavMenu.nav_item).performClick()

    // Check if the callback was triggered with the correct tab
    assert(selectedTab?.route == C.Route.NEW_BOOK)
  }
}
