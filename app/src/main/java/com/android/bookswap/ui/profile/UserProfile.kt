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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
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
  var mod = modifier.padding(0.dp)

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
        user)
  }

  LaunchedEffect(userVM.uid, needRecompose) {
    Log.d("LaunchedEffectTAG", "UserProfileScreen recomposed")
    user = userVM.getUser()
    needRecompose = false
  }

  BookSwapAppTheme() {
    // Scaffold to provide basic UI structure with a top app bar
    Scaffold(
        modifier = mod.testTag("profileScreenContainer").padding(0.dp),
        topBar = {
          TopAppBar(
              modifier = mod,
              // Title of the screen
              title = { Text("Your Profile", modifier = mod.testTag("profileTitleText")) },
              // Icon button for navigation (currently no action defined)
              navigationIcon = {
                IconButton(modifier = mod.size(50.dp), onClick = {}) {
                  Column(
                      modifier = mod.fillMaxSize(),
                      Arrangement.Center,
                      Alignment.CenterHorizontally) {
                        Text(text = "", modifier = mod.fillMaxWidth(), textAlign = TextAlign.Center)
                        // You can add an icon here for the button
                      }
                }
              })
        },
        content = { paddingValues ->
          // Column layout to stack input fields vertically with spacing
          Row(modifier = mod.padding(paddingValues).consumeWindowInsets(paddingValues)) {
            Column(modifier = mod.fillMaxWidth(0.25f)) {
              Box(modifier = mod.aspectRatio(1f), contentAlignment = Alignment.Center) {}
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp), modifier = mod.fillMaxHeight()) {
                  // Title Input Field
                  Text(
                      text = "${user.greeting} ${user.firstName} ${user.lastName}",
                      modifier = mod.testTag("fullNameText"))

                  // Author Input Field
                  Text(text = user.email, modifier = mod.testTag("emailText"))

                  // Description Input Field
                  Text(text = user.phoneNumber, modifier = mod.testTag("phoneNumberText"))

                  // User address
                  Text(
                      text =
                          "${user.address.getAddressLine(0)}," +
                              " ${user.address.postalCode} ${user.address.locality} " +
                              "${user.address.countryCode}, ${user.address.countryName}",
                      modifier = mod.testTag("addressText"))

                  // Save Button
                  Button(
                      onClick = {},
                      // Enable the button only if title and ISBN are filled and photo is blank
                      enabled = true,
                      modifier = mod.testTag("editProfileBtn")) {
                        // Text displayed on the button
                        Text("Edit Profile", modifier = mod.testTag("editProfileBtnTxt"))
                      }
                }
          }
        })
  }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
  var address = Address(Locale.getDefault())
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
