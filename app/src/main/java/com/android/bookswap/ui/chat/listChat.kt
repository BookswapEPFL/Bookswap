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
import androidx.compose.material3.BottomAppBar
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
import com.android.bookswap.models.chat.MessageBox
import com.android.bookswap.ui.profile.ProfileIcon
import com.android.bookswap.ui.theme.Accent
import com.android.bookswap.ui.theme.AccentSecondary
import com.android.bookswap.ui.theme.BackGround
import com.android.bookswap.ui.theme.Primary

/** This is the main screen for the chat feature. It displays the list of messages */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListChatScreen(placeHolderData: List<MessageBox> = emptyList()) {
  Scaffold(
      topBar = {
        TopAppBar(
            colors =
                TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = BackGround,
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
                                color = Accent,
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
                Modifier.fillMaxSize().background(color = BackGround).testTag("chat_messageList")) {
              item { MessageDivider() }
              if (placeHolderData.isEmpty()) {
                item {
                  Text(
                      text = "No messages yet",
                      modifier = Modifier.fillMaxWidth().padding(16.dp),
                      color = Primary,
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold,
                      textAlign = TextAlign.Center)
                }
              } else {
                items(placeHolderData.size) { message ->
                  MessageBoxDisplay(placeHolderData[message]) { /*TODO on click*/}
                  MessageDivider()
                }
              }
            }
      },
      bottomBar = {
        // To Modify with the navbar
        BottomAppBar(modifier = Modifier.background(color = BackGround)) {}
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
          .background(color = BackGround)
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
                    color = Accent)
                Text(text = message.date, fontSize = 14.sp, color = AccentSecondary)
              }

          Text(
              text = message.message,
              fontSize = 14.sp,
              color = AccentSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
        }
  }
}

/*
This function is used to display a divider between the messages
 */
@Composable
fun MessageDivider() {
  HorizontalDivider(
      modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color(0xFF6C584C)))
}
