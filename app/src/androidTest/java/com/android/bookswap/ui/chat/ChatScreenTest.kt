package com.android.bookswap.ui.chat

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.chat.OfflineMessageStorage
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import com.google.firebase.firestore.ListenerRegistration
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var placeHolderData: List<DataMessage>
  private lateinit var mockMessageRepository: MessageRepository
  private lateinit var mockPhotoStorage: PhotoFirebaseStorageRepository
  private lateinit var mockMessageStorage: OfflineMessageStorage
  private val currentUserUUID = UUID.randomUUID()
  private val otherUserUUID = UUID.randomUUID()
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockContext: Context
  private val currentUser =
      DataUser(
          currentUserUUID, "Hello", "Jaime", "Oliver Pastor", "", "", 0.0, 0.0, "", emptyList(), "")
  private val otherUser =
      DataUser(otherUserUUID, "Hey", "Matias", "Salvade", "", "", 0.0, 0.0, "", emptyList(), "")

  @Before
  fun setUp() {
    mockPhotoStorage = mockk()
    mockNavigationActions = mockk()
    mockMessageStorage = mockk()
    mockContext = mockk()

    placeHolderData =
        List(6) {
              DataMessage(
                  MessageType.TEXT,
                  UUID.randomUUID(),
                  "Test message $it",
                  currentUserUUID,
                  otherUserUUID,
                  it.toLong())
            }
            .toMutableList()
    (placeHolderData as MutableList<DataMessage>).add(
        DataMessage(
            MessageType.IMAGE,
            imageTestMessageUUID,
            "Test message 101",
            currentUserUUID,
            otherUserUUID,
            101L))
    mockMessageRepository =
        MockMessageFirestoreSource().apply {
          messages = placeHolderData as MutableList<DataMessage>
        }

    every { mockMessageStorage.extractMessages(any(), any()) } returns placeHolderData
    every { mockMessageStorage.addMessage(any()) } just Runs
    every { mockMessageStorage.setMessages() } just Runs
  }

  private val palette =
      listOf(
          ColorVariable.Primary,
          ColorVariable.Secondary,
          ColorVariable.Accent,
          ColorVariable.AccentSecondary,
          ColorVariable.BackGround)

  @Test
  fun testFormatTimeStamps() {
    val timestamp = System.currentTimeMillis()
    val formattedTimestamp = formatTimestamp(timestamp)
    val expectedTimestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    assert(formattedTimestamp == expectedTimestamp)
  }

  @Test
  fun hasRequiredComponentsWithoutMessage() {
    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.message).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.confirm_button).assertIsDisplayed()
  }

  @Test
  fun hasRequiredComponentsAndShowsMessages() {
    mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.message).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.confirm_button).assertIsDisplayed()
    placeHolderData.forEach { message ->
      if (message.uuid != imageTestMessageUUID) {
        composeTestRule.waitUntil {
          composeTestRule
              .onAllNodesWithTag(
                  "${message.uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
              .fetchSemanticsNodes()
              .isNotEmpty()
        }
        composeTestRule
            .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
            .assertTextEquals(message.text)
        composeTestRule
            .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.timestamp, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.timestamp, useUnmergedTree = true)
            .assertTextEquals(formatTimestamp(message.timestamp))
      }
    }
  }

  @Test
  fun checkLastMessageIsImage() {
    val mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.scrollable, useUnmergedTree = true)
        .performScrollToIndex(mockMessageRepository.messages.size - 1)

    composeTestRule
        .onNodeWithTag(
            "${imageTestMessageUUID}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertExists("The last message should be an image with the test tag 'hobbit'")
  }

  @Test
  fun hasClickableButton() {
    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.confirm_button).assertHasClickAction()
  }

  @Test
  fun hasCompletableTextField() {
    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }
    val testInput = "Hello, World!"
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.message).performTextInput(testInput)
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.message).assertTextEquals(testInput)
  }

  @Test
  fun testSendMessage() {
    val testMessageId = UUID.randomUUID()
    val mockMessageRepository = MockMessageFirestoreSource().apply { mockNewUUID = testMessageId }

    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    val testInput = "Hello, World!"

    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.message).performTextInput(testInput)
    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.confirm_button).performClick()

    // Verify that the message was sent
    val sentMessage = mockMessageRepository.messages.find { it.uuid == testMessageId }
    assert(sentMessage != null) { "Message was not sent" }
    assert(sentMessage?.text == testInput) { "Message text does not match" }
    assert(sentMessage?.senderUUID == currentUserUUID) { "Sender UUID does not match" }
    assert(sentMessage?.receiverUUID == otherUserUUID) { "Receiver UUID does not match" }
  }

  @Test
  fun testTopAppBar() {
    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    composeTestRule.onNodeWithTag(C.Tag.top_app_bar_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.screen_title)
        .assertTextEquals(otherUser.firstName + " " + otherUser.lastName)
    composeTestRule
        .onNodeWithTag(C.Tag.TopAppBar.profile_button, useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun testPopUpExists() {
    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    composeTestRule.waitForIdle()

    val messageNode =
        composeTestRule.onNodeWithTag(
            "${placeHolderData.first().uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
    messageNode.assertExists("Message item not found")

    messageNode.performSemanticsAction(SemanticsActions.OnLongClick)

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.ChatScreen.edit, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty() &&
          composeTestRule
              .onAllNodesWithTag(C.Tag.ChatScreen.delete, useUnmergedTree = true)
              .fetchSemanticsNodes()
              .isNotEmpty()
    }

    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.edit, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.delete, useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.edit, useUnmergedTree = true)
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.delete, useUnmergedTree = true)
        .assertHasClickAction()
  }

  @Test
  fun testDelete() {
    val mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    val message = placeHolderData.first()
    val messageNode =
        composeTestRule.onNodeWithTag(
            "${message.uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
    messageNode.assertExists("Message item not found")

    messageNode.performSemanticsAction(SemanticsActions.OnLongClick)

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.ChatScreen.delete, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }

    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.delete, useUnmergedTree = true).performClick()

    val deletedMessage = mockMessageRepository.messages.find { it.uuid == message.uuid }
    assert(deletedMessage == null) { "Message was not deleted" }

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag("${message.uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isEmpty()
    }

    composeTestRule
        .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
        .assertDoesNotExist()
  }

  @Test
  fun testEdit() {
    val mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    val message = placeHolderData.first()
    val newText = "Updated message text"

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag("${message.uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    val messageNode =
        composeTestRule.onNodeWithTag(
            "${message.uuid}_" + C.Tag.ChatScreen.messages, useUnmergedTree = true)

    messageNode.performSemanticsAction(SemanticsActions.OnLongClick)

    composeTestRule.waitUntil(timeoutMillis = 5367) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.ChatScreen.edit, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }

    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.edit, useUnmergedTree = true).performClick()

    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.message, useUnmergedTree = true)
        .assertTextEquals(message.text)

    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.message, useUnmergedTree = true)
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.message, useUnmergedTree = true)
        .performTextInput(newText)
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.confirm_button, useUnmergedTree = true)
        .performClick()

    composeTestRule.waitUntil(timeoutMillis = 5390) {
      mockMessageRepository.messages.find { it.uuid == message.uuid }?.text == newText
    }

    val updatedMessage = mockMessageRepository.messages.find { it.uuid == message.uuid }
    assert(updatedMessage != null && updatedMessage.text == newText) {
      "Message was not updated correctly"
    }

    composeTestRule.waitUntil(timeoutMillis = 5399) {
      composeTestRule
          .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
          .fetchSemanticsNode()
          .config
          .getOrNull(SemanticsProperties.Text)
          ?.joinToString() == newText
    }

    composeTestRule
        .onNodeWithTag("${message.uuid}_" + C.Tag.ChatScreen.content, useUnmergedTree = true)
        .assertTextEquals(newText)

    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.message, useUnmergedTree = true)
        .assertTextEquals("")
  }

  @Test
  fun scrollToBottomClickImageAndCheckPopup() {
    val mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.scrollable, useUnmergedTree = true)
        .performScrollToIndex(mockMessageRepository.messages.size - 1)

    composeTestRule
        .onNodeWithTag(
            "${imageTestMessageUUID}_" + C.Tag.ChatScreen.container, useUnmergedTree = true)
        .performClick()

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true).performClick()

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isEmpty()
    }
    composeTestRule
        .onNodeWithTag(C.Tag.ChatScreen.pop_out, useUnmergedTree = true)
        .assertDoesNotExist()
  }

  @Test
  fun testAllColorsBelongToPalette() {
    composeTestRule.setContent {
      ChatScreen(
          mockMessageRepository,
          currentUser,
          otherUser,
          mockNavigationActions,
          mockPhotoStorage,
          mockMessageStorage,
          mockContext)
    }

    val uiColors =
        listOf(
            ColorVariable.Primary,
            ColorVariable.Secondary,
            ColorVariable.Accent,
            ColorVariable.AccentSecondary,
            ColorVariable.BackGround)

    uiColors.forEach { color ->
      assert(palette.contains(color)) { "Color $color does not belong to the palette" }
    }
  }

  class MockMessageFirestoreSource : MessageRepository {
    var mockNewUUID: UUID = UUID.randomUUID()
    var messages: MutableList<DataMessage> = mutableListOf()
    private var sendMessageResult: Result<Unit> = Result.success(Unit)

    override fun init(callback: (Result<Unit>) -> Unit) {
      callback(Result.success(Unit))
    }

    override fun getNewUUID(): UUID {
      return mockNewUUID
    }

    override fun getMessages(
        user1UUID: UUID,
        user2UUID: UUID,
        callback: (Result<List<DataMessage>>) -> Unit
    ) {
      // Filter messages to only include those between user1 and user2
      val filteredMessages =
          messages.filter { message ->
            (message.senderUUID == user1UUID && message.receiverUUID == user2UUID) ||
                (message.senderUUID == user2UUID && message.receiverUUID == user1UUID)
          }
      callback(Result.success(filteredMessages))
    }

    override fun sendMessage(message: DataMessage, callback: (Result<Unit>) -> Unit) {
      messages.add(message)
      callback(sendMessageResult)
    }

    override fun deleteMessage(
        messageUUID: UUID,
        user1UUID: UUID,
        user2UUID: UUID,
        callback: (Result<Unit>) -> Unit,
        context: Context
    ) {
      // Remove the message if it matches the UUID and is between the two users
      val removed =
          messages.removeIf { message ->
            message.uuid == messageUUID &&
                ((message.senderUUID == user1UUID && message.receiverUUID == user2UUID) ||
                    (message.senderUUID == user2UUID && message.receiverUUID == user1UUID))
          }
      if (removed) {
        callback(Result.success(Unit))
      } else {
        callback(Result.failure(Exception("Message not found")))
      }
    }

    override fun deleteAllMessages(
        user1UUID: UUID,
        user2UUID: UUID,
        callback: (Result<Unit>) -> Unit
    ) {
      // Remove all messages between the two users
      messages.removeIf { message ->
        (message.senderUUID == user1UUID && message.receiverUUID == user2UUID) ||
            (message.senderUUID == user2UUID && message.receiverUUID == user1UUID)
      }
      callback(Result.success(Unit))
    }

    override fun updateMessage(
        message: DataMessage,
        user1UUID: UUID,
        user2UUID: UUID,
        callback: (Result<Unit>) -> Unit,
        context: Context
    ) {
      val index = messages.indexOfFirst { it.uuid == message.uuid }
      if (index != -1 &&
          ((messages[index].senderUUID == user1UUID && messages[index].receiverUUID == user2UUID) ||
              (messages[index].senderUUID == user2UUID &&
                  messages[index].receiverUUID == user1UUID))) {
        // Update the message text and timestamp
        messages[index] = message.copy(text = message.text, timestamp = System.currentTimeMillis())
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
      requireNotNull(otherUserUUID) { "otherUserId must not be null" }
      requireNotNull(currentUserUUID) { "currentUserId must not be null" }

      // Return messages between the two users
      val filteredMessages =
          messages.filter { message ->
            (message.senderUUID == currentUserUUID && message.receiverUUID == otherUserUUID) ||
                (message.senderUUID == otherUserUUID && message.receiverUUID == currentUserUUID)
          }
      callback(Result.success(filteredMessages))

      return mockk()
    }
  }
}
