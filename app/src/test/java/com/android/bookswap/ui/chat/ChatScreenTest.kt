package com.android.bookswap.ui.chat

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.ui.theme.ColorVariable
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var placeHolderData: List<DataMessage>
  private lateinit var mockMessageRepository: MessageFirestoreSource
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
    mockMessageRepository = mock(MessageFirestoreSource::class.java)
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
    `when`(mockMessageRepository.addMessagesListener(any(), any(), any())).thenAnswer { invocation
      ->
      val callback = invocation.getArgument<(Result<List<DataMessage>>) -> Unit>(2)
      callback(Result.success(placeHolderData))
      mock(ListenerRegistration::class.java)
    }

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
    `when`(mockMessageRepository.getNewUid()).thenReturn(testMessageId)
    `when`(mockMessageRepository.sendMessage(any(), any())).thenAnswer { invocation ->
      val callback = invocation.getArgument<(Result<Unit>) -> Unit>(1)
      callback(Result.success(Unit))
    }

    composeTestRule.setContent {
      ChatScreen(
          messageRepository = mockMessageRepository,
          currentUserId = currentUserId,
          otherUserId = otherUserId)
    }

    val testInput = "Hello, World!"

    composeTestRule.onNodeWithTag("message_input_field").performTextInput(testInput)

    composeTestRule.onNodeWithTag("send_button").performClick()

    verify(mockMessageRepository)
        .sendMessage(
            argThat {
              this.text == testInput &&
                  this.senderId == currentUserId &&
                  this.receiverId == otherUserId
            },
            any())
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
}
