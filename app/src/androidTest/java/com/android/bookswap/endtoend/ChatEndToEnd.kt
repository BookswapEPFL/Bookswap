package com.android.bookswap.endtoend

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.moveBy
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.up
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageBox
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.ui.chat.ChatScreen
import com.android.bookswap.ui.chat.ListChatScreen
import com.android.bookswap.ui.chat.imageTestMessageUUID
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.TopLevelDestination
import com.google.firebase.firestore.ListenerRegistration
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * End-to-end test class for chat functionality.
 *
 * This class contains tests that verify the navigation and message manipulation within the chat
 * feature of the application.
 */
class ChatEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockMessageRepository: MockMessageRepository
  private val navigateToChatScreen = mutableStateOf(false)
  private val currentUserUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002") // John Doe
  private val otherUserUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001") // Other user

  @Before
  fun setup() {
    // Initialize the mock message repository with placeholder messages
    mockMessageRepository = MockMessageRepository()

    // Initialize mock navigation actions
    mockNavigationActions = mockk(relaxed = true)

    // Mock the `navigateTo(destination: TopLevelDestination)` method
    every { mockNavigationActions.navigateTo(any<TopLevelDestination>()) } answers
        {
          // Update the navigation state to switch screens
          navigateToChatScreen.value = true
        }

    // Mock the `navigateTo(screen: String, otherUserUUID: String)` method
    every { mockNavigationActions.navigateTo(any<String>(), any<String>()) } answers
        {
          navigateToChatScreen.value = true
        }

    // Mock the `navigateTo(screen: String)` method
    every { mockNavigationActions.navigateTo(any<String>()) } answers
        {
          navigateToChatScreen.value = true
        }

    // Mock `goBack()` to update the state to simulate going back to the previous screen
    every { mockNavigationActions.goBack() } answers { navigateToChatScreen.value = false }

    val placeholderMessages =
        listOf(
            DataMessage(
                messageType = MessageType.TEXT,
                uuid = UUID.randomUUID(),
                senderUUID = otherUserUUID,
                receiverUUID = currentUserUUID,
                text = "Welcome to the chat!",
                timestamp = System.currentTimeMillis() - 100000),
            DataMessage(
                messageType = MessageType.TEXT,
                uuid = UUID.randomUUID(),
                senderUUID = currentUserUUID,
                receiverUUID = otherUserUUID,
                text = "Thank you!",
                timestamp = System.currentTimeMillis() - 50000),
            DataMessage(
                messageType = MessageType.IMAGE,
                uuid = imageTestMessageUUID,
                senderUUID = otherUserUUID,
                receiverUUID = currentUserUUID,
                text = "Image Message",
                timestamp = System.currentTimeMillis()))

    placeholderMessages.forEach { mockMessageRepository.sendMessage(it) { /* No-op */} }
  }

  @Test
  fun testChatNavigationAndMessageManipulation() {

    composeTestRule.setContent {
      if (navigateToChatScreen.value) {
        ChatScreen(
            messageRepository = mockMessageRepository,
            currentUser =
                DataUser(
                    userUUID = currentUserUUID,
                    greeting = "Mr.",
                    firstName = "John",
                    lastName = "Doe",
                    email = "",
                    phoneNumber = "",
                    longitude = 0.0,
                    latitude = 0.0,
                    profilePictureUrl = "",
                    bookList = emptyList(),
                    googleUid = ""),
            otherUser =
                DataUser(
                    userUUID = otherUserUUID,
                    greeting = "Mr.",
                    firstName = "Tester",
                    lastName = "User",
                    email = "",
                    phoneNumber = "",
                    longitude = 0.0,
                    latitude = 0.0,
                    profilePictureUrl = "",
                    bookList = emptyList(),
                    googleUid = ""),
            navController = mockNavigationActions)
      } else {
        ListChatScreen(
            placeHolderData =
                listOf(
                    MessageBox(
                        contact =
                            DataUser(
                                userUUID = currentUserUUID,
                                greeting = "Mr.",
                                firstName = "John",
                                lastName = "Doe",
                                email = "",
                                phoneNumber = "",
                                longitude = 0.0,
                                latitude = 0.0,
                                profilePictureUrl = "",
                                bookList = emptyList(),
                                googleUid = ""),
                        message = "Hello",
                        date = "Today")),
            navigationActions = mockNavigationActions,
            topAppBar = {},
            bottomAppBar = {})
      }
    }
    // Simulate navigating to the chat screen by clicking on John Doe's message box
    composeTestRule.onNodeWithTag("chat_messageBox").assertExists().performClick()

    // Wait until placeholder messages appear
    val firstPlaceholderUUID = mockMessageRepository.messages[0].uuid
    val secondPlaceholderUUID = mockMessageRepository.messages[1].uuid

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag("message_text $firstPlaceholderUUID", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }

    // Assert that ChatScreen is displayed by checking for placeholder messages
    composeTestRule
        .onNodeWithTag("message_text $firstPlaceholderUUID", useUnmergedTree = true)
        .assertExists()
    composeTestRule
        .onNodeWithTag("message_text $secondPlaceholderUUID", useUnmergedTree = true)
        .assertExists()

    composeTestRule.waitUntil(timeoutMillis = 5001) {
      composeTestRule
          .onAllNodesWithTag("hobbit", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    composeTestRule.onNodeWithTag("hobbit", useUnmergedTree = true).assertExists()

    // Send a new text message
    val newMessage = "Hello, World!"
    composeTestRule.onNodeWithTag("message_input_field").performTextInput(newMessage)
    composeTestRule.onNodeWithTag("send_button").performClick()

    // Wait until the new message appears
    val newMessageUUID = mockMessageRepository.messages.first().uuid
    // **DATA LAYER CHECK**: Verify that the message was added to the mockMessageRepository
    composeTestRule.runOnIdle {
      val addedMessage = mockMessageRepository.messages.lastOrNull()
      assertEquals(newMessage, addedMessage?.text)
      assertEquals(currentUserUUID.toString(), addedMessage?.senderUUID.toString())
      assertEquals(otherUserUUID.toString(), addedMessage?.receiverUUID.toString())
    }

    // **EDIT STEP**: Long-press to edit the message
    composeTestRule
        .onNodeWithTag("message_item $newMessageUUID", useUnmergedTree = true)
        .performSemanticsAction(SemanticsActions.OnLongClick)

    // Click on the edit button, modify the message text, and save
    composeTestRule.onNodeWithTag("editButton", useUnmergedTree = true).performClick()
    val editedMessage = "Updated Message"
    composeTestRule
        .onNodeWithTag("message_input_field", useUnmergedTree = true)
        .performTextClearance()
    composeTestRule
        .onNodeWithTag("message_input_field", useUnmergedTree = true)
        .performTextInput(editedMessage)
    composeTestRule.onNodeWithTag("send_button", useUnmergedTree = true).performClick()

    // Wait for the edited message to appear
    composeTestRule.waitUntil(timeoutMillis = 5003) {
      composeTestRule
          .onAllNodesWithTag("message_text $newMessageUUID", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .any {
            it.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text == editedMessage
          }
    }

    // Assert the message text is updated
    composeTestRule
        .onNodeWithTag("message_text $newMessageUUID", useUnmergedTree = true)
        .assertExists()
        .assertTextEquals(editedMessage)

    // **DELETE STEP**: Long-press to delete the edited message
    composeTestRule
        .onNodeWithTag("message_item $newMessageUUID", useUnmergedTree = true)
        .performSemanticsAction(SemanticsActions.OnLongClick)

    // Click on the delete button
    composeTestRule.onNodeWithTag("deleteButton", useUnmergedTree = true).performClick()

    // Wait until the message is deleted
    composeTestRule.waitUntil(timeoutMillis = 5004) {
      composeTestRule
          .onAllNodesWithTag("message_text $newMessageUUID", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isEmpty()
    }

    // Assert the message no longer exists
    composeTestRule
        .onNodeWithTag("message_text $newMessageUUID", useUnmergedTree = true)
        .assertDoesNotExist()

    // **IMAGE INTERACTION STEP**: Locate and click on the image message
    val imageMessageUUID =
        mockMessageRepository.messages[2].uuid // Assuming this is the image message's UUID
    composeTestRule.onNodeWithTag("hobbit", useUnmergedTree = true).assertExists().performClick()

    // Wait for the popup to appear
    composeTestRule.waitUntil(timeoutMillis = 5005) {
      composeTestRule
          .onAllNodesWithTag("popupImage", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    composeTestRule.onNodeWithTag("popupImage", useUnmergedTree = true).assertIsDisplayed()

    // **SIMULATED ZOOM STEP**: Perform scaling on the image popup to simulate zoom
    composeTestRule.onNodeWithTag("popupImage", useUnmergedTree = true).performGesture {
      down(Offset(150f, 150f)) // Simulate a finger press at the center of the image
      moveBy(Offset(50f, 50f)) // Simulate a drag to increase the scale
      up() // Release the finger to end the gesture
    }

    // Wait until the popup is closed
    composeTestRule.waitUntil(timeoutMillis = 5006) {
      composeTestRule
          .onAllNodesWithTag("popupImage", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isEmpty()
    }
    composeTestRule.onNodeWithTag("popupImage", useUnmergedTree = true).assertDoesNotExist()

    // Go back to the chat list screen
    composeTestRule.onNodeWithTag("backIcon", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("chat_listScreen", useUnmergedTree = true).assertExists()
  }

  class MockMessageRepository : MessageRepository {
    val messages = mutableListOf<DataMessage>()
    private var nextUUID = UUID.randomUUID()

    override fun getNewUUID(): UUID {
      val currentUUID = nextUUID
      nextUUID = UUID.randomUUID()
      return currentUUID
    }

    override fun init(callback: (Result<Unit>) -> Unit) {
      callback(Result.success(Unit)) // Simulates successful initialization
    }

    override fun getMessages(callback: (Result<List<DataMessage>>) -> Unit) {
      callback(Result.success(messages))
    }

    override fun sendMessage(message: DataMessage, callback: (Result<Unit>) -> Unit) {
      messages.add(message)
      callback(Result.success(Unit))
    }

    override fun deleteMessage(
        messageUUID: UUID,
        callback: (Result<Unit>) -> Unit,
        context: Context
    ) {
      messages.removeIf { it.uuid == messageUUID }
      callback(Result.success(Unit))
    }

    override fun deleteAllMessages(
        user1UUID: UUID,
        user2UUID: UUID,
        callback: (Result<Unit>) -> Unit
    ) {
      messages.removeIf {
        (it.senderUUID == user1UUID && it.receiverUUID == user2UUID) ||
            (it.senderUUID == user2UUID && it.receiverUUID == user1UUID)
      }
      callback(Result.success(Unit))
    }

    override fun updateMessage(
        message: DataMessage,
        callback: (Result<Unit>) -> Unit,
        context: Context
    ) {
      val index = messages.indexOfFirst { it.uuid == message.uuid }
      if (index != -1) {
        messages[index] = message.copy(text = message.text) // Update the message text
        callback(Result.success(Unit))
      } else {
        callback(Result.failure(Exception("Message not found")))
      }
    }

    override fun addMessagesListener(
        otherUserUUID: UUID,
        currentUserUUID: UUID,
        callback: (Result<List<DataMessage>>) -> Unit
    ): ListenerRegistration {
      // Immediately provide the existing messages for testing
      callback(Result.success(messages))
      return mockk() // Return a mock ListenerRegistration
    }
  }
}
