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
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.model.chat.OfflineMessageStorage
import com.android.bookswap.resources.C
import com.android.bookswap.ui.chat.ChatScreen
import com.android.bookswap.ui.chat.ListChatScreen
import com.android.bookswap.ui.chat.imageTestMessageUUID
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.TopLevelDestination
import com.google.firebase.firestore.ListenerRegistration
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatEndToEnd {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockMessageRepository: MockMessageRepository
  private lateinit var mockPhotoStorage: PhotoFirebaseStorageRepository
  private lateinit var mockMessageStorage: OfflineMessageStorage
  private lateinit var mockUserRepository: UserFirestoreSource
  private lateinit var mockContext: Context
  private val navigateToChatScreen = mutableStateOf(false)
  private lateinit var mockUserVM: UserViewModel
  private lateinit var mockContactVM: ContactViewModel
  private val currentUserUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002") // John Doe
  private val otherUserUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001") // Other user

  @Before
  fun setup() {
    mockPhotoStorage = mockk()
    mockMessageStorage = mockk()
    mockContext = mockk()
    mockUserRepository = mockk()

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
                MessageType.TEXT,
                UUID.randomUUID(),
                "Welcome to the chat!",
                otherUserUUID,
                currentUserUUID,
                System.currentTimeMillis() - 100000),
            DataMessage(
                MessageType.TEXT,
                UUID.randomUUID(),
                "Thank you!",
                currentUserUUID,
                otherUserUUID,
                System.currentTimeMillis() - 50000),
            DataMessage(
                MessageType.IMAGE,
                imageTestMessageUUID,
                "Image Message",
                otherUserUUID,
                currentUserUUID,
                System.currentTimeMillis()))

    placeholderMessages.forEach { mockMessageRepository.sendMessage(it) { /* No-op */} }

    // mock UserViewModel and ContactViewModel
    mockUserVM = mockk()
    mockContactVM = mockk()

    val contactMap =
        MutableStateFlow(
            mapOf(
                currentUserUUID to
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
                        date = "Today")))

    every { mockContactVM.updateMessageBoxMap() } just runs
    every { mockContactVM.messageBoxMap } returns contactMap
    every { mockMessageStorage.extractMessages(any(), any()) } returns placeholderMessages
    every { mockMessageStorage.addMessage(any()) } just Runs
    every { mockMessageStorage.setMessages() } just Runs
    every { mockUserRepository.getUser(any<String>(), any()) } just Runs
    every { mockUserRepository.getUser(any<UUID>(), any()) } just Runs
  }

  @Test
  fun testChatNavigationAndMessageManipulation() {

    composeTestRule.setContent {
      if (navigateToChatScreen.value) {
        ChatScreen(
            mockMessageRepository,
            mockUserRepository,
            DataUser(currentUserUUID, "Mr.", "John", "Doe", "", "", 0.0, 0.0, "", emptyList(), ""),
            DataUser(otherUserUUID, "Mr.", "Tester", "User", "", "", 0.0, 0.0, "", emptyList(), ""),
            mockNavigationActions,
            mockPhotoStorage,
            mockMessageStorage,
            mockContext,
        )
      } else {
        ListChatScreen(
            navigationActions = mockNavigationActions,
            topAppBar = {},
            bottomAppBar = {},
            contactViewModel = mockContactVM)
      }
    }
    // Simulate navigating to the chat screen by clicking on John Doe's message box
    composeTestRule.onNodeWithTag(C.Tag.ChatList.item).assertExists().performClick()

    // Wait until placeholder messages appear
    val firstPlaceholderUUID = mockMessageRepository.messages[0].uuid
    val secondPlaceholderUUID = mockMessageRepository.messages[1].uuid

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag(
              "${firstPlaceholderUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }

    // Assert that ChatScreen is displayed by checking for placeholder messages
    composeTestRule
        .onNodeWithTag(
            "${firstPlaceholderUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertExists()
    composeTestRule
        .onNodeWithTag(
            "${secondPlaceholderUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertExists()

    composeTestRule.waitUntil(timeoutMillis = 5001) {
      composeTestRule
          .onAllNodesWithTag(
              "${imageTestMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    composeTestRule
        .onNodeWithTag(
            "${imageTestMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertExists()

    // Send a new text message
    val newMessage = "Hello, World!"
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.message).performTextInput(newMessage)
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.confirm_button).performClick()

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
        .onNodeWithTag("${newMessageUUID}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
        .performSemanticsAction(SemanticsActions.OnLongClick)

    // Click on the edit button, modify the message text, and save
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.edit, useUnmergedTree = true).performClick()
    val editedMessage = "Updated Message"
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.message, useUnmergedTree = true)
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.message, useUnmergedTree = true)
        .performTextInput(editedMessage)
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.confirm_button, useUnmergedTree = true)
        .performClick()

    // Wait for the edited message to appear
    composeTestRule.waitUntil(timeoutMillis = 5003) {
      composeTestRule
          .onAllNodesWithTag(
              "${newMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .any {
            it.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text == editedMessage
          }
    }

    // Assert the message text is updated
    composeTestRule
        .onNodeWithTag("${newMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertExists()
        .assertTextEquals(editedMessage)

    // **DELETE STEP**: Long-press to delete the edited message
    composeTestRule
        .onNodeWithTag("${newMessageUUID}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
        .performSemanticsAction(SemanticsActions.OnLongClick)

    // Click on the delete button
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.delete, useUnmergedTree = true).performClick()

    // Wait until the message is deleted
    composeTestRule.waitUntil(timeoutMillis = 5004) {
      composeTestRule
          .onAllNodesWithTag(
              "${newMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isEmpty()
    }

    // Assert the message no longer exists
    composeTestRule
        .onNodeWithTag("${newMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertDoesNotExist()

    // **IMAGE INTERACTION STEP**: Locate and click on the image message
    val imageMessageUUID =
        mockMessageRepository.messages[2].uuid // Assuming this is the image message's UUID
    composeTestRule
        .onNodeWithTag(
            "${imageTestMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertExists()
        .performClick()

    // Wait for the popup to appear
    composeTestRule.waitUntil(timeoutMillis = 5005) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
        .assertIsDisplayed()

    // **SIMULATED ZOOM STEP**: Perform scaling on the image popup to simulate zoom
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true).performGesture {
      down(Offset(150f, 150f)) // Simulate a finger press at the center of the image
      moveBy(Offset(50f, 50f)) // Simulate a drag to increase the scale
      up() // Release the finger to end the gesture
    }

    // Wait until the popup is closed
    composeTestRule.waitUntil(timeoutMillis = 5006) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isEmpty()
    }
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
        .assertDoesNotExist()

    // Go back to the chat list screen
    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.back_button, useUnmergedTree = true)
        .performClick()
    composeTestRule
        .onNodeWithTag(C.Tag.chat_list_screen_container, useUnmergedTree = true)
        .assertExists()
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
