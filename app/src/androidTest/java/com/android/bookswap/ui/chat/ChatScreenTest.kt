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
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import com.google.firebase.firestore.ListenerRegistration
import io.mockk.mockk
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class ChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var placeHolderData: List<DataMessage>
  private lateinit var mockMessageRepository: MessageRepository
  private val currentUserId = "current-user-id"
  private val otherUserId = "other-user-id"
  private lateinit var mockNavigationActions: NavigationActions

  @Before
  fun setUp() {
    mockNavigationActions = mockk()
    placeHolderData =
        List(6) {
          DataMessage(
              uuid = UUID.randomUUID(),
              senderId = "current-user-id",
              receiverId = "other-user-id",
              text = "Test message $it",
              timestamp = it.toLong())
        }
    mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }
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
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
    }
    composeTestRule.onNodeWithTag("message_input_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("send_button").assertIsDisplayed()
  }

  @Test
  fun hasRequiredComponentsAndShowsMessages() {
    val mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
    }
    composeTestRule.onNodeWithTag("message_input_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("send_button").assertIsDisplayed()
    placeHolderData.forEach { message ->
      composeTestRule.waitUntil {
        composeTestRule
            .onAllNodesWithTag("message_item ${message.uuid}", useUnmergedTree = true)
            .fetchSemanticsNodes()
            .isNotEmpty()
      }
      composeTestRule
          .onNodeWithTag("message_item ${message.uuid}", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("message_text ${message.uuid}", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("message_text ${message.uuid}", useUnmergedTree = true)
          .assertTextEquals(message.text)
      composeTestRule
          .onNodeWithTag("message_timestamp ${message.uuid}", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("message_timestamp ${message.uuid}", useUnmergedTree = true)
          .assertTextEquals(formatTimestamp(message.timestamp))
    }
  }

  @Test
  fun hasClickableButton() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
    }
    composeTestRule.onNodeWithTag("send_button").assertHasClickAction()
  }

  @Test
  fun hasCompletableTextField() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
    }
    val testInput = "Hello, World!"
    composeTestRule.onNodeWithTag("message_input_field").performTextInput(testInput)
    composeTestRule.onNodeWithTag("message_input_field").assertTextEquals(testInput)
  }

  @Test
  fun testSendMessage() {
    val testMessageId = UUID.randomUUID()
    val mockMessageRepository = MockMessageFirestoreSource().apply { mockNewUUID = testMessageId }

    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
    }

    val testInput = "Hello, World!"

    composeTestRule.onNodeWithTag("message_input_field").performTextInput(testInput)
    composeTestRule.onNodeWithTag("send_button").performClick()

    // Verify that the message was sent
    val sentMessage = mockMessageRepository.messages.find { it.uuid == testMessageId }
    assert(sentMessage != null) { "Message was not sent" }
    assert(sentMessage?.text == testInput) { "Message text does not match" }
    assert(sentMessage?.senderId == currentUserId) { "Sender ID does not match" }
    assert(sentMessage?.receiverId == otherUserId) { "Receiver ID does not match" }
  }

  @Test
  fun testTopAppBar() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
    }

    composeTestRule.onNodeWithTag("chatTopAppBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chatName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chatName").assertTextEquals(otherUserId)
    composeTestRule.onNodeWithTag("profileIcon", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testPopUpExists() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          navController = mockNavigationActions)
    }

    composeTestRule.waitForIdle()

    val messageNode =
        composeTestRule.onNodeWithTag(
            "message_item ${placeHolderData.first().uuid}", useUnmergedTree = true)
    messageNode.assertExists("Message item not found")

    messageNode.performSemanticsAction(SemanticsActions.OnLongClick)

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag("editButton", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty() &&
          composeTestRule
              .onAllNodesWithTag("deleteButton", useUnmergedTree = true)
              .fetchSemanticsNodes()
              .isNotEmpty()
    }

    composeTestRule.onNodeWithTag("editButton", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("deleteButton", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag("editButton", useUnmergedTree = true).assertHasClickAction()
    composeTestRule.onNodeWithTag("deleteButton", useUnmergedTree = true).assertHasClickAction()
  }

  @Test
  fun testDelete() {
    val mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
    }

    val message = placeHolderData.first()
    val messageNode =
        composeTestRule.onNodeWithTag("message_item ${message.uuid}", useUnmergedTree = true)
    messageNode.assertExists("Message item not found")

    messageNode.performSemanticsAction(SemanticsActions.OnLongClick)

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag("deleteButton", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }

    composeTestRule.onNodeWithTag("deleteButton", useUnmergedTree = true).performClick()

    val deletedMessage = mockMessageRepository.messages.find { it.uuid == message.uuid }
    assert(deletedMessage == null) { "Message was not deleted" }

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag("message_item ${message.uuid}", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isEmpty()
    }

    composeTestRule
        .onNodeWithTag("message_item ${message.uuid}", useUnmergedTree = true)
        .assertDoesNotExist()
  }

  @Test
  fun testEdit() {
    val mockMessageRepository =
        MockMessageFirestoreSource().apply { messages = placeHolderData.toMutableList() }

    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          navController = mockNavigationActions)
    }

    val message = placeHolderData.first()
    val newText = "Updated message text"

    val messageNode =
        composeTestRule.onNodeWithTag("message_item ${message.uuid}", useUnmergedTree = true)
    messageNode.assertExists("Message item not found")

    messageNode.performSemanticsAction(SemanticsActions.OnLongClick)

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onAllNodesWithTag("editButton", useUnmergedTree = true)
          .fetchSemanticsNodes()
          .isNotEmpty()
    }

    composeTestRule.onNodeWithTag("editButton", useUnmergedTree = true).performClick()

    composeTestRule
        .onNodeWithTag("message_input_field", useUnmergedTree = true)
        .assertTextEquals(message.text)

    composeTestRule
        .onNodeWithTag("message_input_field", useUnmergedTree = true)
        .performTextClearance()
    composeTestRule
        .onNodeWithTag("message_input_field", useUnmergedTree = true)
        .performTextInput(newText)
    composeTestRule.onNodeWithTag("send_button", useUnmergedTree = true).performClick()

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      mockMessageRepository.messages.find { it.uuid == message.uuid }?.text == newText
    }

    val updatedMessage = mockMessageRepository.messages.find { it.uuid == message.uuid }
    assert(updatedMessage != null && updatedMessage.text == newText) {
      "Message was not updated correctly"
    }

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
          .onNodeWithTag("message_text ${message.uuid}", useUnmergedTree = true)
          .fetchSemanticsNode()
          .config
          .getOrNull(SemanticsProperties.Text)
          ?.joinToString() == newText
    }

    composeTestRule
        .onNodeWithTag("message_text ${message.uuid}", useUnmergedTree = true)
        .assertTextEquals(newText)

    composeTestRule
        .onNodeWithTag("message_input_field", useUnmergedTree = true)
        .assertTextEquals("")
  }

  @Test
  fun testAllColorsBelongToPalette() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId,
          mockNavigationActions)
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

    override fun getMessages(callback: (Result<List<DataMessage>>) -> Unit) {
      callback(Result.success(messages))
    }

    override fun sendMessage(message: DataMessage, callback: (Result<Unit>) -> Unit) {
      messages.add(message)
      callback(sendMessageResult)
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
        user1Id: String,
        user2Id: String,
        callback: (Result<Unit>) -> Unit
    ) {
      messages.removeIf { it.senderId == user1Id && it.receiverId == user2Id }
      messages.removeIf { it.senderId == user2Id && it.receiverId == user1Id }
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
        callback(Result.success(Unit)) // Simulate success
      } else {
        callback(Result.failure(Exception("Message not found")))
      }
    }

    override fun addMessagesListener(
        otherUserId: String,
        currentUserId: String,
        callback: (Result<List<DataMessage>>) -> Unit
    ): ListenerRegistration {
      requireNotNull(otherUserId) { "otherUserId must not be null" }
      requireNotNull(currentUserId) { "currentUserId must not be null" }

      callback(Result.success(messages)) // Or whatever logic you'd like to simulate
      return mockk()
    }
  }
}
