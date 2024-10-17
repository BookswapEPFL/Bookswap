package com.android.bookswap.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
    composeTestRule.setContent {
      BottomNavigationMenu(onTabSelect = {}, tabList = mockTabs, selectedItem = Route.CHAT)
    }

    // Check if the bottom navigation is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun bottomNavigation_hasCorrectNumberOfTabs() {
    composeTestRule.setContent {
      BottomNavigationMenu(onTabSelect = {}, tabList = mockTabs, selectedItem = Route.CHAT)
    }

    // Check if the correct number of tabs is displayed
    mockTabs.forEach { tab -> composeTestRule.onNodeWithTag(tab.textId).assertExists() }
  }

  @Test
  fun bottomNavigation_selectedItemIsHighlighted() {
    composeTestRule.setContent {
      BottomNavigationMenu(onTabSelect = {}, tabList = mockTabs, selectedItem = Route.MAP)
    }

    // Verify that the selected item is 'Map'
    composeTestRule.onNodeWithTag("Map").assertIsSelected()
  }

  @Test
  fun bottomNavigation_onTabClickInvokesCallback() {
    var selectedTab: TopLevelDestination? = null

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it }, tabList = mockTabs, selectedItem = Route.CHAT)
    }

    // Perform a click on the "New Book" tab
    composeTestRule.onNodeWithTag("New Book").performClick()

    // Check if the callback was triggered with the correct tab
    assert(selectedTab?.route == Route.NEWBOOK)
  }
}
