package com.android.bookswap.ui.profile

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.OthersUserViewModel
import com.android.bookswap.model.UserBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.BookListComponent
import com.android.bookswap.ui.theme.ColorVariable
import java.util.UUID

/** Constants * */
private val PROFILE_PICTURE_SIZE = 90.dp
private val PROFILE_PICTURE_BORDER_WIDTH = 3.dp
private val ICON_SIZE = 80.dp
private val PADDING = 16.dp
private val ITEM_SPACING = 8.dp

/**
 * Composable function to display the user profile screen.
 *
 * @param userId The UUID of the user whose profile is to be displayed.
 * @param otherUserVM The ViewModel containing user data. Defaults to a new UserViewModel instance.
 * @param topAppBar A composable function to display the top app bar. Defaults to an empty
 *   composable.
 * @param bottomAppBar A composable function to display the bottom app bar. Defaults to an empty
 *   composable.
 */
@Composable
fun OthersUserProfileScreen(
    userId: UUID,
    otherUserVM: OthersUserViewModel = OthersUserViewModel(userId),
    booksRepository: BooksRepository,
    userBookViewModel: UserBookViewModel = UserBookViewModel(booksRepository),
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {

  var user by remember { mutableStateOf(DataUser()) }
  var isLoading by remember { mutableStateOf(true) }

  // I think it is better (good thing) to use LaunchedEffect
  LaunchedEffect(userId) {
    isLoading = true
    otherUserVM.getUserByUUID(userId, callback = { user = it ?: DataUser() })
    Log.e("FetchedUser", "Name: ${user.firstName}")
    isLoading = false
  }

  val bookListData = remember { mutableStateOf<List<DataBook>>(emptyList()) }
  var isBooksLoading by remember { mutableStateOf(true) }

  LaunchedEffect(user.bookList) {
    val userBookList = user.bookList
    for (book in userBookList) {
      Log.e("OtherUserProfileScreen", "BookListUUID: $userBookList")
    }

    isBooksLoading = true
    try {
      bookListData.value = userBookViewModel.getBooks(user.bookList)
    } catch (exception: Exception) {
      Log.e("OtherUserProfileScreen", "Error fetching books: $exception")
    } finally {
      isBooksLoading = false
    }
  }

  Log.e("OtherUserProfileScreen", "BookListDataBook: ${bookListData.value}")

  Scaffold(
      modifier = Modifier.testTag(C.Tag.other_user_profile_screen_container),
      topBar = topAppBar,
      bottomBar = bottomAppBar) { padding ->
        if (isLoading) {
          Box(
              modifier = Modifier.fillMaxSize().padding(padding),
              contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // To show it is loading
                Log.e("OtherUserProfileScreen", "Loading") // log to see on logcat
          }
        } else {
          Column(
              modifier =
                  Modifier.padding(padding).fillMaxSize().background(ColorVariable.BackGround),
              verticalArrangement = Arrangement.spacedBy(ITEM_SPACING),
              horizontalAlignment = Alignment.CenterHorizontally) {
                // Profile Picture
                Box(
                    modifier =
                        Modifier.padding(PADDING)
                            .size(PROFILE_PICTURE_SIZE)
                            .border(PROFILE_PICTURE_BORDER_WIDTH, ColorVariable.Accent, CircleShape)
                            .background(ColorVariable.AccentSecondary, CircleShape),
                    contentAlignment = Alignment.Center) {
                      if (user.profilePictureUrl.isNotEmpty()) {
                        // Replace with an image loader like Coil or Glide if required
                        Text("Profile Picture Placeholder")
                      } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(ICON_SIZE),
                            tint = ColorVariable.Accent)
                      }
                    }

                // Full Name:
                LabeledText(testTag = C.Tag.OtherUserProfile.fullname, label = "Name:", value = "${user.greeting} ${user.firstName} ${user.lastName}")

                // Email:
                LabeledText(testTag = C.Tag.OtherUserProfile.email, label = "Email:", value = user.email)

                // Phone Number:
                LabeledText(testTag = C.Tag.OtherUserProfile.phone, label = "Phone:", value = user.phoneNumber)

                // Address:
                LabeledText(testTag = C.Tag.OtherUserProfile.address, label = "Address:", value = "${user.latitude}, ${user.longitude}")

                // Book List
                if (isBooksLoading) {
                  Log.e("OtherUserProfileScreen", "Books are loading")
                  CircularProgressIndicator(modifier = Modifier.padding(PADDING))
                } else {
                  Log.e("OtherUserProfileScreen", "Displaying book list")
                  BookListComponent(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(PADDING), // background(Color.LightGray) // Debug background
                      // .border(2.dp, Color.Red),    // Debug border,
                      bookList = bookListData.value)
                }
              }
        }
      }
}

/** Constant * */
private const val LABEL_WEIGHT = 0.5f
private const val VALUE_WEIGHT = 2f
private val BORDER_WIDTH = 1.dp
private val PADDING_SMALL = 4.dp

/**
 * A composable function to display a labeled text field.
 *
 * @param label The label for the field (e.g., "Email:").
 * @param value The value of the field.
 */
@Composable
fun LabeledText(testTag: String = "LabeledText", label: String, value: String) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .background(ColorVariable.Secondary, shape = MaterialTheme.shapes.small)
              .border(BORDER_WIDTH, ColorVariable.Accent, shape = MaterialTheme.shapes.small)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(PADDING_SMALL),
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = label,
                  color = ColorVariable.Accent,
                  style = MaterialTheme.typography.labelLarge,
                  modifier = Modifier.weight(LABEL_WEIGHT).testTag(testTag + C.Tag.LabeledText.label))
              Text(
                  text = value,
                  color = ColorVariable.Accent,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.weight(VALUE_WEIGHT).testTag(testTag + C.Tag.LabeledText.text))
            }
      }
}
