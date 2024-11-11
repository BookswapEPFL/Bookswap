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

@Composable
fun NewUserScreen(navigaionActions: NavigationActions) {
  val _email = remember { mutableStateOf("") }
  val _phone = remember { mutableStateOf("") }
  val _greeting = remember { mutableStateOf("") }
  val _firstName = remember { mutableStateOf("") }
  val _lastName = remember { mutableStateOf("") }

  LazyColumn(
      contentPadding = PaddingValues(16.dp),
      modifier =
          Modifier.fillMaxSize()
              .background(color = ColorVariable.BackGround)
              .testTag("chat_messageList")) {
        item {
          Text(
              "Welcome",
              modifier = Modifier.testTag("welcomeTxt").fillMaxWidth(),
              style =
                  TextStyle(
                      color = ColorVariable.Accent,
                      fontWeight = FontWeight(600),
                      textAlign = TextAlign.Center,
                      fontSize = 40.sp,
                  ))
        }
        item {
          Text(
              "Please fill in your personal information to start BookSwapping",
              modifier = Modifier.testTag("personalInfoTxt").fillMaxWidth(),
              style =
                  TextStyle(
                      color = ColorVariable.Accent,
                      fontWeight = FontWeight(400),
                      textAlign = TextAlign.Center,
                      fontSize = 18.sp,
                  ))
        }
        item {
          Card(
              Modifier.testTag("editProfileContainer").background(ColorVariable.BackGround),
              colors =
                  androidx.compose.material3.CardDefaults.cardColors()
                      .copy(containerColor = ColorVariable.BackGround)) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp).background(ColorVariable.BackGround),
                    Arrangement.Center,
                    Alignment.CenterHorizontally) {
                      IconButton(
                          onClick = { /*TODO*/},
                          modifier = Modifier.size(80.dp).testTag("profilPics")) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "profile picture",
                                tint = ColorVariable.Accent,
                                modifier = Modifier.size(80.dp))
                          }
                      OutlinedTextField(
                          _greeting.value,
                          { _greeting.value = it },
                          Modifier.testTag("greetingTF").fillMaxWidth().padding(8.dp, 4.dp),
                          label = { Text("Greeting") },
                          placeholder = { Text("Mr.", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true)

                      OutlinedTextField(
                          _firstName.value,
                          { _firstName.value = it },
                          Modifier.testTag("firstnameTF").fillMaxWidth().padding(8.dp, 4.dp),
                          label = { Text("Firstname") },
                          placeholder = { Text("John", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true)

                      OutlinedTextField(
                          _lastName.value,
                          { _lastName.value = it },
                          Modifier.testTag("lastnameTF").fillMaxWidth().padding(8.dp, 4.dp),
                          label = { Text("Lastname") },
                          placeholder = { Text("Doe", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true)

                      OutlinedTextField(
                          _email.value,
                          { _email.value = it },
                          Modifier.testTag("emailTF").fillMaxWidth().padding(8.dp, 4.dp),
                          label = { Text("Email") },
                          placeholder = { Text("John.Doe@example.com", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                          singleLine = true)

                      OutlinedTextField(
                          _phone.value,
                          { _phone.value = it },
                          Modifier.testTag("phoneTF").fillMaxWidth().padding(8.dp, 4.dp),
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
                onClick = { navigaionActions.navigateTo(Route.MAP) },
                colors = ButtonDefaults.buttonColors(ColorVariable.Primary),
                modifier = Modifier.width(200.dp).height(50.dp).testTag("CreateButton")) {
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
