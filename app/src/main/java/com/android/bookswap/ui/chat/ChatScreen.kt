package com.android.bookswap.ui.chat

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.model.chat.ChatViewModel
import com.android.bookswap.ui.components.BackButtonComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String, // To identify the current user for aligning messages
    viewModel: ChatViewModel,
    navController: NavigationActions
) {
  val context = LocalContext.current
  var messages by remember { mutableStateOf(emptyList<DataMessage>()) }
  var newMessageText by remember { mutableStateOf(TextFieldValue("")) }
  var selectedMessage by remember { mutableStateOf<DataMessage?>(null) }
  var updateActive by remember { mutableStateOf(false) }
  // var listenerRegistration: ListenerRegistration?
  LaunchedEffect(Unit) {
      while (true) {
          viewModel.givemessagerepo().getMessages { result ->
              if (result.isSuccess) {
                  messages = result.getOrThrow()
                      .filter {
                          (it.senderId == currentUserId && it.receiverId ==viewModel.getOtherUserId()) ||
                                  (it.senderId == viewModel.getOtherUserId() && it.receiverId == currentUserId)
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
                text = viewModel.getOtherUserId(),
                style = MaterialTheme.typography.titleMedium,
                color = ColorVariable.Accent,
                modifier =
                    Modifier.testTag("chatName")
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 24.dp))
          },
          navigationIcon = { BackButtonComponent(navController) },
          actions = {
            IconButton(onClick = { /* Handle profile icon click */}) {
              Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "Profile",
                  modifier = Modifier.testTag("profileIcon").size(36.dp),
                  tint = ColorVariable.Accent)
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
          modifier = Modifier.testTag("chatTopAppBar"))
      Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        // Message list
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            verticalArrangement = Arrangement.Bottom) {
              items(messages) { message ->
                MessageItem(
                    message = message,
                    currentUserId = currentUserId,
                    onLongPress = { selectedMessage = message })
              }
            }
        // Message input field and send button
        Row(
            modifier =
                Modifier.fillMaxWidth().padding(top = 8.dp).background(ColorVariable.Primary),
            verticalAlignment = Alignment.CenterVertically) {
              BasicTextField(
                  value = newMessageText,
                  onValueChange = { newMessageText = it },
                  modifier =
                      Modifier.weight(1f)
                          .padding(8.dp)
                          .background(ColorVariable.Secondary, MaterialTheme.shapes.small)
                          .border(1.dp, ColorVariable.Accent, MaterialTheme.shapes.small)
                          .padding(8.dp)
                          .testTag("message_input_field"),
              )
            Button(
                onClick = {
                    if (updateActive) {
                        // Update the message
                        viewModel.givemessagerepo().updateMessage(
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
                        val messageId = viewModel.givemessagerepo().getNewUid()
                        val newMessage =
                            DataMessage(
                                id = messageId,
                                text = newMessageText.text,
                                senderId = currentUserId,
                                receiverId = viewModel.getOtherUserId(), // Ensure receiverId is set here
                                timestamp = System.currentTimeMillis())
                        // Send the message
                        viewModel.givemessagerepo().sendMessage(
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
                modifier = Modifier.padding(horizontal = 8.dp).testTag("send_button")) {
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
                          .padding(8.dp),
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
                                .padding(8.dp)
                                .testTag("editButton")) {
                          Text("Edit")
                        }
                    Button(
                        onClick = {
                          // Handle delete
                          selectedMessage?.let { message ->
                            viewModel.givemessagerepo().deleteMessage(
                                message.id,
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
                                .padding(8.dp)
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
fun MessageItem(message: DataMessage, currentUserId: String, onLongPress: () -> Unit) {
  val isCurrentUser = message.senderId == currentUserId
  val cornerRadius = 25.dp
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
                Modifier.padding(8.dp)
                    .widthIn(max = (LocalConfiguration.current.screenWidthDp.dp * 2 / 3))
                    .border(1.dp, ColorVariable.Accent, shape)
                    .combinedClickable (onClick = {}, onLongClick = { onLongPress() })
                    .testTag("message_item ${message.id}")) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message.text,
                    modifier = Modifier.testTag("message_text ${message.id}"),
                    color = ColorVariable.Accent)
                Text(
                    text = formatTimestamp(message.timestamp),
                    color = ColorVariable.AccentSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier =
                        Modifier.align(Alignment.End).testTag("message_timestamp ${message.id}"))
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
