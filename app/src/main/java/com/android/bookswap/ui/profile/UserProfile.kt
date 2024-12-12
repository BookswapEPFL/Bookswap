package com.android.bookswap.ui.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.model.UserBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.BookListComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.*

/** Constants * */
private val PADDING = 16.dp
private val PROFILE_IMAGE_SIZE = 90.dp
private val PICTURE_BORDER_WIDTH = 3.5f.dp
private val PICTURE_SCALE = 1.2f
private val ICON_SIZE = 100.dp
private val ICON_PADDING = 2.5f.dp
private val ICON_SCALE = 1.2f
private val SPACER_HEIGHT_INFO_EDIT = 8.dp
private val SPACER_HEIGHT_PIC_INFO = 6.dp

/**
 * Composable function to display the user profile screen.
 *
 * @param userVM The ViewModel containing user data. Defaults to a new UserViewModel instance.
 * @param topAppBar A composable function to display the top app bar. Defaults to an empty
 *   composable.
 * @param bottomAppBar A composable function to display the bottom app bar. Defaults to an empty
 *   composable.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfile(
    photoStorage: PhotoFirebaseStorageRepository,
    booksRepository: BooksRepository,
    userBookViewModel: UserBookViewModel = UserBookViewModel(booksRepository),
    navigationActions: NavigationActions,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  val context = LocalContext.current
  val appConfig = LocalAppConfig.current
  var userData = appConfig.userViewModel.getUser()
  val showEditPicture = remember { mutableStateOf(false) }
  var showEditProfile by remember { mutableStateOf(false) }
  var needRecompose by remember { mutableStateOf(false) }

  val bookListData = remember { mutableStateOf<List<DataBook>>(emptyList()) }
  var isBooksLoading by remember { mutableStateOf(true) }

  LaunchedEffect(userData.bookList) {
    try {
      isBooksLoading = true
      bookListData.value = userBookViewModel.getBooks(userData.bookList)
    } catch (exception: Exception) {
      Log.e("UserProfileScreen", "Error fetching books: $exception")
    } finally {
      isBooksLoading = false
    }
  }

  val photoRequester =
      PhotoRequester(context) { result ->
        result.fold(
            onSuccess = { image ->
              photoStorage.addPhotoToStorage(
                  photoId = "profile", bitmap = image.asAndroidBitmap()) { uploadResult ->
                    uploadResult.fold(
                        onSuccess = { url ->
                          appConfig.userViewModel.updateUser(picURL = url)
                          showEditPicture.value = false
                        },
                        onFailure = {
                          Toast.makeText(context, "Error uploading photo", Toast.LENGTH_SHORT)
                              .show()
                        })
                  }
            },
            onFailure = {
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
          appConfig.userViewModel.updateUser(
              greeting = it.greeting,
              firstName = it.firstName,
              lastName = it.lastName,
              email = it.email,
              phone = it.phoneNumber,
              latitude = userData.latitude,
              longitude = userData.longitude,
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
    Dialog(onDismissRequest = { showEditPicture.value = false }) {
      Card(Modifier.padding(PADDING)) {
        Column(
            modifier = Modifier.padding(PADDING),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Edit Profile Picture")
              Button(onClick = { photoRequester.requestPhoto() }) { Text("Take Photo") }
            }
      }
    }
  }

  Scaffold(topBar = topAppBar, bottomBar = bottomAppBar) {
    Column(modifier = Modifier.fillMaxSize().background(ColorVariable.BackGround).padding(it)) {
      Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(
            onClick = { showEditPicture.value = true },
            modifier = Modifier.size(PROFILE_IMAGE_SIZE).clip(CircleShape)) {
              if (userData.profilePictureUrl.isEmpty()) {
                Image(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Default profile picture",
                    modifier = Modifier.size(ICON_SIZE).scale(ICON_SCALE))
              } else {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize().scale(PICTURE_SCALE).clip(CircleShape))
              }
            }
      }

      Spacer(modifier = Modifier.height(SPACER_HEIGHT_PIC_INFO))

      // User Information
      LabeledTextUserProfile(
          label = "Your Name:", value = "${userData.firstName} ${userData.lastName}")
      LabeledTextUserProfile(label = "Email:", value = userData.email)
      LabeledTextUserProfile(label = "Phone:", value = userData.phoneNumber)
      LabeledTextUserProfile(
          label = "Address:", value = "${userData.latitude}, ${userData.longitude}")

      Spacer(modifier = Modifier.height(SPACER_HEIGHT_INFO_EDIT))

      // Edit Profile Button
      Button(onClick = { showEditProfile = true }) { Text("Edit Profile") }

      Spacer(modifier = Modifier.height(20.dp))

      // Disconnect Button
      Button(
          onClick = {
            appConfig.userViewModel.disconnectUser()
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
            navigationActions.navigateTo(C.Screen.AUTH)
          },
          modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Disconnect")
          }

      // Book List
      if (isBooksLoading) {
        CircularProgressIndicator(Modifier.padding(PADDING))
      } else if (bookListData.value.isEmpty()) {
        Text("No books available", style = MaterialTheme.typography.bodyLarge)
      } else {
        BookListComponent(
            bookList = bookListData.value,
            onBookClick = { bookId ->
              navigationActions.navigateTo("${C.Screen.BOOK_PROFILE}/$bookId")
            })
      }
    }
  }
}

/** Constant * */
private const val LABEL_WEIGHT = 0.75f
private const val VALUE_WEIGHT = 2f
private val BORDER_WIDTH = 2.dp
private val ROW_PADDING = 4.dp
private val BOX_PADDING = 2.dp

/**
 * A composable function to display a labeled text field.
 *
 * @param label The label for the field (e.g., "Email:").
 * @param value The value of the field.
 */
@Composable
fun LabeledTextUserProfile(testTag: String = "LabeledText", label: String, value: String) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .padding(BOX_PADDING)
              .background(ColorVariable.Secondary, shape = MaterialTheme.shapes.small)
              .border(BORDER_WIDTH, ColorVariable.Accent, shape = MaterialTheme.shapes.small)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(ROW_PADDING),
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = label,
                  color = ColorVariable.Accent,
                  style = MaterialTheme.typography.labelLarge,
                  modifier =
                      Modifier.weight(LABEL_WEIGHT).testTag(testTag + C.Tag.LabeledText.label))
              Text(
                  text = value,
                  color = ColorVariable.Accent,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier =
                      Modifier.weight(VALUE_WEIGHT).testTag(testTag + C.Tag.LabeledText.text))
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
