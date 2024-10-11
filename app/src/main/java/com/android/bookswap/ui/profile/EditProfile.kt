package com.android.bookswap.ui.profile

import android.location.Address
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.android.bookswap.data.User
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.theme.BookSwapAppTheme
import java.util.Locale

@Composable
fun EditProfileDialog(onDismiss: () -> Unit, onSave: (User) -> Unit, user: User) {

  var _email = remember { mutableStateOf<String>(user.email) }
  var _phone = remember { mutableStateOf<String>(user.phoneNumber) }
  var _greeting = remember { mutableStateOf<String>(user.greeting) }
  var _firstName = remember { mutableStateOf<String>(user.firstName) }
  var _lastName = remember { mutableStateOf<String>(user.lastName) }

  BookSwapAppTheme {
        AlertDialog(
            modifier = Modifier.testTag("editProfileAlert"),
            onDismissRequest = onDismiss,
            title = { Text("Edit Profile") },
            text = {
              Column() {
                OutlinedTextField(
                    value = _greeting.value,
                    onValueChange = { _greeting.value = it },
                    label = { Text("greeting") },
                    modifier = Modifier.fillMaxWidth().testTag("greetingTbx"),
                )

                OutlinedTextField(
                    value = _firstName.value,
                    onValueChange = { _firstName.value = it },
                    label = { Text("firstname") },
                    modifier = Modifier.fillMaxWidth().testTag("firstNameTbx"))
                OutlinedTextField(
                    value = _lastName.value,
                    onValueChange = { _lastName.value = it },
                    label = { Text("lastname") },
                    modifier = Modifier.fillMaxWidth().testTag("lastNameTbx"))
                OutlinedTextField(
                    readOnly = true, // {TODO: fix database UUID }
                    value = _email.value,
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().testTag("emailTbx"))
                OutlinedTextField(
                    value = _phone.value,
                    onValueChange = { _phone.value = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth().testTag("phoneTbx"))
              }
            },
            confirmButton = {
              androidx.compose.material3.Button(
                  onClick = {
                    val greeting = _greeting.value
                    val firstName = _firstName.value
                    val lastName = _lastName.value
                    val email = _email.value
                    val phone = _phone.value
                    onSave(
                        User(
                            greeting,
                            firstName,
                            lastName,
                            email,
                            phone,
                            user.address,
                            user.profilePictureUrl,
                            user.userId))
                  },
                  modifier = Modifier.testTag("confirmBtn")) {
                    Text("Save")
                  }
            },
            dismissButton = {
              androidx.compose.material3.Button(
                  onClick = onDismiss, modifier = Modifier.testTag("dismissBtn")) {
                    Text("Cancel")
                  }
            })
      }
      .also { Log.d("EditProfile", "Alert.also()") }
}

@Preview
@Composable
fun EditProfileDialoguePreview() {
  val address = Address(Locale.getDefault())
  address.countryCode = "CH"
  address.locality = "Lausanne"
  address.postalCode = "1000"
  address.countryName = "Switzerland"
  address.setAddressLine(0, "Rue de la Gare 1")
  val userVM = UserViewModel("")
  userVM.updateUser(
      User(
          "M.",
          "John",
          "Doe",
          "John.Doe@example.com",
          "+41223456789",
          address,
          "dummyPic.png",
          "dummyUUID0000"))
  EditProfileDialog(onDismiss = { /*TODO*/}, onSave = { /*TODO*/}, user = userVM.getUser())
}
