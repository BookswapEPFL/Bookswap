package com.android.bookswap.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

/**
 * Composable function to display the user profile screen.
 *
 * @param userVM The ViewModel containing user data. Defaults to a new UserViewModel instance.
 * @param topAppBar A composable function to display the top app bar. Defaults to an empty
 *   composable.
 * @param bottomAppBar A composable function to display the bottom app bar. Defaults to an empty
 *   composable.
 */
@Composable
fun UserProfile(
    userVM: UserViewModel = UserViewModel(java.util.UUID.randomUUID()),
    photoStorage: PhotoFirebaseStorageSource = PhotoFirebaseStorageSource(Firebase.storage),
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  val context = LocalContext.current
  var user = userVM.getUser()
  val showEditPicture = remember { mutableStateOf(false) }
  var showEditProfile by remember { mutableStateOf(false) }

  var needRecompose by remember { mutableStateOf(false) }

  val photoRequester =
      PhotoRequester(context) { result ->
        result.fold(
            onSuccess = { image ->
              photoStorage.addPhotoToStorage(
                  photoId = "profile",
                  bitmap = image.asAndroidBitmap(),
                  callback = { result ->
                    result.fold(
                        onSuccess = { url ->
                          userVM.updateUser(picURL = url)
                          showEditPicture.value = false
                        },
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
  photoRequester.Init()

  if (showEditProfile) {
    EditProfileDialog(
        onDismiss = {
          showEditProfile = false
          needRecompose = true
        },
        onSave = {
          userVM.updateUser(
              greeting = it.greeting,
              firstName = it.firstName,
              lastName = it.lastName,
              email = it.email,
              phone = it.phoneNumber,
              user.latitude,
              user.longitude,
              picURL = user.profilePictureUrl)
          showEditProfile = false
          needRecompose = true
        },
        dataUser = user)
  }

  LaunchedEffect(userVM.uuid, needRecompose) {
    user = userVM.getUser()
    needRecompose = false
  }

  if (showEditPicture.value) {

    Dialog(
        onDismissRequest = { showEditPicture.value = false },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
          Card(Modifier.testTag(C.Tag.UserProfile.profileImage).padding(16.dp)) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                Arrangement.Center,
                Alignment.CenterHorizontally) {
                  Text("Edit Profile Picture", Modifier.testTag(C.Tag.TopAppBar.screen_title))
                  ButtonComponent(
                      { photoRequester.requestPhoto() },
                      Modifier.testTag(C.Tag.UserProfile.take_photo)) {
                        Text("Take Photo")
                      }
                }
          }
        }
  }

  // Scaffold to provide basic UI structure with a top app bar
  Scaffold(
      modifier = Modifier.testTag(C.Tag.user_profile_screen_container),
      topBar = topAppBar,
      bottomBar = bottomAppBar) {
        // Column layout to stack input fields vertically with spacing
        Row(
            modifier = Modifier.padding(it).consumeWindowInsets(it).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5f.dp)) {
              Column(modifier = Modifier.fillMaxWidth(0.25f)) {
                Box {
                  IconButton(
                      onClick = { showEditPicture.value = true },
                      modifier = Modifier.aspectRatio(1f)) {
                        Box(
                            modifier =
                                Modifier.padding(2.5f.dp)
                                    .border(3.5f.dp, Color(0xFFA98467), CircleShape)) {
                              if (user.profilePictureUrl.isEmpty()) {
                                Image(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize().scale(1.2f).clipToBounds(),
                                    colorFilter = ColorFilter.tint(Color(0xFF6C584C)))
                              } else {
                                AsyncImage(
                                    model = user.profilePictureUrl,
                                    contentDescription = "profile picture",
                                    modifier =
                                        Modifier.fillMaxSize()
                                            .scale(1.2f)
                                            .clipToBounds()
                                            .clip(CircleShape))
                              }
                            }
                        Box(
                            modifier = Modifier.fillMaxSize().padding(0f.dp),
                            contentAlignment = Alignment.TopEnd) {
                              Image(
                                  imageVector = Icons.Outlined.Edit,
                                  contentDescription = "",
                                  colorFilter = ColorFilter.tint(Color(0xFFAAAAAA)))
                            }
                      }
                }
              }
              Column(Modifier.fillMaxHeight().fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                // Full name text
                Text(
                    text = "${user.greeting} ${user.firstName} ${user.lastName}",
                    modifier = Modifier.testTag(C.Tag.UserProfile.fullname))

                // Email text
                Text(text = user.email, modifier = Modifier.testTag(C.Tag.UserProfile.email))

                // Phone number text
                Text(text = user.phoneNumber, modifier = Modifier.testTag(C.Tag.UserProfile.phone))

                // User address
                Text(
                    text = "${user.latitude}, ${user.longitude}",
                    modifier = Modifier.testTag(C.Tag.UserProfile.address))

                // Edit Button
                ButtonComponent(
                    { showEditProfile = true }, Modifier.testTag(C.Tag.UserProfile.edit)) {
                      Text("Edit Profile")
                    }
              }
            }
      }
}

// @Preview(showBackground = true, widthDp = 540, heightDp = 1110)
// @Composable
// fun UserProfilePreview() {
//  val userVM = UserViewModel("")
//  userVM.updateUser(
//    DataUser(
//      "M.",
//      "John",
//      "Doe",
//      "John.Doe@example.com",
//      "+41223456789",
//      0.0,
//      0.0,
//      "dummyPic.png",
//      "dummyUUID0000")
//  )
//  UserProfile(userVM)
// }
