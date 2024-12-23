package com.android.bookswap.ui.profile

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.bookswap.R
import com.android.bookswap.data.DataUser
import com.android.bookswap.model.InputVerification
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.resources.C
import com.android.bookswap.ui.MAXLENGTHEMAIL
import com.android.bookswap.ui.MAXLENGTHFIRSTNAME
import com.android.bookswap.ui.MAXLENGTHGREETING
import com.android.bookswap.ui.MAXLENGTHLASTNAME
import com.android.bookswap.ui.MAXLENGTHPHONE
import com.android.bookswap.ui.components.AddressFieldsComponent
import com.android.bookswap.ui.theme.BookSwapAppTheme

/**
 * A composable function that displays a dialog for editing user profile information.
 *
 * @param onDismiss A lambda function to be called when the dialog is dismissed.
 * @param onSave A lambda function to be called when the save button is clicked, with the updated
 *   DataUser object.
 * @param dataUser The DataUser object containing the user's current profile information.
 */
@Composable
fun EditProfileDialog(context: Context, onDismiss: () -> Unit, onSave: (DataUser) -> Unit) {
  val appConfig = LocalAppConfig.current
  val dataUser = appConfig.userViewModel.getUser().copy()
  val _email = remember { mutableStateOf<String>(dataUser.email) }
  val _phone = remember { mutableStateOf<String>(dataUser.phoneNumber) }
  val _greeting = remember { mutableStateOf<String>(dataUser.greeting) }
  val _firstName = remember { mutableStateOf<String>(dataUser.firstName) }
  val _lastName = remember { mutableStateOf<String>(dataUser.lastName) }

  val verification = InputVerification()

  val greetingError = remember { mutableStateOf(false) }
  val emailError = remember { mutableStateOf(false) }
  val phoneError = remember { mutableStateOf(false) }
  val firstNameError = remember { mutableStateOf(false) }
  val lastNameError = remember { mutableStateOf(false) }

  val address = remember { mutableStateOf("") }
  val city = remember { mutableStateOf("") }
  val canton = remember { mutableStateOf("") }
  val postal = remember { mutableStateOf("") }
  val country = remember { mutableStateOf("") }

  BookSwapAppTheme {
    Dialog({ onDismiss() }, DialogProperties(true, false)) {
      Card(Modifier.padding(16.dp)) {
        LazyColumn(
            Modifier.fillMaxWidth().padding(16.dp).testTag(C.Tag.edit_profile_screen_container),
            horizontalAlignment = Alignment.CenterHorizontally) {
              item {
                Text(
                    stringResource(R.string.edit_profile_screen_title),
                    Modifier.testTag(C.Tag.TopAppBar.screen_title))
              }
              item {
                OutlinedTextField(
                    value = _greeting.value,
                    onValueChange = {
                      if (it.length <= MAXLENGTHGREETING) {
                        _greeting.value = it
                        dataUser.greeting = _greeting.value
                        greetingError.value = false
                      } else {
                        greetingError.value = true
                      }
                    },
                    Modifier.testTag(C.Tag.EditProfile.greeting).fillMaxWidth().padding(8.dp, 4.dp),
                    label = { Text(stringResource(R.string.edit_profile_greeting)) },
                    placeholder = {
                      Text(stringResource(R.string.edit_profile_mr), Modifier, Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    isError = greetingError.value,
                )
              }
              item {
                OutlinedTextField(
                    value = _firstName.value,
                    onValueChange = {
                      if (it.length <= MAXLENGTHFIRSTNAME) {
                        _firstName.value = it
                        dataUser.firstName = _firstName.value
                        firstNameError.value = false
                      } else {
                        firstNameError.value = true
                      }
                    },
                    Modifier.testTag(C.Tag.EditProfile.firstname)
                        .fillMaxWidth()
                        .padding(8.dp, 4.dp),
                    label = { Text(stringResource(R.string.edit_profile_firstname)) },
                    placeholder = {
                      Text(stringResource(R.string.edit_profile_john), Modifier, Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    isError = firstNameError.value)
              }
              item {
                OutlinedTextField(
                    value = _lastName.value,
                    onValueChange = {
                      if (it.length <= MAXLENGTHLASTNAME) {
                        _lastName.value = it
                        dataUser.lastName = _lastName.value
                        lastNameError.value = false
                      } else {
                        lastNameError.value = true
                      }
                    },
                    Modifier.testTag(C.Tag.EditProfile.lastname).fillMaxWidth().padding(8.dp, 4.dp),
                    label = { Text(stringResource(R.string.edit_profile_lastname)) },
                    placeholder = {
                      Text(stringResource(R.string.edit_profile_Doe), Modifier, Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    isError = lastNameError.value)
              }
              item {
                OutlinedTextField(
                    value = _email.value,
                    onValueChange = {
                      if (it.length <= MAXLENGTHEMAIL) {
                        _email.value = it
                        emailError.value = false
                        if (verification.validateEmail(_email.value)) {
                          dataUser.email = _email.value
                        }
                      } else {
                        emailError.value = true
                      }
                    },
                    Modifier.testTag(C.Tag.EditProfile.email).fillMaxWidth().padding(8.dp, 4.dp),
                    label = { Text(stringResource(R.string.edit_profile_email)) },
                    placeholder = {
                      Text(
                          stringResource(R.string.edit_profile_email_example), Modifier, Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = emailError.value)
              }
              item {
                OutlinedTextField(
                    value = _phone.value,
                    onValueChange = {
                      if (it.length <= MAXLENGTHPHONE) {
                        _phone.value = it
                        phoneError.value = false
                        if (verification.validatePhone(_phone.value)) {
                          dataUser.phoneNumber = _phone.value
                        }
                      } else {
                        phoneError.value = true
                      }
                    },
                    Modifier.testTag(C.Tag.EditProfile.phone).fillMaxWidth().padding(8.dp, 4.dp),
                    label = { Text(stringResource(R.string.edit_profile_phone)) },
                    placeholder = {
                      Text(
                          stringResource(R.string.edit_profile_phone_example), Modifier, Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = phoneError.value)
              }
              item {
                AddressFieldsComponent(
                    editVSNew = false,
                    address = address.value,
                    onAddressChange = { address.value = it },
                    city = city.value,
                    onCityChange = { city.value = it },
                    canton = canton.value,
                    onCantonChange = { canton.value = it },
                    postal = postal.value,
                    onPostalChange = { postal.value = it },
                    country = country.value,
                    onCountryChange = { country.value = it })
              }
              item {
                Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.SpaceEvenly) {
                  Button(
                      modifier = Modifier.testTag(C.Tag.EditProfile.confirm),
                      onClick = {
                        Log.d(
                            "EditProfile_ClickBtn",
                            "Save Clicked, User info: ${dataUser.printFullname()}")
                        if (city.value.isNotEmpty() && country.value.isNotEmpty()) {
                          val addressComponents =
                              if (address.value.isEmpty() ||
                                  canton.value.isEmpty() ||
                                  postal.value.isEmpty()) {
                                listOf(city.value, country.value)
                              } else {
                                listOf(
                                    address.value,
                                    city.value,
                                    canton.value,
                                    postal.value,
                                    country.value)
                              }
                          appConfig.userViewModel.updateCoordinates(
                              addressComponents,
                              context,
                              appConfig.userViewModel.getUser().userUUID)
                        }
                        onSave(dataUser)
                      }) {
                        Text(stringResource(R.string.edit_profile_save_button))
                      }

                  Button(
                      {
                        Log.i("EditProfile_ClickBtn", "Cancel Clicked")
                        onDismiss()
                      },
                      Modifier.testTag(C.Tag.EditProfile.dismiss)) {
                        Text(stringResource(R.string.edit_profile_cancel_button))
                      }
                }
              }
            }
      }
    }
  }
}
