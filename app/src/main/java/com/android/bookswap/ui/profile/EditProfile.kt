package com.android.bookswap.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.User

@Composable
fun EditProfileDialog(onDismiss: () -> Unit, onSave: (User) -> Unit, user: User) {

  var _email = remember { mutableStateOf<String>(user.email) }
  var _phone = remember { mutableStateOf<String>(user.phoneNumber) }
  var _greeting = remember { mutableStateOf<String>(user.greeting) }
  var _firstName = remember { mutableStateOf<String>(user.firstName) }
  var _lastName = remember { mutableStateOf<String>(user.lastName) }

  AlertDialog(
          onDismissRequest = onDismiss,
          title = { Text("Edit Profile") },
          text = {
            Column {
              OutlinedTextField(
                  value = _greeting.value,
                  onValueChange = { _greeting.value = it },
                  label = { Text("greeting") },
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
              OutlinedTextField(
                  value = _firstName.value,
                  onValueChange = { _firstName.value = it },
                  label = { Text("firstname") },
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
              OutlinedTextField(
                  value = _lastName.value,
                  onValueChange = { _lastName.value = it },
                  label = { Text("lastname") },
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
              OutlinedTextField(
                  readOnly = true, // {TODO: fix database UUID }
                  value = _email.value,
                  onValueChange = {},
                  label = { Text("Email") },
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
              OutlinedTextField(
                  value = _phone.value,
                  onValueChange = { _phone.value = it },
                  label = { Text("Phone") },
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
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
                }) {
                  Text("Save")
                }
          },
          dismissButton = {
            androidx.compose.material3.Button(onClick = onDismiss) { Text("Cancel") }
          })
      .also { Log.d("EditProfile", "Alert.also()") }
}
