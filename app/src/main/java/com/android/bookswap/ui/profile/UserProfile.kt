package com.android.bookswap.ui.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.android.bookswap.R
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.model.UserBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.BookListComponent
import com.android.bookswap.ui.components.ButtonComponent
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
 * @param photoStorage The repository for storing photos.
 * @param booksRepository The repository for managing books.
 * @param userBookViewModel The ViewModel containing user book data. Defaults to a new
 *   UserBookViewModel instance.
 * @param navigationActions The navigation actions for navigating between screens.
 * @param topAppBar A composable function for the top app bar. Defaults to an empty composable.
 * @param bottomAppBar A composable function for the bottom app bar. Defaults to an empty
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
  val addressStr by appConfig.userViewModel.addressStr.collectAsState()
  val showEditPicture = remember { mutableStateOf(false) }
  var showEditProfile by remember { mutableStateOf(false) }

  var needRecompose by remember { mutableStateOf(false) }

  val bookListData = remember { mutableStateOf<List<DataBook>>(emptyList()) }
  var isBooksLoading by remember { mutableStateOf(true) }

  LaunchedEffect(userData.bookList) {
    val userBookList = userData.bookList
    for (book in userBookList) {
      Log.i("UserProfileScreen", "BookListUUID: $userBookList")
    }

    isBooksLoading = true
    try {
      bookListData.value = userBookViewModel.getBooks(userData.bookList)
    } catch (exception: Exception) {
      Log.e("UserProfileScreen", "Error fetching books: $exception")
    } finally {
      isBooksLoading = false
    }

    Log.i("UserProfileScreen", "DataBookList: $bookListData.value")
  }

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
                          Toast.makeText(
                                  context,
                                  context.getString(R.string.new_user_toast_error_upload),
                                  Toast.LENGTH_SHORT)
                              .show()
                        })
                  })
            },
            onFailure = { exception ->
              Log.e("NewUserScreen", "Error taking photo: $exception")
              Toast.makeText(
                      context,
                      context.getString(R.string.new_user_toast_error_taking),
                      Toast.LENGTH_SHORT)
                  .show()
            })
      }
  photoRequester.Init() // Initialize the photoRequester

  if (showEditProfile) {
    EditProfileDialog(
        context = context,
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

    Dialog(
        onDismissRequest = { showEditPicture.value = false },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
          Card(Modifier.testTag(C.Tag.UserProfile.profileImageBox).padding(PADDING)) {
            Column(
                Modifier.fillMaxWidth().padding(PADDING),
                Arrangement.Center,
                Alignment.CenterHorizontally) {
                  Text(stringResource(R.string.user_profile_edit_picture))
                  ButtonComponent(
                      { photoRequester.requestPhoto() },
                      Modifier.testTag(C.Tag.UserProfile.take_photo)) {
                        Text(stringResource(R.string.user_profile_take_photo))
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
        Column(modifier = Modifier.padding(it).fillMaxSize().background(ColorVariable.BackGround)) {
          Box(
              modifier = Modifier.fillMaxWidth(), // Ensure the Box takes up the full width
              contentAlignment = Alignment.Center // Center the content horizontally
              ) {
                IconButton(
                    onClick = { showEditPicture.value = true },
                    modifier =
                        Modifier.size(PROFILE_IMAGE_SIZE)
                            .clip(CircleShape)
                            .testTag(C.Tag.UserProfile.profileImage)) {
                      Box(
                          modifier =
                              Modifier.padding(ICON_PADDING)
                                  .border(PICTURE_BORDER_WIDTH, Color(0xFFA98467), CircleShape),
                          contentAlignment = Alignment.Center) {
                            // show either the profile picture or the default icon
                            if (userData.profilePictureUrl.isEmpty()) {
                              Image(
                                  imageVector = Icons.Rounded.AccountCircle,
                                  contentDescription = "No profile picture",
                                  modifier =
                                      Modifier
                                          // .fillMaxSize()
                                          .size(ICON_SIZE)
                                          .scale(ICON_SCALE)
                                          .clipToBounds(),
                                  colorFilter = ColorFilter.tint(Color(0xFF6C584C)))
                            } else {
                              AsyncImage(
                                  model = userData.profilePictureUrl,
                                  contentDescription = "profile picture",
                                  modifier =
                                      Modifier.fillMaxSize()
                                          .scale(PICTURE_SCALE)
                                          .clipToBounds()
                                          .clip(CircleShape))
                            }
                          }
                      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                        Image(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Color(0xFFAAAAAA)))
                      }
                    }
              }
          Spacer(modifier = Modifier.height(SPACER_HEIGHT_PIC_INFO))

          // Full name text
          LabeledTextUserProfile(
              testTag = C.Tag.OtherUserProfile.fullname,
              label = stringResource(R.string.user_profile_your_name),
              value = "${userData.greeting} ${userData.firstName} ${userData.lastName}")

          // Email text
          LabeledTextUserProfile(
              testTag = C.Tag.OtherUserProfile.email,
              label = stringResource(R.string.user_profile_your_email),
              value = userData.email)

          // Phone number text
          LabeledTextUserProfile(
              testTag = C.Tag.OtherUserProfile.phone,
              label = stringResource(R.string.user_profile_your_phone),
              value = userData.phoneNumber)
          // User address:
          LabeledTextUserProfile(
              testTag = C.Tag.OtherUserProfile.address,
              label = stringResource(R.string.user_profile_your_address),
              value = addressStr)

          Spacer(modifier = Modifier.height(SPACER_HEIGHT_INFO_EDIT))

          // Edit Button
          ButtonComponent(
              { showEditProfile = true },
              Modifier.testTag(C.Tag.UserProfile.edit).align(Alignment.CenterHorizontally)) {
                Text(stringResource(R.string.user_profile_edit))
              }

          // Book List
          if (isBooksLoading) {
            Log.i("UserProfileScreen", "Books are loading")
            CircularProgressIndicator(modifier = Modifier.padding(PADDING))
          } else if (bookListData.value.isEmpty()) {
            Log.e("UserProfileScreen", "No books available")
            Text(
                stringResource(R.string.user_profile_no_books),
                style = MaterialTheme.typography.bodyLarge)
          } else {
            Log.i("UserProfileScreen", "Displaying book list")
            BookListComponent(
                modifier = Modifier.fillMaxWidth().padding(PADDING),
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
