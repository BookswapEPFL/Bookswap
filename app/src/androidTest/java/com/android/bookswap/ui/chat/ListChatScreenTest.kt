package com.android.bookswap.ui.chat

import androidx.compose.runtime.Composable
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
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.model.chat.ChatViewModel
import com.android.bookswap.model.chat.MessageBox
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var placeHolderData: List<MessageBox>
  private lateinit var viewModel: ChatViewModel

  @Before
  fun setUp() {
    placeHolderData =
        List(12) {
          MessageBox(
              "Contact ${it + 1}",
              "Test message $it test for the feature of ellipsis in the message",
              "01.01.24")
        }
    viewModel = ChatViewModel(MessageFirestoreSource(Firebase.firestore))
  }

  @Composable
  @Test
  fun hasRequiredComponentsWithMessage() {
    val navController = rememberNavController()
    composeTestRule.setContent {
      ListChatScreen(
          navController = navController, placeHolderData = placeHolderData, viewModel = viewModel)
    }
    composeTestRule.onNodeWithTag("chat_messageScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileIconButton").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chat_messageList").assertIsDisplayed()
    /* Need to put the navbar here */

    composeTestRule.onNodeWithTag("chat_messageScreenTitle").onChild().assertTextEquals("Messages")
  }

  @Composable
  @Test
  fun hasRequiredComponentsWithMessageEmpty() {
    val navController = rememberNavController()
    composeTestRule.setContent {
      ListChatScreen(
          navController = navController, placeHolderData = emptyList(), viewModel = viewModel)
    }
    composeTestRule.onNodeWithTag("chat_messageScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileIconButton").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chat_messageList").assertIsDisplayed()

    /* Need to put the navbar here */

    composeTestRule.onNodeWithTag("chat_messageScreenTitle").onChild().assertTextEquals("Messages")
    composeTestRule.onNodeWithTag("chat_messageList").onChild().assertTextEquals("No messages yet")
  }

  @Composable
  @Test
  fun hasClickableComponents() {
    val navController = rememberNavController()
    composeTestRule.setContent {
      ListChatScreen(
          navController = navController, placeHolderData = placeHolderData, viewModel = viewModel)
    }
    composeTestRule.onNodeWithTag("profileIconButton").assertHasClickAction()
  }

  @Composable
  @Test
  fun allMessageBoxesAreClickable() {
    val navController = rememberNavController()
    composeTestRule.setContent {
      ListChatScreen(
          navController = navController, placeHolderData = placeHolderData, viewModel = viewModel)
    }
    val messageNodes = composeTestRule.onAllNodesWithTag("chat_messageBox")
    assert(messageNodes.fetchSemanticsNodes().isNotEmpty())
    messageNodes.assertAll(hasClickAction())
  }

  @Composable
  @Test
  fun testNavigationFromListChatScreenToChatScreen() {
    val navController = rememberNavController()
    composeTestRule.setContent {
      ListChatScreen(
          navController = navController, placeHolderData = placeHolderData, viewModel = viewModel)
    }

    // Perform click on the message box
    composeTestRule.onNodeWithTag("chat_messageBox").performClick()

    // Verify that the navigation to ChatScreen occurred
    composeTestRule.onNodeWithTag("chatScreen").assertExists()
  }

  @Composable
  @Test
  fun testEmptyMessageList() {
    val navController = rememberNavController()
    composeTestRule.setContent {
      ListChatScreen(
          navController = navController, placeHolderData = emptyList(), viewModel = viewModel)
    }

    // Verify that the "No messages yet" text is displayed
    composeTestRule.onNodeWithTag("chat_messageList").assertTextEquals("No messages yet")
  }

  @Composable
  @Test
  fun testMessageContent() {
    val navController = rememberNavController()
    composeTestRule.setContent {
      ListChatScreen(
          navController = navController, placeHolderData = placeHolderData, viewModel = viewModel)
    }

    // Verify that the message content is displayed correctly
    composeTestRule.onNodeWithTag("chat_messageContactName").assertTextEquals("Contact 1")
    composeTestRule.onNodeWithTag("chat_messageDate").assertTextEquals("01.01.24")
    composeTestRule
        .onNodeWithTag("chat_messageContent")
        .assertTextEquals("Test message 0 test for the feature of ellipsis in the message")
  }
}
