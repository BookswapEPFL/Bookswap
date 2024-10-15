package com.android.bookswap.ui.profile

import android.location.Address
import android.util.Log
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
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.User
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(userVM: UserViewModel, modifier: Modifier = Modifier) {
  var user = userVM.getUser()
  var showEditProfile by remember { mutableStateOf(false) }

  var needRecompose by remember { mutableStateOf(false) }
  var mod = modifier

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
              phone = it.phoneNumber)
          showEditProfile = false
          needRecompose = true
        },
        user = user,
        modifier = Modifier.testTag("editProfileDialogue"))
  }

  LaunchedEffect(userVM.uid, needRecompose) {
    Log.d("LaunchedEffectTAG", "UserProfileScreen recomposed")
    user = userVM.getUser()
    needRecompose = false
  }

  BookSwapAppTheme() {

    // Scaffold to provide basic UI structure with a top app bar
    Scaffold(
        modifier = mod.testTag("profileScreenContainer"),
        topBar = {
          TopAppBar(
              modifier = mod.testTag("profileTopAppBar"),
              // Title of the screen
              title = { Text("Your Profile", modifier = mod.testTag("profileTitleTxt")) },
              // Icon button for navigation (currently no action defined)
              navigationIcon = {
                IconButton(modifier = mod.size(50.dp), onClick = {}) {
                  Icon(
                      imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                      contentDescription = "",
                      modifier = mod.size(30.dp))
                }
              })
        },
        content = { paddingValues ->
          // Column layout to stack input fields vertically with spacing
          Row(
              modifier = mod.padding(paddingValues).consumeWindowInsets(paddingValues),
              horizontalArrangement = Arrangement.spacedBy(5f.dp)) {
                Column(modifier = mod.fillMaxWidth(0.25f)) {
                  Box {
                    IconButton(onClick = { /*TODO*/}, modifier = mod.aspectRatio(1f)) {
                      Box(
                          modifier =
                              mod.padding(2.5f.dp)
                                  .border(3.5f.dp, Color(0xFFA98467), CircleShape)) {
                            Image(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "",
                                modifier = mod.fillMaxSize().scale(1.2f).clipToBounds(),
                                colorFilter = ColorFilter.tint(Color(0xFF6C584C)))
                          }
                      Box(
                          modifier = mod.fillMaxSize().padding(0f.dp),
                          contentAlignment = Alignment.TopEnd) {
                            IconButton(onClick = { /*TODO*/}, modifier = mod) {
                              Image(
                                  imageVector = Icons.Outlined.Edit,
                                  contentDescription = "",
                                  colorFilter = ColorFilter.tint(Color(0xFFAAAAAA)))
                            }
                          }
                    }
                  }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = mod.fillMaxHeight()) {
                      // Title Input Field
                      Text(
                          text = "${user.greeting} ${user.firstName} ${user.lastName}",
                          modifier = mod.testTag("fullNameTxt"))

                      // Author Input Field
                      Text(text = user.email, modifier = mod.testTag("emailTxt"))

                      // Description Input Field
                      Text(text = user.phoneNumber, modifier = mod.testTag("phoneNumberTxt"))

                      // User address
                      Text(
                          text =
                              "${user.address.getAddressLine(0)}," +
                                  " ${user.address.postalCode} ${user.address.locality} " +
                                  "${user.address.countryCode}, ${user.address.countryName}",
                          modifier = mod.testTag("addressTxt"))

                      // Save Button
                      Button(
                          onClick = {
                            showEditProfile = true
                            Log.d("click", "UserProfile: edit")
                          },
                          border = BorderStroke(1f.dp, Color(0xFFA98467)),
                          modifier = mod.testTag("editProfileBtn")) {
                            // Text displayed on the button
                            Text("Edit Profile")
                          }
                    }
              }
        })
  }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAD2)
@Composable
fun UserProfilePreview() {
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

  UserProfile(userVM)
}
