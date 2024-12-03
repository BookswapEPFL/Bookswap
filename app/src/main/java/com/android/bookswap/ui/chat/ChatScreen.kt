package com.android.bookswap.ui.chat

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import com.android.bookswap.R
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.repository.MessageRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.model.chat.OfflineMessageStorage
import com.android.bookswap.resources.C
import com.android.bookswap.ui.MAXLENGTHMESSAGE
import com.android.bookswap.ui.components.BackButtonComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.delay

/**
 * Composable function for the chat screen.
 *
 * @param messageRepository Repository for handling messages.
 * @param currentUser The current user data.
 * @param otherUser The other user data.
 * @param navController Navigation actions for navigating between screens.
 * @param photoStorage Repository for handling photo storage.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messageRepository: MessageRepository,
    userSource: UsersRepository,
    currentUser: DataUser,
    otherUser: DataUser,
    navController: NavigationActions,
    photoStorage: PhotoFirebaseStorageRepository,
    messageStorage: OfflineMessageStorage,
    context: Context
) {
  var messages by remember { mutableStateOf(emptyList<DataMessage>()) }
  var newMessageText by remember { mutableStateOf(TextFieldValue("")) }
  var selectedMessage by remember { mutableStateOf<DataMessage?>(null) }
  var updateActive by remember { mutableStateOf(false) }
  val maxMessagesStoredOffline = 10
  val padding8 = 8.dp
  val padding24 = 24.dp
  val padding36 = 36.dp
  val photoReq =
      PhotoRequester(context) { result ->
        if (result.isSuccess) {
          photoStorage.addPhotoToStorage(
              photoId = UUID.randomUUID().toString(),
              bitmap = result.getOrThrow().asAndroidBitmap(),
              callback = { result ->
                result
                    .onSuccess { url ->
                      messageRepository.sendMessage(
                          dataMessage =
                              DataMessage(
                                  messageType = MessageType.IMAGE,
                                  uuid = messageRepository.getNewUUID(),
                                  text = url,
                                  senderUUID = currentUser.userUUID,
                                  receiverUUID = otherUser.userUUID,
                                  timestamp = System.currentTimeMillis()),
                          callback = { result ->
                            if (result.isSuccess) {
                              Log.d("ChatScreen", "Image stored successfully")
                            } else {
                              Toast.makeText(
                                      context, "Image could not be stored.", Toast.LENGTH_LONG)
                                  .show()
                              Log.e("ChatScreen", "Image could not be stored.")
                            }
                          })
                    }
                    .onFailure { exception ->
                      Log.e("ChatScreen", "Failed to store image: ${exception.message}")
                    }
              })
        } else {
          Toast.makeText(context, "Image could not be stored.", Toast.LENGTH_LONG).show()
          Log.e("ChatScreen", "Image could not be stored.")
        }
      }

  photoReq.Init()

  LaunchedEffect(Unit) {
    while (true) {
      for (message in
          messageStorage.extractMessages(messages.toMutableList(), maxMessagesStoredOffline)) {
        messageStorage.addMessage(message)
      }
      messageStorage.setMessages()
      messageRepository.getMessages { result ->
        if (result.isSuccess) {
          messages =
              result
                  .getOrThrow()
                  .filter {
                    (it.senderUUID == currentUser.userUUID &&
                        it.receiverUUID == otherUser.userUUID) ||
                        (it.senderUUID == otherUser.userUUID &&
                            it.receiverUUID == currentUser.userUUID)
                  }
                  .sortedBy { it.timestamp }
          Log.d("ChatScreen", "Fetched messages: $messages")
        } else {
          messages = messageStorage.getMessagesFromText()
          Log.e("ChatScreen", "Failed to fetch messages: ${result.exceptionOrNull()?.message}")
        }
      }
      delay(2000) // Delay for 2 seconds

      // Check and add contacts for both users
      if (messages.isNotEmpty()) {
        // Add otherUser to currentUser's contacts
        userSource.getUser(currentUser.userUUID) { result ->
          if (result.isSuccess) {
            val updatedUser = result.getOrThrow()
            if (!updatedUser.contactList.contains(otherUser.userUUID)) {
              userSource.addContact(currentUser.userUUID, otherUser.userUUID) { contactResult ->
                if (contactResult.isSuccess) {
                  Log.d(
                      "ChatScreen",
                      "Added ${otherUser.userUUID} to ${currentUser.userUUID}'s contacts")
                } else {
                  Log.e(
                      "ChatScreen",
                      "Failed to add contact: ${contactResult.exceptionOrNull()?.message}")
                }
              }
            }
          } else {
            Log.e(
                "ChatScreen", "Failed to fetch current user: ${result.exceptionOrNull()?.message}")
          }
        }

        // Add currentUser to otherUser's contacts
        userSource.getUser(otherUser.userUUID) { result ->
          if (result.isSuccess) {
            val updatedOtherUser = result.getOrThrow()
            if (!updatedOtherUser.contactList.contains(currentUser.userUUID)) {
              userSource.addContact(otherUser.userUUID, currentUser.userUUID) { contactResult ->
                if (contactResult.isSuccess) {
                  Log.d(
                      "ChatScreen",
                      "Added ${currentUser.userUUID} to ${otherUser.userUUID}'s contacts")
                } else {
                  Log.e(
                      "ChatScreen",
                      "Failed to add contact: ${contactResult.exceptionOrNull()?.message}")
                }
              }
            }
          } else {
            Log.e("ChatScreen", "Failed to fetch other user: ${result.exceptionOrNull()?.message}")
          }
        }
      }
    }
  }
  Box(modifier = Modifier.fillMaxSize().background(ColorVariable.BackGround)) {
    Column(modifier = Modifier.fillMaxSize()) {
      TopAppBar(
          title = {
            Text(
                text = otherUser.firstName + " " + otherUser.lastName,
                style = MaterialTheme.typography.titleMedium,
                color = ColorVariable.Accent,
                modifier =
                    Modifier.testTag(C.Tag.TopAppBar.screen_title)
                        .align(Alignment.CenterHorizontally)
                        .padding(start = padding24))
          },
          navigationIcon = { BackButtonComponent(navController) },
          actions = {
            IconButton(onClick = { /* Handle profile icon click */}) {
              AsyncImage(
                  model = otherUser.profilePictureUrl,
                  contentDescription = "Profile Picture",
                  contentScale = ContentScale.Crop,
                  modifier =
                      Modifier.testTag(C.Tag.TopAppBar.profile_button)
                          .size(padding36)
                          .clip(CircleShape))
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
          modifier = Modifier.testTag(C.Tag.top_app_bar_container))
      Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        // Message list
        LazyColumn(
            modifier = Modifier.weight(1f).padding(padding8).testTag(C.Tag.ChatScreen.scrollable),
            verticalArrangement = Arrangement.Bottom) {
              items(messages) { message ->
                MessageItem(
                    message = message,
                    currentUserUUID = currentUser.userUUID,
                    onLongPress = { selectedMessage = message })
              }
            }

        // Message input field and send button
        Row(
            modifier =
                Modifier.fillMaxWidth().padding(top = padding8).background(ColorVariable.Primary),
            verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                  onClick = { photoReq.requestPhoto() },
                  modifier = Modifier.testTag(C.Tag.ChatScreen.add_image)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Previous Image",
                        tint = ColorVariable.Accent)
                  }
              BasicTextField(
                  value = newMessageText,
                  onValueChange = {
                    if (it.text.length <= MAXLENGTHMESSAGE) {
                      newMessageText = it
                    }
                  },
                  modifier =
                      Modifier.weight(1f)
                          .padding(padding8)
                          .background(ColorVariable.Secondary, MaterialTheme.shapes.small)
                          .border(1.dp, ColorVariable.Accent, MaterialTheme.shapes.small)
                          .padding(padding8)
                          .testTag(C.Tag.ChatScreen.message),
              )
              Button(
                  onClick = {
                    if (newMessageText.text.isEmpty()) {
                      Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                    } else if (updateActive) {
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
                              senderUUID = currentUser.userUUID,
                              receiverUUID = otherUser.userUUID, // Ensure receiverId is set here
                              timestamp = System.currentTimeMillis())
                      // Send the message
                      messageRepository.sendMessage(
                          dataMessage = newMessage,
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
                  modifier =
                      Modifier.padding(horizontal = padding8)
                          .testTag(C.Tag.ChatScreen.confirm_button)) {
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
                                .testTag(C.Tag.ChatScreen.edit)) {
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
                                .testTag(C.Tag.ChatScreen.delete)) {
                          Text("Delete")
                        }
                  }
            }
      }
    }
  }
}
/**
 * Composable function to display a message item in the chat screen.
 *
 * @param message The message data to display.
 * @param currentUserUUID The UUID of the current user.
 * @param onLongPress Callback function to handle long press on the message item.
 */
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
                    .testTag("${message.uuid}_" + C.Tag.ChatScreen.messages)) {
              Column(
                  modifier =
                      Modifier.padding(16.dp)
                          .testTag("${message.uuid}_" + C.Tag.ChatScreen.container)) {
                    if (message.uuid != imageTestMessageUUID &&
                        message.messageType == MessageType.IMAGE) {
                      AsyncImage(
                          model = message.text,
                          contentDescription = "Message Image",
                          modifier =
                              Modifier.testTag("${message.uuid}_" + C.Tag.ChatScreen.content))
                    } else if (message.uuid == imageTestMessageUUID) {
                      Image(
                          painter = painterResource(id = R.drawable.the_hobbit_cover),
                          contentDescription = "Hobbit",
                          modifier =
                              Modifier.size(100.dp)
                                  .testTag("${message.uuid}_" + C.Tag.ChatScreen.content))
                    } else {
                      Text(
                          text = message.text,
                          modifier =
                              Modifier.testTag("${message.uuid}_" + C.Tag.ChatScreen.content),
                          color = ColorVariable.Accent)
                    }
                    Text(
                        text = formatTimestamp(message.timestamp),
                        color = ColorVariable.AccentSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier =
                            Modifier.align(Alignment.End)
                                .testTag("${message.uuid}_" + C.Tag.ChatScreen.timestamp))
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
                      .testTag(C.Tag.ChatScreen.pop_out)
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
                      if (message.uuid == imageTestMessageUUID) {
                        Image(
                            painter = painterResource(id = R.drawable.the_hobbit_cover),
                            contentDescription = "Hobbit",
                            modifier = Modifier.size(imagePopUp * scale))
                      } else
                          AsyncImage(
                              model = message.text,
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
                                          translationY = offsetY))
                    }
              }
        }
  }
}
/**
 * Formats a timestamp into a readable string.
 *
 * @param timestamp The timestamp to format.
 * @return A formatted string representing the timestamp. If the timestamp is from today, it returns
 *   the time in "HH:mm" format. Otherwise, it returns the date in "MMM dd, yyyy" format.
 */
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
