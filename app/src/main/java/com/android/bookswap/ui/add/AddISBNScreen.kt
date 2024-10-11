package com.android.bookswap.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.models.chat.MessageBox
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.profile.ProfileIcon
import com.android.bookswap.ui.theme.Accent
import com.android.bookswap.ui.theme.BackGround

/** This is the main screen for the chat feature. It displays the list of messages */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddISBNScreen(placeHolderData: List<MessageBox> = emptyList()) {
  Scaffold(
      topBar = {
        MediumTopAppBar(
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = BackGround,
                ),
            title = {
              Box(modifier = Modifier) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.85f)) {
                      IconButton(
                          onClick = { /*TODO*/},
                          modifier = Modifier.testTag("go_back_button"),
                      ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Icon",
                            tint = Accent,
                            modifier = Modifier.size(64.dp),
                        )
                      }
                      Text(
                          text = "Search ISBN",
                          style =
                              TextStyle(
                                  fontSize = 30.sp,
                                  lineHeight = 20.sp,
                                  fontWeight = FontWeight(700),
                                  color = Accent,
                                  letterSpacing = 0.3.sp,
                              ),
                          modifier = Modifier.padding(top = 4.dp))
                    }
              }
            },
            actions = { Box(modifier = Modifier.padding(top = 30.dp)) { ProfileIcon() } })
      },
      content = { pv ->
        Box(
            modifier =
                Modifier.fillMaxSize().padding(pv).padding().background(color = BackGround)) {
              var isbn by remember { mutableStateOf("") }

              Column(
                  modifier = Modifier.fillMaxWidth().padding(top = 40.dp).testTag("isbn_fields"),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(45.dp)) {
                    FieldComponent("ISBN*", value = isbn) {
                      if (it.all { c -> c.isDigit() }) {
                        isbn = it
                      }
                    }
                    ButtonComponent({}) {
                      Row(Modifier.fillMaxWidth()) {
                        Text("Search", style = TextStyle(color = Color.White))
                        Spacer(Modifier.weight(1f))
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search icon",
                            tint = Color.White,
                        )
                      }
                    }
                  }
            }
      },
      bottomBar = {
        // TODO: Change to navbar
        BottomAppBar(modifier = Modifier.background(color = BackGround)) {}
      })
}
