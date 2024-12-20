package com.android.bookswap.ui.books.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.android.bookswap.R
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.BookFromChatGPT
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.ButtonWithIcons
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

/**
 * Composable function to display the screen for choosing how to add a book.
 *
 * @param navController The navigation actions to handle navigation events.
 * @param topAppBar A composable function to display the top app bar.
 * @param bottomAppBar A composable function to display the bottom app bar.
 * @param photoFirebaseStorageRepository a repository allowing to upload photo as url
 * @param booksRepository book where to add the book
 */
@Composable
fun BookAdditionChoiceScreen(
    navController: NavigationActions,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {},
    photoFirebaseStorageRepository: PhotoFirebaseStorageRepository,
    booksRepository: BooksRepository
) {
  val columnPadding = 16.dp
  val context = LocalContext.current
  val buttonWidth = (LocalConfiguration.current.screenWidthDp.dp * (0.75f))

  val appConfig = LocalAppConfig.current
  // When the Photo is taken execute the lambda
  val photoRequester =
      PhotoRequester(LocalContext.current) { result ->
        if (result.isFailure) return@PhotoRequester
        // Send photo to viewmodel for chatgpt system
        BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository).addBookFromImage(
            // Display error message in a toast.
            result.getOrThrow().asAndroidBitmap(),
            appConfig.userViewModel.uuid,
        ) { error, uuid ->
          Toast.makeText(context, context.resources.getString(error.message), Toast.LENGTH_SHORT)
              .show()
          if (error == BookFromChatGPT.Companion.ErrorType.NONE) {
            navController.navigateTo(C.Screen.EDIT_BOOK, uuid!!.toString())
          }
        }
      }
  photoRequester.Init()
  Scaffold(
      modifier = Modifier.testTag(C.Tag.new_book_choice_screen_container),
      topBar = topAppBar,
      bottomBar = bottomAppBar) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .fillMaxSize()
                    .background(ColorVariable.BackGround)
                    .padding(bottom = 10f * columnPadding, top = columnPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              ButtonWithIcons(
                  text = stringResource(R.string.book_addition_choice_button_manually),
                  leftIcon = Icons.Default.Add,
                  leftIconPainter = null,
                  rightIcon = Icons.AutoMirrored.Filled.ArrowForward,
                  onClick = { navController.navigateTo(C.Screen.ADD_BOOK_MANUALLY) },
                  buttonWidth = buttonWidth)
              Spacer(modifier = Modifier.height(2f * columnPadding))
              ButtonWithIcons(
                  text = stringResource(R.string.book_addition_choice_button_from_isbn),
                  leftIcon = null,
                  leftIconPainter = painterResource(id = R.drawable.download),
                  rightIcon = Icons.AutoMirrored.Filled.ArrowForward,
                  onClick = { navController.navigateTo(C.Screen.ADD_BOOK_ISBN) },
                  buttonWidth = buttonWidth)
              Spacer(modifier = Modifier.height(2f * columnPadding))
              ButtonWithIcons(
                  text = stringResource(R.string.book_addition_choice_button_from_photo),
                  leftIcon = null,
                  leftIconPainter = painterResource(id = R.drawable.photoicon),
                  rightIcon = Icons.AutoMirrored.Filled.ArrowForward,
                  onClick = { photoRequester.requestPhoto() },
                  buttonWidth = buttonWidth,
              )
            }
      }
}
