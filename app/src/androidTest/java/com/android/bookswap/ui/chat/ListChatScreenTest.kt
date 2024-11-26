package com.android.bookswap.ui.chat

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageBox
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.navigation.BottomNavigationMenu
import com.android.bookswap.ui.navigation.List_Navigation_Bar_Destinations
import com.android.bookswap.ui.navigation.NavigationActions
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var placeHolderData: List<MessageBox>
  private lateinit var placeHolderDataEmpty: List<MessageBox>

  @Before
  fun setUp() {
    placeHolderData =
        List(12) {
          MessageBox(
              DataUser(
                  UUID.randomUUID(),
                  "Hello",
                  "First ${it + 1}",
                  "Last ${it + 1}",
                  "",
                  "",
                  0.0,
                  0.0,
                  "",
                  emptyList(),
                  "googleUid"),
              "Test message $it test for the feature of ellipsis in the message",
              "01.01.24")
        }
    placeHolderDataEmpty = emptyList()
  }

  @Test
  fun hasRequiredComponentsWithMessage() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          placeHolderData,
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                { destination -> navigationActions.navigateTo(destination) },
                List_Navigation_Bar_Destinations,
                navigationActions.currentRoute())
          })
    }
    composeTestRule.onNodeWithTag(C.Tag.top_app_bar_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.profile_button).assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.ChatList.scrollable).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.bottom_navigation_menu_container).assertIsDisplayed()

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertTextEquals("Messages")
  }

  @Test
  fun hasRequiredComponentsWithMessageEmpty() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          placeHolderDataEmpty,
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                { destination -> navigationActions.navigateTo(destination) },
                List_Navigation_Bar_Destinations,
                navigationActions.currentRoute())
          })
    }
    composeTestRule.onNodeWithTag(C.Tag.top_app_bar_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.profile_button).assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.ChatList.scrollable).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.bottom_navigation_menu_container).assertIsDisplayed()

    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertTextEquals("Messages")
    composeTestRule
        .onNodeWithTag(C.Tag.ChatList.scrollable)
        .onChild()
        .assertTextEquals("No messages yet")
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          placeHolderData,
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                { destination -> navigationActions.navigateTo(destination) },
                List_Navigation_Bar_Destinations,
                navigationActions.currentRoute())
          })
    }
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.profile_button).assertHasClickAction()
  }

  @Test
  fun allMessageBoxesAreClickable() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(
          placeHolderData,
          navigationActions,
          { TopAppBarComponent(Modifier, navigationActions, "Messages") },
          {
            BottomNavigationMenu(
                { destination -> navigationActions.navigateTo(destination) },
                List_Navigation_Bar_Destinations,
                navigationActions.currentRoute())
          })
    }

    val messageNodes = composeTestRule.onAllNodesWithTag(C.Tag.ChatList.item)
    assert(messageNodes.fetchSemanticsNodes().isNotEmpty())
    messageNodes.assertAll(hasClickAction())
  }
}
