package com.android.bookswap.ui.books

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.bookswap.R
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.drawVerticalScrollbar
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import java.util.UUID

/** Constant values used in the BookProfileScreen. */
const val COLUMN_WEIGHT = 1f
val COLUMN_PADDING = 8.dp
val HORIZONTAL_PADDING = 16.dp
val TOP_PADDING = 2.dp
const val SCROLLBAR_PADDING = 12f
const val MAX_LINES = 15
const val BORDER_WIDTH = 1f
const val HALF_WIDTH = 0.5f

/**
 * Composable function to display the profile screen of a book.
 *
 * @param DataBook The data object containing book details.
 * @param navController The navigation actions for navigating between screens.
 * @param topAppBar A composable function to display the top app bar.
 * @param bottomAppBar A composable function to display the bottom app bar.
 */
@Composable
fun BookProfileScreen(
    bookId: UUID,
    booksRepository: BooksRepository,
    navController: NavigationActions,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {

  val pictureWidth = (LocalConfiguration.current.screenWidthDp.dp * (0.60f))
  val pictureHeight = pictureWidth * 1.41f
  val image = R.drawable.isabellacatolica
  val imageDescription = "Isabel La Catolica"

  val appConfig = LocalAppConfig.current
  // State to hold the book data and loading status
  val (dataBook, setDataBook) = remember { mutableStateOf<DataBook?>(null) }
  val (isLoading, setLoading) = remember { mutableStateOf(true) }
  val (error, setError) = remember { mutableStateOf<Exception?>(null) }
  // Fetch the book data
  LaunchedEffect(bookId) {
    booksRepository.getBook(
        uuid = bookId,
        OnSucess = { book ->
          setDataBook(book)
          setLoading(false)
        },
        onFailure = { exception ->
          setError(exception)
          setLoading(false)
        })
  }
  Scaffold(
      modifier = Modifier.testTag(C.Tag.book_profile_screen_container),
      topBar = topAppBar,
      bottomBar = bottomAppBar) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(ColorVariable.BackGround)
                    .testTag(C.Tag.BookProfile.scrollable),
            verticalArrangement = Arrangement.spacedBy(COLUMN_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally) {
              when {
                isLoading -> {
                  item {
                    // Display a loading indicator
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                      CircularProgressIndicator()
                    }
                  }
                }
                error != null -> {
                  item {
                    // Display an error message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                      Text(
                          text =
                              stringResource(R.string.book_profile_error_loading) +
                                  "${error.message}",
                          color = Color.Red)
                    }
                  }
                }
                dataBook != null -> {
                  item {
                    Text(
                        text = dataBook.title,
                        modifier =
                            Modifier.testTag(C.Tag.BookProfile.title).padding(COLUMN_PADDING),
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.titleLarge)
                  }
                  item {
                    Text(
                        text =
                            dataBook.author ?: stringResource(R.string.book_profile_author_unknown),
                        modifier = Modifier.testTag(C.Tag.BookProfile.author),
                        color = ColorVariable.AccentSecondary,
                        style = MaterialTheme.typography.titleMedium)
                  }
                  item { Spacer(modifier = Modifier.height(COLUMN_PADDING)) }
                  item {
                    Box(
                        modifier =
                            Modifier.size(pictureWidth, pictureHeight)
                                .background(ColorVariable.BackGround)) {
                          if (dataBook.photo?.isNotEmpty() == true) {
                            AsyncImage(
                                model = dataBook.photo,
                                contentDescription = dataBook.title,
                                modifier =
                                    Modifier.height(pictureHeight)
                                        .fillMaxWidth()
                                        .testTag(C.Tag.BookProfile.image))
                          } else {
                            Image(
                                painter = painterResource(id = image),
                                contentDescription = imageDescription,
                                modifier =
                                    Modifier.height(pictureHeight)
                                        .fillMaxWidth()
                                        .testTag(C.Tag.BookProfile.imagePlaceholder))
                          }
                        }
                  }
                  item { Spacer(modifier = Modifier.height(COLUMN_PADDING)) }
                  item {
                    dataBook.rating?.let {
                      Text(
                          text = stringResource(R.string.book_profile_rating_label) + " $it/5",
                          color = ColorVariable.Accent,
                          style = MaterialTheme.typography.bodyMedium,
                          modifier = Modifier.testTag(C.Tag.BookProfile.rating))
                      Spacer(modifier = Modifier.height(COLUMN_PADDING))
                    }
                  }

                  item {
                    Text(
                        text = stringResource(R.string.book_profile_synopsis_label),
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.testTag(C.Tag.BookProfile.synopsis_label))
                  }
                  item { BookSynopsis(description = dataBook.description ?: "") }
                  item { Spacer(modifier = Modifier.height(COLUMN_PADDING)) }
                  item {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING)) {
                          Column(modifier = Modifier.weight(COLUMN_WEIGHT)) {
                            ProfileText(
                                text =
                                    stringResource(R.string.book_profile_language_label) +
                                        " " +
                                        dataBook.language.languageName,
                                testTag = C.Tag.BookProfile.language)
                            ProfileText(
                                text = stringResource(R.string.book_profile_genres_label),
                                testTag = C.Tag.BookProfile.genres)
                            dataBook.genres.forEach { genre ->
                              Text(
                                  text = "- ${genre.Genre}",
                                  color = ColorVariable.AccentSecondary,
                                  style = MaterialTheme.typography.bodyMedium,
                                  modifier =
                                      Modifier.padding(
                                              top = TOP_PADDING, start = HORIZONTAL_PADDING)
                                          .testTag(genre.Genre + C.Tag.BookProfile.genre))
                            }
                            ProfileText(
                                text =
                                    stringResource(R.string.book_profile_isbn_label) +
                                        " ${dataBook.isbn ?: stringResource(R.string.book_profile_isbn_missing)}",
                                testTag = C.Tag.BookProfile.isbn)
                          }

                          Column(modifier = Modifier.weight(COLUMN_WEIGHT)) {
                            ProfileText(
                                text = stringResource(R.string.book_profile_date_label),
                                testTag = C.Tag.BookProfile.date)
                            ProfileText(
                                text = stringResource(R.string.book_profile_volume_label),
                                testTag = C.Tag.BookProfile.volume)
                            ProfileText(
                                text = stringResource(R.string.book_profile_issue_label),
                                testTag = C.Tag.BookProfile.issue)
                            ProfileText(
                                text = stringResource(R.string.book_profile_editorial_label),
                                testTag = C.Tag.BookProfile.editorial)
                            ProfileText(
                                text = stringResource(R.string.book_profile_location_label),
                                testTag = C.Tag.BookProfile.location)
                          }
                        }
                  }
                  // Conditionally display the "Edit Book" or "Go to User" button if the current
                  // user owns the book or not
                  item {
                    Button(
                        colors =
                            ButtonColors(
                                ColorVariable.Secondary,
                                ColorVariable.Accent,
                                ColorVariable.Secondary,
                                ColorVariable.Accent),
                        border = BorderStroke(BORDER_WIDTH.dp, ColorVariable.Accent),
                        onClick = {
                          if (dataBook.userId == appConfig.userViewModel.getUser().userUUID) {
                            navController.navigateTo(C.Screen.EDIT_BOOK, dataBook.uuid.toString())
                          } else {
                            navController.navigateTo(
                                C.Screen.OTHERS_USER_PROFILE, dataBook.userId.toString())
                          }
                        },
                        modifier =
                            Modifier.padding(COLUMN_PADDING)
                                .testTag(C.Tag.BookProfile.edit)
                                .fillMaxWidth(HALF_WIDTH)) {
                          Text(
                              if (dataBook.userId == appConfig.userViewModel.getUser().userUUID) {
                                stringResource(R.string.book_profile_edit_button)
                              } else {
                                stringResource(R.string.book_profile_go_to_user_button)
                              })
                        }
                  }
                }
              }
            }
      }
}
/**
 * Composable function to display a text with a specific style and test tag.
 *
 * @param text The text to be displayed.
 * @param testTag The test tag for the text composable.
 */
@Composable
fun ProfileText(text: String, testTag: String) {
  Text(
      text = text,
      color = ColorVariable.Accent,
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.padding(vertical = COLUMN_PADDING).testTag(testTag))
}

/**
 * Composable function to display the synopsis of a book. The synopsis is scrollable if it exceeds a
 * certain number of lines.
 *
 * @param description The description of the book.
 */
@Composable
fun BookSynopsis(description: String) {
  val scrollState = rememberScrollState()

  Box(
      modifier =
          Modifier.heightIn(
                  max =
                      with(LocalDensity.current) {
                        MaterialTheme.typography.bodyMedium.lineHeight.toDp() * MAX_LINES
                      })
              .verticalScroll(scrollState)
              .drawVerticalScrollbar(scrollState, paddingEnd = SCROLLBAR_PADDING)
              .padding(start = HORIZONTAL_PADDING, end = HORIZONTAL_PADDING)
              .testTag(C.Tag.BookProfile.synopsis)) {
        Text(
            text = description.ifEmpty { stringResource(R.string.book_profile_synopsis_empty) },
            color = ColorVariable.Accent,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start)
      }
}
