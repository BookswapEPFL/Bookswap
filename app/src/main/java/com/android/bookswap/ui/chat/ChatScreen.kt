package com.android.bookswap.ui.chat

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.android.bookswap.R
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.ui.components.BackButtonComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messageRepository: MessageRepository,
    currentUserUUID: UUID, // To identify the current user for aligning messages
    otherUserUUID: UUID,
    navController: NavigationActions
) {
  val context = LocalContext.current
  var messages by remember { mutableStateOf(emptyList<DataMessage>()) }
  var newMessageText by remember { mutableStateOf(TextFieldValue("")) }
  var selectedMessage by remember { mutableStateOf<DataMessage?>(null) }
  var updateActive by remember { mutableStateOf(false) }
  val padding8 = 8.dp
  val padding24 = 24.dp
  val padding36 = 36.dp
  LaunchedEffect(Unit) {
    while (true) {
      messageRepository.getMessages { result ->
        if (result.isSuccess) {
          messages =
              result
                  .getOrThrow()
                  .filter {
                    (it.senderUUID == currentUserUUID && it.receiverUUID == otherUserUUID) ||
                        (it.senderUUID == otherUserUUID && it.receiverUUID == currentUserUUID)
                  }
                  .sortedBy { it.timestamp }
          Log.d("ChatScreen", "Fetched messages: $messages")
        } else {
          Log.e("ChatScreen", "Failed to fetch messages: ${result.exceptionOrNull()?.message}")
        }
      }
      delay(2000) // Delay for 2 seconds
    }
  }
  Box(modifier = Modifier.fillMaxSize().background(ColorVariable.BackGround)) {
    Column(modifier = Modifier.fillMaxSize()) {
      TopAppBar(
          title = {
            Text(
                text = otherUserUUID.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = ColorVariable.Accent,
                modifier =
                    Modifier.testTag("chatName")
                        .align(Alignment.CenterHorizontally)
                        .padding(start = padding24))
          },
          navigationIcon = { BackButtonComponent(navController) },
          actions = {
            IconButton(onClick = { /* Handle profile icon click */}) {
              Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "Profile",
                  modifier = Modifier.testTag("profileIcon").size(padding36),
                  tint = ColorVariable.Accent)
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
          modifier = Modifier.testTag("chatTopAppBar"))
      Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        // Message list
        LazyColumn(
            modifier = Modifier.weight(1f).padding(padding8).testTag("column"),
            verticalArrangement = Arrangement.Bottom) {
              items(messages) { message ->
                MessageItem(
                    message = message,
                    currentUserUUID = currentUserUUID,
                    onLongPress = { selectedMessage = message })
              }
            }

        // Message input field and send button
        Row(
            modifier =
                Modifier.fillMaxWidth().padding(top = padding8).background(ColorVariable.Primary),
            verticalAlignment = Alignment.CenterVertically) {
              BasicTextField(
                  value = newMessageText,
                  onValueChange = { newMessageText = it },
                  modifier =
                      Modifier.weight(1f)
                          .padding(padding8)
                          .background(ColorVariable.Secondary, MaterialTheme.shapes.small)
                          .border(1.dp, ColorVariable.Accent, MaterialTheme.shapes.small)
                          .padding(padding8)
                          .testTag("message_input_field"),
              )
              Button(
                  onClick = {
                    if (updateActive) {
                      // Update the message
                      messageRepository.updateMessage(
                          selectedMessage!!.copy(text = newMessageText.text),
                          { result: Result<Unit> ->
                            if (result.isSuccess) {
                              Log.d("ChatScreen", "Message updated successfully")
                              selectedMessage = null
                              newMessageText = TextFieldValue("")
                              updateActive = false
                            } else {
                              Log.e(
                                  "ChatScreen",
                                  "Failed to update message: ${result.exceptionOrNull()?.message}")
                              selectedMessage = null
                              newMessageText = TextFieldValue("")
                              updateActive = false
                            }
                          },
                          context)
                    } else {
                      // Send a new message
                      val messageId = messageRepository.getNewUUID()
                      val newMessage =
                          DataMessage(
                              messageType = MessageType.TEXT,
                              uuid = messageId,
                              text = newMessageText.text,
                              senderUUID = currentUserUUID,
                              receiverUUID = otherUserUUID, // Ensure receiverId is set here
                              timestamp = System.currentTimeMillis())
                      // Send the message
                      messageRepository.sendMessage(
                          message = newMessage,
                      ) { result ->
                        if (result.isSuccess) {
                          newMessageText = TextFieldValue("")
                        } else {
                          Toast.makeText(context, "Message could not be sent.", Toast.LENGTH_LONG)
                              .show()
                          Log.e(
                              "MessageView",
                              "Failed to send message: ${result.exceptionOrNull()?.message}")
                        }
                      }
                    }
                  },
                  colors =
                      ButtonColors(
                          ColorVariable.Secondary,
                          ColorVariable.Accent,
                          ColorVariable.Secondary,
                          ColorVariable.Accent),
                  modifier = Modifier.padding(horizontal = padding8).testTag("send_button")) {
                    Text(if (updateActive) "Update" else "Send")
                  }
            }
      }
    }
    if (!updateActive) {
      selectedMessage?.let { message ->
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = {
              if (!updateActive) {
                selectedMessage = null
              }
            }) {
              Column(
                  modifier =
                      Modifier.background(ColorVariable.Primary, shape = RoundedCornerShape(8.dp))
                          .border(2.dp, ColorVariable.Accent, shape = RoundedCornerShape(8.dp))
                          .padding(padding8),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                          // Handle edit
                          newMessageText = TextFieldValue(message.text)
                          updateActive = true
                        },
                        modifier =
                            Modifier.background(
                                    ColorVariable.Primary, shape = RoundedCornerShape(50))
                                .padding(padding8)
                                .testTag("editButton")) {
                          Text("Edit")
                        }
                    Button(
                        onClick = {
                          // Handle delete
                          selectedMessage?.let { message ->
                            messageRepository.deleteMessage(
                                message.uuid,
                                { result ->
                                  if (result.isSuccess) {
                                    Log.d("ChatScreen", "Message deleted successfully")
                                    selectedMessage = null
                                  } else {
                                    Log.e(
                                        "ChatScreen",
                                        "Failed to delete message: ${result.exceptionOrNull()?.message}")
                                  }
                                },
                                context)
                          }
                        },
                        modifier =
                            Modifier.background(
                                    ColorVariable.Primary, shape = RoundedCornerShape(50))
                                .padding(padding8)
                                .testTag("deleteButton")) {
                          Text("Delete")
                        }
                  }
            }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageItem(message: DataMessage, currentUserUUID: UUID, onLongPress: () -> Unit) {
  val isCurrentUser = message.senderUUID == currentUserUUID
  val cornerRadius = 25.dp
  val padding8 = 8.dp
  val padding16 = 16.dp
  val imagePopUp = 300.dp
  val shape =
      if (isCurrentUser) {
        RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = cornerRadius,
            bottomStart = cornerRadius,
            bottomEnd = 5.dp)
      } else {
        RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = cornerRadius,
            bottomStart = 5.dp,
            bottomEnd = cornerRadius)
      }
  var showPopup by remember { mutableStateOf(false) }
  var scale by remember { mutableFloatStateOf(1f) }
  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }

  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start) {
        Card(
            colors =
                if (isCurrentUser) {
                  CardColors(
                      ColorVariable.Primary,
                      ColorVariable.Accent,
                      ColorVariable.Primary,
                      ColorVariable.Accent)
                } else {
                  CardColors(
                      ColorVariable.Secondary,
                      ColorVariable.Accent,
                      ColorVariable.Secondary,
                      ColorVariable.Accent)
                },
            shape = shape,
            modifier =
                Modifier.padding(padding8)
                    .widthIn(max = (LocalConfiguration.current.screenWidthDp.dp * 2 / 3))
                    .border(1.dp, ColorVariable.Accent, shape)
                    .combinedClickable(
                        onClick = {
                          if (message.messageType == MessageType.IMAGE) showPopup = true
                        },
                        onLongClick = { onLongPress() })
                    .testTag("message_item ${message.uuid}")) {
              Column(modifier = Modifier.padding(16.dp)) {
                if (message.messageType == MessageType.IMAGE) {
                  Image(
                      painter = painterResource(id = R.drawable.the_hobbit_cover),
                      contentDescription = "Message Image",
                      modifier = Modifier.testTag("hobbit"))
                } else {
                  Text(
                      text = message.text,
                      modifier = Modifier.testTag("message_text ${message.uuid}"),
                      color = ColorVariable.Accent)
                }
                Text(
                    text = formatTimestamp(message.timestamp),
                    color = ColorVariable.AccentSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier =
                        Modifier.align(Alignment.End).testTag("message_timestamp ${message.uuid}"))
              }
            }
      }

  if (showPopup) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = {
          showPopup = false
          scale = 1f
          offsetX = 0f
          offsetY = 0f
        }) {
          Box(
              modifier =
                  Modifier.fillMaxSize()
                      .background(Color.Black.copy(alpha = 0.8f))
                      .clickable {
                        showPopup = false
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                      }
                      .padding(padding16)) {
                Box(
                    modifier =
                        Modifier.align(Alignment.Center)
                            .size(imagePopUp * scale)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY)) {
                      Image(
                          painter = painterResource(id = R.drawable.the_hobbit_cover),
                          contentDescription = "Enlarged Image",
                          modifier =
                              Modifier.size(imagePopUp * scale)
                                  .pointerInput(Unit) {
                                    detectTransformGestures { _, _, zoom, _ -> scale *= zoom }
                                  }
                                  .graphicsLayer(
                                      scaleX = scale,
                                      scaleY = scale,
                                      translationX = offsetX,
                                      translationY = offsetY)
                                  .testTag("HobbitBig"))
                    }
              }
        }
  }
}

fun formatTimestamp(timestamp: Long): String {
  val messageDate = Date(timestamp)
  val currentDate = Date()
  val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
  val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

  return if (dateFormat.format(messageDate) == dateFormat.format(currentDate)) {
    timeFormat.format(messageDate)
  } else {
    dateTimeFormat.format(messageDate)
  }
}

val imageTestMessageUUID: UUID =
    UUID.fromString(
        "11111111-aa16-43d1-8c47-082ac787f755") // Placeholder message for testing image (adapted to
                                                // use UUID)
