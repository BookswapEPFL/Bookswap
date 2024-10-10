package com.android.bookswap.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.android.bookswap.model.chat.Message
import com.android.bookswap.model.chat.MessageRepositoryFirestore
import com.android.bookswap.ui.theme.Accent
import com.android.bookswap.ui.theme.AccentSecondary
import com.android.bookswap.ui.theme.BackGround
import com.android.bookswap.ui.theme.Primary
import com.android.bookswap.ui.theme.Secondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageView(
    messageRepository: MessageRepositoryFirestore,
    currentUserId: String, // To identify the current user for aligning messages
    otherUserId: String
) {
  var messages by remember { mutableStateOf(emptyList<Message>()) }
  var newMessageText by remember { mutableStateOf(TextFieldValue("")) }


    DisposableEffect(Unit) {
        val listenerRegistration = messageRepository.addMessagesListener(
            otherUserId = otherUserId,
            currentUserId = currentUserId,
            onSuccess = { fetchedMessages ->
                messages = fetchedMessages
            },
            onFailure = { e ->
                Log.e("MessageView", "Failed to fetch messages: ${e.message}")
            }
        )

        onDispose {
            listenerRegistration.remove()
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(BackGround)) {
      Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
          // Message list
          LazyColumn(
              modifier = Modifier.weight(1f).padding(8.dp),
              verticalArrangement = Arrangement.Bottom
          ) {
              items(messages) { message ->
                  MessageItem(
                      message = message,
                      currentUserId = currentUserId
                  )
              }
          }

          // Message input field and send button
          Row(
              modifier = Modifier.fillMaxWidth().padding(top = 8.dp).background(Primary),
              verticalAlignment = Alignment.CenterVertically
          ) {
              BasicTextField(
                  value = newMessageText,
                  onValueChange = { newMessageText = it },
                  modifier =
                  Modifier.weight(1f)
                      .padding(8.dp)
                      .background(Secondary, MaterialTheme.shapes.small)
                      .border(1.dp, Accent, MaterialTheme.shapes.small)
                      .padding(8.dp)
              )
              Button(
                  onClick = {
                      val messageId = messageRepository.getNewUid()
                      val newMessage = Message(
                          id = messageId,
                          text = newMessageText.text,
                          senderId = currentUserId,
                          receiverId = otherUserId, // Ensure receiverId is set here
                          timestamp = System.currentTimeMillis()
                      )
                      // Send the message
                      messageRepository.sendMessage(
                          message = newMessage,
                          onSuccess = {
                              newMessageText = TextFieldValue("") // Clear input field
                          },
                          onFailure = { e ->
                              Log.e("MessageView", "Failed to send message: ${e.message}")
                          })
                  },
                  colors = ButtonColors(Secondary, Accent, Secondary, Accent),
                  modifier = Modifier.padding(horizontal = 8.dp)
              ) {
                  Text("Send")
              }
          }
  }
  }
}

@Composable
fun MessageItem(message: Message, currentUserId: String) {
  val isCurrentUser = message.senderId == currentUserId
  val cornerRadius = 25.dp
  val shape = if (isCurrentUser) {
      RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius, bottomStart = cornerRadius, bottomEnd = 5.dp)
  } else {
      RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius, bottomStart = 5.dp, bottomEnd = cornerRadius)
  }
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start) {
      Card(
          colors =
          if (isCurrentUser) {
              CardColors(Primary, Accent, Primary, Accent)
          } else {
              CardColors(Secondary, Accent, Secondary, Accent)
          },
          shape = shape,
          modifier = Modifier
              .padding(8.dp)
              .widthIn(max = (LocalConfiguration.current.screenWidthDp.dp * 2 / 3))
              .border(1.dp, Accent, shape)
      ) {
          Column(modifier = Modifier.padding(16.dp)) {
              Text(
                  text = message.text,
                  color = Accent
              )
              Text(
                  text = formatTimestamp(message.timestamp),
                  color = AccentSecondary,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.align(Alignment.End)
              )
          }
      }


  }}

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
