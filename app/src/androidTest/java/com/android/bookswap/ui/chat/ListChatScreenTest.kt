package com.android.bookswap.ui.chat

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.setContent { ListChatScreen() }
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.onNodeWithTag("messageScreenTitle").onChild().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("messageList").assertIsDisplayed()
    /* Need to put the navbar here */

    composeTestRule.onNodeWithTag("messageScreenTitle").onChild().assertTextEquals("Messages")
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.onNodeWithTag("profileIcon").assertHasClickAction()
  }

  @Test
  fun allMessageBoxesAreClickable() {
    val messageNodes = composeTestRule.onAllNodesWithTag("messageBox")
    assert(messageNodes.fetchSemanticsNodes().isNotEmpty())
    messageNodes.assertAll(hasClickAction())
  }
}
