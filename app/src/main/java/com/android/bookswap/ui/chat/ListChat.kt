package com.android.bookswap.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.bookswap.R
import com.android.bookswap.data.MessageBox
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.chat.ContactViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

/**
 * Displays the main screen for the chat feature, showing a list of messages.
 *
 * @param navigationActions Actions for navigating between screens.
 * @param topAppBar A composable function for the top app bar.
 * @param bottomAppBar A composable function for the bottom app bar.
 * @param contactViewModel The ViewModel for managing contact data.
 */
@Composable
fun ListChatScreen(
    navigationActions: NavigationActions,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {},
    contactViewModel: ContactViewModel = ContactViewModel(LocalAppConfig.current.userViewModel)
) {
  LaunchedEffect(Unit) { contactViewModel.updateMessageBoxMap() }
  val messageBoxMap by contactViewModel.messageBoxMap.collectAsState()
  val messageList = messageBoxMap.values.toList()
  Scaffold(
      modifier = Modifier.testTag(C.Tag.chat_list_screen_container),
      topBar = topAppBar,
      content = { pv ->
        LazyColumn(
            contentPadding = pv,
            modifier =
                Modifier.fillMaxSize()
                    .background(color = ColorVariable.BackGround)
                    .testTag(C.Tag.ChatList.scrollable)) {
              item { MessageDivider() }
              if (messageBoxMap.isEmpty()) {
                item {
                  Text(
                      text = stringResource(R.string.chat_list_no_messages),
                      modifier = Modifier.fillMaxWidth().padding(16.dp),
                      color = ColorVariable.Primary,
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold,
                      textAlign = TextAlign.Center)
                }
              } else {
                items(messageList.size) { index ->
                  val messageBox = messageList[index]
                  MessageBoxDisplay(messageBox) {
                    navigationActions.navigateTo(
                        C.Screen.CHAT, messageBox.contact.userUUID.toString())
                  }
                  MessageDivider()
                }
              }
            }
      },
      bottomBar = bottomAppBar)
}

/**
 * Displays a message box with contact information and message preview.
 *
 * @param message The message box containing contact and message details.
 * @param onClick The action to perform when the message box is clicked.
 */
@Composable
fun MessageBoxDisplay(message: MessageBox, onClick: () -> Unit = {}) {
  Row(
      Modifier.fillMaxWidth()
          .height(55.dp)
          .background(color = ColorVariable.BackGround)
          .clickable(onClick = onClick)
          .testTag(C.Tag.ChatList.item),
  ) {
    if (message.contact.profilePictureUrl.isNotEmpty()) {
      // Show the profile picture of the contact or the default icon
      AsyncImage(
          model = message.contact.profilePictureUrl,
          contentDescription = "Contact Icon",
          modifier =
              Modifier.size(40.dp)
                  .align(Alignment.CenterVertically)
                  .fillMaxHeight()
                  .clip(CircleShape),
          contentScale = ContentScale.Crop)
    } else {
      Icon(
          imageVector = Icons.Filled.Person,
          contentDescription = "Contact Icon empty",
          modifier = Modifier.size(40.dp).align(Alignment.CenterVertically).fillMaxHeight(),
      )
    }

    Column(
        modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp, top = 4.dp),
        verticalArrangement = Arrangement.Center) {
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = message.contact.firstName + " " + message.contact.lastName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = ColorVariable.Accent,
                    modifier = Modifier.testTag(C.Tag.ChatList.contact))
                Text(
                    text = message.date.takeUnless { it.isNullOrEmpty() } ?: "",
                    fontSize = 14.sp,
                    color = ColorVariable.AccentSecondary,
                    modifier = Modifier.testTag(C.Tag.ChatList.timestamp))
              }

          Text(
              text =
                  message.message?.takeUnless { it.isEmpty() }
                      ?: stringResource(R.string.chat_list_no_messages),
              fontSize = 14.sp,
              color = ColorVariable.AccentSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.testTag(C.Tag.ChatList.message))
        }
  }
}

/** This function is used to display a divider between the messages */
@Composable
fun MessageDivider() {
  HorizontalDivider(
      modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color(0xFF6C584C)))
}
