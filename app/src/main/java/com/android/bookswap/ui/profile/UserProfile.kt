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
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.theme.*

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
    photoStorage: PhotoFirebaseStorageRepository,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  val context = LocalContext.current
  val appConfig = LocalAppConfig.current
  var userData = appConfig.userViewModel.getUser()
  val addressStr by appConfig.userViewModel.addressStr.collectAsState()
  val showEditPicture = remember { mutableStateOf(false) }
  var showEditProfile by remember { mutableStateOf(false) }

  var needRecompose by remember { mutableStateOf(false) }
  // Create a PhotoRequester instance
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
                          appConfig.userViewModel.updateUser(picURL = url)
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
  photoRequester.Init() // Initialize the photoRequester

  if (showEditProfile) {
    EditProfileDialog(
        onDismiss = {
          showEditProfile = false
          needRecompose = true
        },
        onSave = {
          appConfig.userViewModel.updateUser(
              greeting = it.greeting,
              firstName = it.firstName,
              lastName = it.lastName,
              email = it.email,
              phone = it.phoneNumber,
              userData.latitude,
              userData.longitude,
              picURL = userData.profilePictureUrl)
          showEditProfile = false
          needRecompose = true
        })
  }

  LaunchedEffect(appConfig.userViewModel.uuid, needRecompose) {
    userData = appConfig.userViewModel.getUser()
    needRecompose = false
  }

  if (showEditPicture.value) {

    Dialog(
        onDismissRequest = { showEditPicture.value = false },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
          Card(Modifier.testTag(C.Tag.UserProfile.profileImageBox).padding(16.dp)) {
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
        Column(
            modifier = Modifier.fillMaxHeight().padding(it).consumeWindowInsets(it).padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(5f.dp)) {
                    Column(modifier = Modifier.fillMaxWidth(0.25f)) {
                      Box {
                        IconButton(
                            onClick = { showEditPicture.value = true },
                            modifier =
                                Modifier.aspectRatio(1f).testTag(C.Tag.UserProfile.profileImage)) {
                              Box(
                                  modifier =
                                      Modifier.padding(2.5f.dp)
                                          .border(3.5f.dp, Color(0xFFA98467), CircleShape)) {
                                    // show either the profile picture or the default icon
                                    if (userData.profilePictureUrl.isEmpty()) {
                                      Image(
                                          imageVector = Icons.Rounded.AccountCircle,
                                          contentDescription = "No profile picture",
                                          modifier =
                                              Modifier.fillMaxSize().scale(1.2f).clipToBounds(),
                                          colorFilter = ColorFilter.tint(Color(0xFF6C584C)))
                                    } else {
                                      AsyncImage(
                                          model = userData.profilePictureUrl,
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
                    Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                      // Full name text
                      Text(
                          text = userData.printFullname(),
                          modifier = Modifier.testTag(C.Tag.UserProfile.fullname))

                      // Email text
                      Text(
                          text = userData.email,
                          modifier = Modifier.testTag(C.Tag.UserProfile.email))

                      // Phone number text
                      Text(
                          text = userData.phoneNumber,
                          modifier = Modifier.testTag(C.Tag.UserProfile.phone))

                      // User address
                      Text(
                          text = addressStr, modifier = Modifier.testTag(C.Tag.UserProfile.address))
                    }
                  }
              // Edit Button
              ButtonComponent(
                  { showEditProfile = true }, Modifier.testTag(C.Tag.UserProfile.edit)) {
                    Text("Edit Profile")
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
