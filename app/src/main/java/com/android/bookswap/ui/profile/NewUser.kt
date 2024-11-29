package com.android.bookswap.ui.profile

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.InputVerification
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.resources.C
import com.android.bookswap.ui.MAXLENGTHEMAIL
import com.android.bookswap.ui.MAXLENGTHFIRSTNAME
import com.android.bookswap.ui.MAXLENGTHGREETING
import com.android.bookswap.ui.MAXLENGTHLASTNAME
import com.android.bookswap.ui.MAXLENGTHPHONE
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
private val ERROR_FONT_SIZE = 12.sp

/**
 * NewUserScreen is the screen where the user can create a new account by filling in his personal
 * information
 *
 * @param navigationActions: NavigationActions
 */
@Composable
fun NewUserScreen(
    navigationActions: NavigationActions,
    photoStorage: PhotoFirebaseStorageRepository
) {
  val context = LocalContext.current
  val verification = InputVerification()

  val email = remember { mutableStateOf("") }
  val phone = remember { mutableStateOf("") }
  val greeting = remember { mutableStateOf("") }
  val firstName = remember { mutableStateOf("") }
  val lastName = remember { mutableStateOf("") }

  val emailError = remember { mutableStateOf<String?>("Invalid email format") }
  val phoneError = remember { mutableStateOf<String?>("Invalid phone number") }
  val firstNameError = remember { mutableStateOf<String?>("First name required") }
  val lastNameError = remember { mutableStateOf<String?>("Last name required") }

  val appConfig = LocalAppConfig.current
  var firstAttempt = true

  val profilPicture = remember { mutableStateOf<String?>(null) }
  // This is the photo requester that will be used to take a photo
  val photoRequester =
      PhotoRequester(context) { result ->
        result.fold(
            onSuccess = { image ->
              photoStorage.addPhotoToStorage(
                  photoId = "profile",
                  bitmap = image.asAndroidBitmap(),
                  callback = { result ->
                    result.fold(
                        onSuccess = { url -> profilPicture.value = url },
                        onFailure = { exception ->
                          Log.e("NewUserScreen", "Error uploading photo: $exception")
                          Toast.makeText(context, "Error uploading photo", Toast.LENGTH_SHORT)
                              .show()
                        })
                  })
            },
            onFailure = { exception ->
              Log.e("NewUserScreen", "Error taking photo: $exception")
              Toast.makeText(context, "Error taking photo", Toast.LENGTH_SHORT).show()
            })
      }
  photoRequester.Init() // This is the initialization of the photo requester
  LazyColumn(
      contentPadding = PaddingValues(CONTENT_PADDING),
      modifier =
          Modifier.fillMaxSize()
              .background(color = ColorVariable.BackGround)
              .testTag(C.Tag.new_user_screen_container)) {
        item {
          Text(
              "Welcome",
              modifier = Modifier.testTag(C.Tag.TopAppBar.screen_title).fillMaxWidth(),
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
              modifier = Modifier.testTag(C.Tag.NewUser.personal_info).fillMaxWidth(),
              style =
                  TextStyle(
                      color = ColorVariable.Accent,
                      fontWeight = INFO_FONT_WEIGHT,
                      textAlign = TextAlign.Center,
                      fontSize = INFO_FONT_SIZE,
                  ))
        }

        item {
          Card(
              Modifier.testTag(C.Tag.edit_profile_screen_container)
                  .background(ColorVariable.BackGround),
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
                          onClick = { photoRequester.requestPhoto() },
                          modifier = Modifier.size(ICON_SIZE).testTag(C.Tag.NewUser.profile_pic)) {
                            // Show either the profile picture or the default icon
                            if (profilPicture.value == null) {
                              Icon(
                                  imageVector = Icons.Default.AccountCircle,
                                  contentDescription = "no profile picture",
                                  tint = ColorVariable.Accent,
                                  modifier = Modifier.size(ICON_SIZE))
                            } else {
                              AsyncImage(
                                  model = profilPicture.value,
                                  contentDescription = "profile picture",
                                  modifier = Modifier.size(ICON_SIZE).clip(CircleShape),
                                  contentScale = ContentScale.Crop)
                            }
                          }
                      OutlinedTextField(
                          greeting.value,
                          { if (it.length <= MAXLENGTHGREETING) greeting.value = it },
                          Modifier.testTag(C.Tag.NewUser.greeting)
                              .fillMaxWidth()
                              .padding(TEXT_PADDING),
                          label = { Text("Greeting") },
                          placeholder = { Text("Mr.", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true)

                      OutlinedTextField(
                          firstName.value,
                          { if (it.length <= MAXLENGTHFIRSTNAME) firstName.value = it },
                          Modifier.testTag(C.Tag.NewUser.firstname)
                              .fillMaxWidth()
                              .padding(TEXT_PADDING),
                          label = { Text("Firstname") },
                          placeholder = { Text("John", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true,
                          isError =
                              !verification.validateNonEmpty(firstName.value) && !firstAttempt)
                      if (!verification.validateNonEmpty(firstName.value) && !firstAttempt) {
                        Text(
                            firstNameError.value!!,
                            color = Color.Red,
                            fontSize = ERROR_FONT_SIZE,
                            modifier = Modifier.testTag(C.Tag.NewUser.firstname_error))
                      }

                      OutlinedTextField(
                          lastName.value,
                          { if (it.length <= MAXLENGTHLASTNAME) lastName.value = it },
                          Modifier.testTag(C.Tag.NewUser.lastname)
                              .fillMaxWidth()
                              .padding(TEXT_PADDING),
                          label = { Text("Lastname") },
                          placeholder = { Text("Doe", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                          singleLine = true,
                          isError = !verification.validateNonEmpty(lastName.value) && !firstAttempt)
                      if (!verification.validateNonEmpty(lastName.value) && !firstAttempt) {
                        Text(
                            lastNameError.value!!,
                            color = Color.Red,
                            fontSize = ERROR_FONT_SIZE,
                            modifier = Modifier.testTag(C.Tag.NewUser.lastname_error))
                      }

                      OutlinedTextField(
                          email.value,
                          { if (it.length <= MAXLENGTHEMAIL) email.value = it },
                          Modifier.testTag(C.Tag.NewUser.email)
                              .fillMaxWidth()
                              .padding(TEXT_PADDING),
                          label = { Text("Email") },
                          placeholder = { Text("John.Doe@example.com", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                          singleLine = true,
                          isError = !verification.validateEmail(email.value) && !firstAttempt)
                      if (!verification.validateEmail(email.value) && !firstAttempt) {
                        Text(
                            emailError.value!!,
                            color = Color.Red,
                            fontSize = ERROR_FONT_SIZE,
                            modifier = Modifier.testTag(C.Tag.NewUser.email_error))
                      }

                      OutlinedTextField(
                          phone.value,
                          { if (it.length <= MAXLENGTHPHONE) phone.value = it },
                          Modifier.testTag(C.Tag.NewUser.phone)
                              .fillMaxWidth()
                              .padding(TEXT_PADDING),
                          label = { Text("Phone") },
                          placeholder = { Text("+4122345678", Modifier, Color.Gray) },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                          singleLine = true,
                          isError = !verification.validatePhone(phone.value) && !firstAttempt)
                      if (!verification.validatePhone(phone.value) && !firstAttempt) {
                        Text(
                            phoneError.value!!,
                            color = Color.Red,
                            fontSize = ERROR_FONT_SIZE,
                            modifier = Modifier.testTag(C.Tag.NewUser.phone_error))
                      }
                    }
              }
        }

        item {
          Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                  if (verification.validateEmail(email.value) &&
                      verification.validatePhone(phone.value) &&
                      verification.validateNonEmpty(firstName.value) &&
                      verification.validateNonEmpty(lastName.value)) {
                    appConfig.userViewModel.updateUser(
                        greeting = greeting.value,
                        firstName = firstName.value,
                        lastName = lastName.value,
                        email = email.value,
                        phone = phone.value,
                        picURL = profilPicture.value ?: "",
                        googleUid = Firebase.auth.currentUser?.uid ?: "")
                    navigationActions.navigateTo(C.Route.MAP)
                  } else {
                    firstAttempt = false
                    Toast.makeText(context, "Please correct the errors", Toast.LENGTH_SHORT).show()
                  }
                },
                colors = ButtonDefaults.buttonColors(ColorVariable.Primary),
                modifier =
                    Modifier.width(BUTTON_WIDTH)
                        .height(BUTTON_HEIGHT)
                        .testTag(C.Tag.NewUser.confirm)) {
                  Text(
                      text = "Create",
                      textAlign = TextAlign.Center,
                      style = TextStyle(color = ColorVariable.BackGround))
                }
          }
        }
      }
}
