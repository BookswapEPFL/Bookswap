package com.android.bookswap.ui.profile

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.DataUser
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.components.BookListComponent
import java.util.UUID


/**
 * Composable function to display the user profile screen.
 *
 * @param userId The UUID of the user whose profile is to be displayed.
 * @param userVM The ViewModel containing user data. Defaults to a new UserViewModel instance.
 * @param topAppBar A composable function to display the top app bar. Defaults to an empty
 *   composable.
 * @param bottomAppBar A composable function to display the bottom app bar. Defaults to an empty
 *   composable.
 */
@Composable
fun OthersUserProfileScreen(
    userId: UUID,
    userVM: UserViewModel = UserViewModel(userId),
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
    //var user = userVM.getUser()
    var user by remember { mutableStateOf(DataUser()) }
    var isLoading by remember { mutableStateOf(true) }

    //I think it is better (good thing) to use LaunchedEffect
    LaunchedEffect(userId) {
        isLoading = true
        user = userVM.getUser(force = true)
        isLoading = false
    }

    Scaffold(
        modifier = Modifier.testTag("OtherUserProfileScreen"),
        topBar = topAppBar,
        bottomBar = bottomAppBar
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator() // To show it is loading
                Log.e("OtherUserProfileScreen", "Loading") //log to see on logcat
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(120.dp)
                        .border(3.dp, Color(0xFFA98467), CircleShape)
                        .background(Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.profilePictureUrl.isNotEmpty()) {
                        // Replace with an image loader like Coil or Glide if required
                        Text("Profile Picture Placeholder")
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = Color.Gray
                        )
                    }
                }
                Column(Modifier.fillMaxHeight().fillMaxWidth(), Arrangement.spacedBy(8.dp)) {

                    // Name:
                    Text(
                        text = "${user.greeting} ${user.firstName} ${user.lastName}",
                        modifier = Modifier.testTag("otherUserFullNameTxt")
                    )

                    //Email:
                    Text(
                        text = user.email,
                        modifier = Modifier.testTag("otherUserEmailTxt")
                    )

                    // Phone number:
                    Text(
                        text = user.phoneNumber,
                        modifier = Modifier.testTag("otherUserPhoneNumberTxt")
                    )

                    // User address:
                    Text(
                        text = "${user.latitude}, ${user.longitude}",
                        modifier = Modifier.testTag("otherUserAddressTxt")
                    )
                }

                // BookList:
                BookListComponent(
                    modifier = Modifier.testTag("otherUserBookList"),
                    bookList = user.bookList,
                    //bc it is a list of UUIDs I maybe need to retrieve each book before
                    //or maybe the retrieval of the books should be done in the booklist composable
                )
            }
        }
    }
}
