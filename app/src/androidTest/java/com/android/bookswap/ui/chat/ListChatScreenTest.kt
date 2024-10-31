package com.android.bookswap.ui.chat

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
import com.android.bookswap.model.chat.MessageBox
import com.android.bookswap.ui.navigation.NavigationActions
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
              "Contact ${it + 1}",
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
      ListChatScreen(placeHolderData, navigationActions)
    }
    composeTestRule.onNodeWithTag("chat_messageScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileIconButton").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chat_messageList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("chat_messageScreenTitle").onChild().assertTextEquals("Messages")
  }

  @Test
  fun hasRequiredComponentsWithMessageEmpty() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(placeHolderDataEmpty, navigationActions)
    }
    composeTestRule.onNodeWithTag("chat_messageScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileIconButton").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chat_messageList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    composeTestRule.onNodeWithTag("chat_messageScreenTitle").onChild().assertTextEquals("Messages")
    composeTestRule.onNodeWithTag("chat_messageList").onChild().assertTextEquals("No messages yet")
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(placeHolderData, navigationActions)
    }
    composeTestRule.onNodeWithTag("profileIconButton").assertHasClickAction()
  }

  @Test
  fun allMessageBoxesAreClickable() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      ListChatScreen(placeHolderData, navigationActions)
    }
    val messageNodes = composeTestRule.onAllNodesWithTag("chat_messageBox")
    assert(messageNodes.fetchSemanticsNodes().isNotEmpty())
    messageNodes.assertAll(hasClickAction())
  }
}
