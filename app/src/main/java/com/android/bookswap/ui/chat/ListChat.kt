package com.android.bookswap.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.bookswap.model.chat.ChatViewModel
import com.android.bookswap.model.chat.MessageBox
import com.android.bookswap.ui.navigation.BottomNavigationMenu
import com.android.bookswap.ui.navigation.List_Navigation_Bar_Destinations
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.profile.ProfileIcon
import com.android.bookswap.ui.theme.ColorVariable

/** This is the main screen for the chat feature. It displays the list of messages */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListChatScreen(
    navController: NavController,
    placeHolderData: List<MessageBox> = emptyList(),
    viewModel: ChatViewModel,
    navigationActions: NavigationActions
) {
  Scaffold(
      topBar = {
        TopAppBar(
            colors =
                TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = ColorVariable.BackGround,
                ),
            title = {
              Box(
                  modifier = Modifier.fillMaxSize().testTag("chat_messageScreenTitle"),
                  contentAlignment = Alignment.Center) {
                    Text(
                        text = "Messages",
                        style =
                            TextStyle(
                                fontSize = 30.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(700),
                                color = ColorVariable.Accent,
                                letterSpacing = 0.3.sp,
                            ))
                  }
            },
            actions = { ProfileIcon() })
      },
      content = { pv ->
        LazyColumn(
            contentPadding = pv,
            modifier =
                Modifier.fillMaxSize()
                    .background(color = ColorVariable.BackGround)
                    .testTag("chat_messageList")) {
              item { MessageDivider() }
              if (placeHolderData.isEmpty()) {
                item {
                  Text(
                      text = "No messages yet",
                      modifier = Modifier.fillMaxWidth().padding(16.dp),
                      color = ColorVariable.Primary,
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold,
                      textAlign = TextAlign.Center)
                }
              } else {
                items(placeHolderData.size) { message ->
                  MessageBoxDisplay(placeHolderData[message]) {
                    ListToChat(
                        viewModel, placeHolderData[message], placeHolderData[message].contactName)
                    navController.navigate("chatScreen/${placeHolderData[message].contactName}")
                  }
                  MessageDivider()
                }
              }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = List_Navigation_Bar_Destinations,
            selectedItem = navigationActions.currentRoute())
      })
}

/*
This function is used to display the message box
 */
@Composable
fun MessageBoxDisplay(message: MessageBox, onClick: () -> Unit = {}) {
  Row(
      Modifier.fillMaxWidth()
          .height(55.dp)
          .background(color = ColorVariable.BackGround)
          .clickable(onClick = onClick)
          .testTag("chat_messageBox"),
  ) {
    Icon(
        imageVector = Icons.Filled.Person,
        contentDescription = "Contact Icon",
        modifier = Modifier.size(40.dp).align(Alignment.CenterVertically).fillMaxHeight(),
    )
    Column(
        modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp, top = 4.dp),
        verticalArrangement = Arrangement.Center) {
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = message.contactName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = ColorVariable.Accent,
                    modifier = Modifier.testTag("chat_messageContactName"))
                Text(
                    text = message.date,
                    fontSize = 14.sp,
                    color = ColorVariable.AccentSecondary,
                    modifier = Modifier.testTag("chat_messageDate"))
              }

          Text(
              text = message.message,
              fontSize = 14.sp,
              color = ColorVariable.AccentSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.testTag("chat_messageContent"))
        }
  }
}

/** This function is used to display a divider between the messages */
@Composable
fun MessageDivider() {
  HorizontalDivider(
      modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color(0xFF6C584C)))
}

/**
 * Sets up the chat to display messages for a specific contact. Changes the other user in the chat
 * view model to match the selected contact.
 *
 * @param viewModel The ChatViewModel managing the messages.
 * @param MessageBox The MessageBox containing information about the contact.
 * @param user The ID of the current user (could be used for further configuration).
 */
fun ListToChat(viewModel: ChatViewModel, MessageBox: MessageBox, user: String) {
  // Change the other user's ID in the chat ViewModel to the contact's name
  viewModel.changeOtherUserId(MessageBox.contactName)
}
