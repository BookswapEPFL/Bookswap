package com.android.bookswap.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Route
import com.android.bookswap.ui.theme.ColorVariable

// Constants for magic numbers used in the UI layout
private val CONTENT_PADDING = 16.dp
private val ICON_SIZE = 80.dp
private val TEXT_PADDING = PaddingValues(8.dp, 4.dp)
private val BUTTON_WIDTH = 200.dp
private val BUTTON_HEIGHT = 50.dp
private val WELCOME_FONT_SIZE = 40.sp
private val INFO_FONT_SIZE = 18.sp
private val WELCOME_FONT_WEIGHT = FontWeight(600)
private val INFO_FONT_WEIGHT = FontWeight(400)

/**
 * NewUserScreen is the screen where the user can create a new account by filling in his personal
 * information
 *
 * @param navigationActions: NavigationActions
 */
@Composable
fun NewUserScreen(navigationActions: NavigationActions) {
  val email = remember { mutableStateOf("") }
  val phone = remember { mutableStateOf("") }
  val greeting = remember { mutableStateOf("") }
  val firstName = remember { mutableStateOf("") }
  val lastName = remember { mutableStateOf("") }

  LazyColumn(
      contentPadding = PaddingValues(CONTENT_PADDING),
      modifier =
          Modifier.fillMaxSize()
              .background(color = ColorVariable.BackGround)
              .testTag("chat_messageList")) {
        item {
          // The welcome text
          Text(
              "Welcome",
              modifier = Modifier.testTag("welcomeTxt").fillMaxWidth(),
              style =
                  TextStyle(
                      color = ColorVariable.Accent,
                      fontWeight = WELCOME_FONT_WEIGHT,
                      textAlign = TextAlign.Center,
                      fontSize = WELCOME_FONT_SIZE,
                  ))
        }
        item {
          // The personal information text
          Text(
              "Please fill in your personal information to start BookSwapping",
              modifier = Modifier.testTag("personalInfoTxt").fillMaxWidth(),
              style =
                  TextStyle(
                      color = ColorVariable.Accent,
                      fontWeight = INFO_FONT_WEIGHT,
                      textAlign = TextAlign.Center,
                      fontSize = INFO_FONT_SIZE,
                  ))
        }
        item {
          // The card containing the form to fill in the personal information
          Card(
              Modifier.testTag("editProfileContainer").background(ColorVariable.BackGround),
              colors =
                  androidx.compose.material3.CardDefaults.cardColors()
                      .copy(containerColor = ColorVariable.BackGround)) {
                Column(
                    Modifier.fillMaxWidth()
                        .padding(CONTENT_PADDING)
                        .background(ColorVariable.BackGround),
                    Arrangement.Center,
                    Alignment.CenterHorizontally) {
                      IconButton(
                          onClick = { /* TODO */},
                          modifier = Modifier.size(ICON_SIZE).testTag("profilPics")) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "profile picture",
                                tint = ColorVariable.Accent,
                                modifier = Modifier.size(ICON_SIZE))
                          }
                      OutlinedTextField(
                          greeting.value,
                          { greeting.value = it },
                          Modifier.testTag("greetingTF").fillMaxWidth().padding(TEXT_PADDING),
                          label = { Text("Greeting") },
                          placeholder = { Text("Mr.", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true)

                      OutlinedTextField(
                          firstName.value,
                          { firstName.value = it },
                          Modifier.testTag("firstnameTF").fillMaxWidth().padding(TEXT_PADDING),
                          label = { Text("Firstname") },
                          placeholder = { Text("John", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true)

                      OutlinedTextField(
                          lastName.value,
                          { lastName.value = it },
                          Modifier.testTag("lastnameTF").fillMaxWidth().padding(TEXT_PADDING),
                          label = { Text("Lastname") },
                          placeholder = { Text("Doe", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true)

                      OutlinedTextField(
                          email.value,
                          { email.value = it },
                          Modifier.testTag("emailTF").fillMaxWidth().padding(TEXT_PADDING),
                          label = { Text("Email") },
                          placeholder = { Text("John.Doe@example.com", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                          singleLine = true)

                      OutlinedTextField(
                          phone.value,
                          { phone.value = it },
                          Modifier.testTag("phoneTF").fillMaxWidth().padding(TEXT_PADDING),
                          label = { Text("Phone") },
                          placeholder = { Text("+4122345678", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                          singleLine = true)
                    }
              }
        }
        item {
          Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = { navigationActions.navigateTo(Route.MAP) },
                colors = ButtonDefaults.buttonColors(ColorVariable.Primary),
                modifier =
                    Modifier.width(BUTTON_WIDTH).height(BUTTON_HEIGHT).testTag("CreateButton")) {
                  Text(
                      text = "Create",
                      textAlign = TextAlign.Center,
                      style =
                          TextStyle(
                              color = ColorVariable.BackGround,
                          ))
                }
          }
        }
      }
}