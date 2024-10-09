package com.android.bookswap.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.android.bookswap.model.chat.Message
import com.android.bookswap.model.chat.MessageRepositoryFirestore
import com.android.bookswap.ui.theme.Accent
import com.android.bookswap.ui.theme.Primary
import com.android.bookswap.ui.theme.Secondary

@Composable
fun MessageView(
    messageRepository: MessageRepositoryFirestore,
    currentUserId: String // To identify the current user for aligning messages
) {
  var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
  var newMessageText by remember { mutableStateOf(TextFieldValue("")) }

  // Fetch messages from Firestore
  LaunchedEffect(Unit) {
    messageRepository.getMessages(
        onSuccess = { fetchedMessages -> messages = fetchedMessages },
        onFailure = { e -> Log.e("MessageView", "Failed to fetch messages: ${e.message}") })
  }

  Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
    // Message list
    LazyColumn(modifier = Modifier.weight(1f).padding(8.dp), reverseLayout = true) {
      items(messages) { message -> MessageItem(message = message, currentUserId = currentUserId) }
    }

    // Message input field and send button
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
          BasicTextField(
              value = newMessageText,
              onValueChange = { newMessageText = it },
              modifier =
                  Modifier.weight(1f)
                      .padding(8.dp)
                      .background(Secondary, MaterialTheme.shapes.small)
                      .padding(8.dp))
          Button(
              onClick = {
                val messageId = messageRepository.getNewUid()
                val newMessage =
                    Message(
                        id = messageId,
                        text = newMessageText.text,
                        senderId = currentUserId,
                        timestamp = System.currentTimeMillis())
                // Send the message
                messageRepository.sendMessage(
                    message = newMessage,
                    onSuccess = {
                      newMessageText = TextFieldValue("") // Clear input field
                      messages = messages + newMessage // Add the new message to the list
                    },
                    onFailure = {
                      // Handle failure
                    })
              }) {
                Text("Send")
              }
        }
  }
}

@Composable
fun MessageItem(message: Message, currentUserId: String) {
  val isCurrentUser = message.senderId == currentUserId
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
            modifier = Modifier.padding(8.dp)) {
              Text(text = message.text, modifier = Modifier.padding(8.dp), color = Color.White)
            }
      }
}
