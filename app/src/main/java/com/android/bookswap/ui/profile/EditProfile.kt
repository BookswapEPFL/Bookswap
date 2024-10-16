package com.android.bookswap.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.bookswap.data.DataUser
import com.android.bookswap.ui.theme.BookSwapAppTheme

@Composable
fun EditProfileDialog(
    onDismiss: () -> Unit,
    onSave: (DataUser) -> Unit,
    dataUser: DataUser,
    modifier: Modifier = Modifier
) {

  var _email = remember { mutableStateOf<String>(dataUser.email) }
  var _phone = remember { mutableStateOf<String>(dataUser.phoneNumber) }
  var _greeting = remember { mutableStateOf<String>(dataUser.greeting) }
  var _firstName = remember { mutableStateOf<String>(dataUser.firstName) }
  var _lastName = remember { mutableStateOf<String>(dataUser.lastName) }

  BookSwapAppTheme {
    Dialog({ onDismiss() }, DialogProperties(true, true)) {
      Card(Modifier.testTag("editProfileContainer").padding(16.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            Arrangement.Center,
            Alignment.CenterHorizontally) {
              Text("Edit Profile", Modifier.testTag("editProfileTitleTxt"))
              OutlinedTextField(
                  _greeting.value,
                  {
                    _greeting.value = it
                    dataUser.greeting = _greeting.value
                  },
                  Modifier.testTag("greetingTbx").fillMaxWidth().padding(8.dp, 4.dp),
                  label = { Text("Greeting") },
                  placeholder = { Text("Mr.", Modifier, Color.Gray) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                  singleLine = true)

              OutlinedTextField(
                  _firstName.value,
                  {
                    _firstName.value = it
                    dataUser.firstName = _firstName.value
                  },
                  Modifier.testTag("firstnameTbx").fillMaxWidth().padding(8.dp, 4.dp),
                  label = { Text("Firstname") },
                  placeholder = { Text("John", Modifier, Color.Gray) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                  singleLine = true)

              OutlinedTextField(
                  _lastName.value,
                  {
                    _lastName.value = it
                    dataUser.lastName = _lastName.value
                  },
                  Modifier.testTag("lastnameTbx").fillMaxWidth().padding(8.dp, 4.dp),
                  label = { Text("Lastname") },
                  placeholder = { Text("Doe", Modifier, Color.Gray) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                  singleLine = true)

              OutlinedTextField(
                  _email.value,
                  {
                    _email.value = it
                    dataUser.email = _email.value
                  },
                  Modifier.testTag("emailTbx").fillMaxWidth().padding(8.dp, 4.dp),
                  label = { Text("Email") },
                  placeholder = { Text("John.Doe@example.com", Modifier, Color.Gray) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                  singleLine = true)

              OutlinedTextField(
                  _phone.value,
                  {
                    _phone.value = it
                    dataUser.phoneNumber = _phone.value
                  },
                  Modifier.testTag("phoneTbx").fillMaxWidth().padding(8.dp, 4.dp),
                  label = { Text("Phone") },
                  placeholder = { Text("+4122345678", Modifier, Color.Gray) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                  singleLine = true)

              Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceEvenly) {
                Button(
                    {
                      Log.d(
                          "EditProfile_ClickBtn",
                          "Save Clicked, User info: ${dataUser.printFull1Line()}")
                      onSave(dataUser)
                    },
                    Modifier.testTag("confirmBtn")) {
                      Text("Save")
                    }

                Button(
                    { Log.d("EditProfile_ClickBtn", "Cancel Clicked") },
                    Modifier.testTag("dismissBtn")) {
                      Text("Cancel")
                    }
              }
            }
      }
    }
  }
}

/*@Preview(showBackground = true, widthDp = 540, heightDp = 1110)
@Composable
fun EditProfileDialoguePreview() {
  val address = android.location.Address(java.util.Locale.getDefault())
  address.countryCode = "CH"
  address.locality = "Lausanne"
  address.postalCode = "1000"
  address.countryName = "Switzerland"
  address.setAddressLine(0, "Rue de la Gare 1")
  EditProfileDialog(onDismiss = { *//*TODO*//* }, onSave = { *//*TODO*//* }, dataUser = DataUser("Mr.", "John", "Doe", "John.Dow@example.com","+41223456789",address,"dummyPic.png","dummyUUID0000"))
                                                                       }*/
