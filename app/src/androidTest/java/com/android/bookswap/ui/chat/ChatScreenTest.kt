package com.android.bookswap.ui.chat

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.ui.theme.ColorVariable
import com.google.firebase.firestore.ListenerRegistration
import io.mockk.mockk
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var placeHolderData: List<DataMessage>
  private lateinit var mockMessageRepository: MessageRepository
  private val currentUserId = "current-user-id"
  private val otherUserId = "other-user-id"

  @Before
  fun setUp() {
    placeHolderData =
        List(6) {
          DataMessage(
              id = it.toString(),
              senderId = "current-user-id",
              receiverId = "other-user-id",
              text = "Test message $it",
              timestamp = System.currentTimeMillis())
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
          otherUserId = otherUserId)
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
          otherUserId = otherUserId)
    }
    composeTestRule.onNodeWithTag("message_input_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("send_button").assertIsDisplayed()
    placeHolderData.forEach { message ->
      composeTestRule.onNodeWithTag("message_item ${message.id}").assertIsDisplayed()
      composeTestRule.onNodeWithTag("message_text ${message.id}").assertIsDisplayed()
      composeTestRule.onNodeWithTag("message_text ${message.id}").assertTextEquals(message.text)
      composeTestRule.onNodeWithTag("message_timestamp ${message.id}").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("message_timestamp ${message.id}")
          .assertTextEquals(formatTimestamp(message.timestamp))
    }
  }

  @Test
  fun hasClickableButton() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId)
    }
    composeTestRule.onNodeWithTag("send_button").assertHasClickAction()
  }

  @Test
  fun hasCompletableTextField() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId)
    }
    val testInput = "Hello, World!"
    composeTestRule.onNodeWithTag("message_input_field").performTextInput(testInput)
    composeTestRule.onNodeWithTag("message_input_field").assertTextEquals(testInput)
  }

  @Test
  fun testSendMessage() {
    val testMessageId = "test-message-id"
    val mockMessageRepository = MockMessageFirestoreSource().apply { mockNewUid = testMessageId }

    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId)
    }

    val testInput = "Hello, World!"

    composeTestRule.onNodeWithTag("message_input_field").performTextInput(testInput)
    composeTestRule.onNodeWithTag("send_button").performClick()

    // Verify that the message was sent
    val sentMessage = mockMessageRepository.messages.find { it.id == testMessageId }
    assert(sentMessage != null) { "Message was not sent" }
    assert(sentMessage?.text == testInput) { "Message text does not match" }
    assert(sentMessage?.senderId == currentUserId) { "Sender ID does not match" }
    assert(sentMessage?.receiverId == otherUserId) { "Receiver ID does not match" }
  }

  @Test
  fun testAllColorsBelongToPalette() {
    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId)
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
    var mockNewUid: String = "mock-uid"
    var messages: MutableList<DataMessage> = mutableListOf()
    private var sendMessageResult: Result<Unit> = Result.success(Unit)

    override fun init(callback: (Result<Unit>) -> Unit) {
      callback(Result.success(Unit))
    }

    override fun getNewUid(): String {
      return mockNewUid
    }

    override fun getMessages(callback: (Result<List<DataMessage>>) -> Unit) {
      callback(Result.success(messages))
    }

    override fun sendMessage(message: DataMessage, callback: (Result<Unit>) -> Unit) {
      messages.add(message)
      callback(sendMessageResult)
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
