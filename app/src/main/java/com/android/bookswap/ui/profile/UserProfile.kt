package com.android.bookswap.ui.profile

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.navigation.BackButton
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.*
import com.android.bookswap.user

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(userVM: UserViewModel = UserViewModel()) {
  var user = userVM.getUser()
  var showEditProfile by remember { mutableStateOf(false) }

  var needRecompose by remember { mutableStateOf(false) }

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
        dataUser = user,
        modifier = Modifier.testTag("editProfileAld"))
  }

  LaunchedEffect(userVM.uuid, needRecompose) {
    user = userVM.getUser()
    needRecompose = false
  }

  BookSwapAppTheme() {

    // Scaffold to provide basic UI structure with a top app bar
    Scaffold(
        modifier = Modifier.testTag("profileScreenContainer"),
        topBar = {
          TopAppBar(
              modifier = Modifier.testTag("profileTopAppBar"),
              // Title of the screen
              title = { Text("Your Profile", modifier = Modifier.testTag("profileTitleTxt")) },
              // Icon button for navigation (currently no action defined)
              navigationIcon = {  IconButton(modifier = Modifier.size(32.dp), onClick = {}) {
                Icon(
                  imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                  contentDescription = "",
                  modifier = Modifier.size(30.dp))
              }})
        }) {
          // Column layout to stack input fields vertically with spacing
          Row(
              modifier = Modifier.padding(it).consumeWindowInsets(it).fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(5f.dp)) {
                Column(modifier = Modifier.fillMaxWidth(0.25f)) {
                  Box {
                    IconButton(onClick = { /*TODO*/}, modifier = Modifier.aspectRatio(1f)) {
                      Box(
                          modifier =
                              Modifier.padding(2.5f.dp)
                                  .border(3.5f.dp, Color(0xFFA98467), CircleShape)) {
                            Image(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize().scale(1.2f).clipToBounds(),
                                colorFilter = ColorFilter.tint(Color(0xFF6C584C)))
                          }
                      Box(
                          modifier = Modifier.fillMaxSize().padding(0f.dp),
                          contentAlignment = Alignment.TopEnd) {
                            IconButton(onClick = { /*TODO*/}, modifier = Modifier) {
                              Image(
                                  imageVector = Icons.Outlined.Edit,
                                  contentDescription = "",
                                  colorFilter = ColorFilter.tint(Color(0xFFAAAAAA)))
                            }
                          }
                    }
                  }
                }
                Column(Modifier.fillMaxHeight().fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                  // Full name text
                  Text(
                      text = "${user.greeting} ${user.firstName} ${user.lastName}",
                      modifier = Modifier.testTag("fullNameTxt"))

                  // Email text
                  Text(text = user.email, modifier = Modifier.testTag("emailTxt"))

                  // Phone number text
                  Text(text = user.phoneNumber, modifier = Modifier.testTag("phoneNumberTxt"))

                  // User address
                  Text(
                      text = "${user.latitude}, ${user.longitude}",
                      modifier = Modifier.testTag("addressTxt"))

                  // Edit Button
                  ElevatedButton(
                      onClick = { showEditProfile = true },
                      border = BorderStroke(1f.dp, Color(0xFFA98467)),
                      modifier = Modifier.testTag("editProfileBtn")) {
                        // Text displayed on the button
                        Text("Edit Profile")
                      }
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
