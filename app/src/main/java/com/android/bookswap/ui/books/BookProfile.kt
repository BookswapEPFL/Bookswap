package com.android.bookswap.ui.books

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.bookswap.R
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import junit.framework.TestCase.assertTrue
import java.util.UUID

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
  val columnPadding = 8.dp
  val pictureWidth = (LocalConfiguration.current.screenWidthDp.dp * (0.60f))
  val pictureHeight = pictureWidth * 1.41f
  val buttonsHeight = pictureHeight / 12.0f
  val images = listOf(R.drawable.isabellacatolica, R.drawable.felipeii)
  val imagesDescription = listOf("Isabel La Catolica", "Felipe II")
  var currentImageIndex by remember { mutableIntStateOf(0) }

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
            verticalArrangement = Arrangement.spacedBy(columnPadding),
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
                      Text(text = "Error loading book details: ${error.message}", color = Color.Red)
                    }
                  }
                }
                dataBook != null -> {
                  item {
                    Text(
                        text = dataBook.title,
                        modifier = Modifier.testTag(C.Tag.BookProfile.title).padding(columnPadding),
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.titleLarge)
                  }
                  item {
                    Text(
                        text = dataBook.author ?: "Author Unknown",
                        modifier = Modifier.testTag(C.Tag.BookProfile.author),
                        color = ColorVariable.AccentSecondary,
                        style = MaterialTheme.typography.titleMedium)
                  }
                  item { Spacer(modifier = Modifier.height(columnPadding)) }
                  item {
                    Box(
                        modifier =
                            Modifier.size(pictureWidth, pictureHeight)
                                .background(ColorVariable.BackGround)) {
                          Image(
                              painter = painterResource(id = images[currentImageIndex]),
                              contentDescription = imagesDescription[currentImageIndex],
                              modifier =
                                  Modifier.height(pictureHeight)
                                      .fillMaxWidth()
                                      .testTag("${currentImageIndex}_" + C.Tag.BookProfile.image))
                        }
                  }
                  item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                          IconButton(
                              onClick = {
                                currentImageIndex =
                                    (currentImageIndex - 1 + images.size) % images.size
                              },
                              modifier =
                                  Modifier.height(buttonsHeight)
                                      .testTag(C.Tag.BookProfile.previous_image)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Image",
                                    tint = ColorVariable.Accent)
                              }
                          Text(
                              text = imagesDescription[currentImageIndex],
                              color = ColorVariable.AccentSecondary,
                              modifier = Modifier.padding(horizontal = 8.dp))
                          IconButton(
                              onClick = {
                                currentImageIndex = (currentImageIndex + 1) % images.size
                              },
                              modifier =
                                  Modifier.height(buttonsHeight)
                                      .testTag(C.Tag.BookProfile.next_image)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Image",
                                    tint = ColorVariable.Accent)
                              }
                        }
                  }
                  item { Spacer(modifier = Modifier.height(columnPadding)) }
                  item {
                    dataBook.rating?.let {
                      Text(
                          text = "Rating: $it/10",
                          color = ColorVariable.Accent,
                          style = MaterialTheme.typography.bodyMedium,
                          modifier =
                              Modifier.padding(vertical = 8.dp).testTag(C.Tag.BookProfile.rating))
                    }
                  }
                  item { Spacer(modifier = Modifier.height(columnPadding)) }
                  item {
                    Text(
                        text = "Synopsis",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.titleSmall,
                        modifier =
                            Modifier.padding(vertical = 8.dp)
                                .testTag(C.Tag.BookProfile.synopsis_label))
                  }
                  item {
                    Text(
                        text = dataBook.description ?: "No description available",
                        color = ColorVariable.Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier.padding(vertical = 8.dp).testTag(C.Tag.BookProfile.synopsis),
                        textAlign = TextAlign.Center)
                  }
                  item { Spacer(modifier = Modifier.height(columnPadding)) }
                  item {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                      Column(modifier = Modifier.weight(1f)) {
                        ProfileText(
                            text = "Language: ${dataBook.language.languageCode}",
                            testTag = C.Tag.BookProfile.language)
                        ProfileText(text = "Genres:", testTag = C.Tag.BookProfile.genres)
                        dataBook.genres.forEach { genre ->
                          Text(
                              text = "- ${genre.Genre}",
                              color = ColorVariable.AccentSecondary,
                              style = MaterialTheme.typography.bodyMedium,
                              modifier =
                                  Modifier.padding(top = 2.dp, start = 16.dp)
                                      .testTag(genre.Genre + C.Tag.BookProfile.genre))
                        }
                        ProfileText(
                            text =
                                "ISBN: ${dataBook.isbn ?: "ISBN doesn't exist or is not available"}",
                            testTag = C.Tag.BookProfile.isbn)
                      }

                      VerticalDivider(color = ColorVariable.Accent, thickness = 1.dp)

                      Column(modifier = Modifier.weight(1f)) {
                        ProfileText(
                            text = "Date of Publication: [Temporary Date]",
                            testTag = C.Tag.BookProfile.date)
                        ProfileText(
                            text = "Volume: [Temporary Volume]", testTag = C.Tag.BookProfile.volume)
                        ProfileText(
                            text = "Issue: [Temporary Issue]", testTag = C.Tag.BookProfile.issue)
                        ProfileText(
                            text = "Editorial: [Temporary Editorial]",
                            testTag = C.Tag.BookProfile.editorial)
                        ProfileText(
                            text = "Place of Edition: [Temporary Place]",
                            testTag = C.Tag.BookProfile.location)
                      }
                    }
                  }
                  // Conditionally display the "Edit Book" button if the current user owns the book
                  if (dataBook.userId == appConfig.userViewModel.uuid) {
                    item { Spacer(modifier = Modifier.height(columnPadding)) }
                    item {
                      ButtonComponent(
                          onClick = {
                            navController.navigateTo(C.Screen.EDIT_BOOK, dataBook.uuid.toString())
                          },
                          modifier = Modifier.padding(8.dp).testTag(C.Tag.BookProfile.edit)) {
                            Text("Edit Book")
                          }
                    }
                  }
                  item {
                    Spacer(
                        modifier = Modifier.height(0.dp).testTag(C.Tag.BookProfile.scrollable_end))
                  }
                    item {
                        ButtonComponent(
                            onClick = {
                            booksRepository.exchangeBook(dataBook, currentUserId){
                                Toast.makeText(LocalContext.current, "Book exchanged successfully", Toast.LENGTH_SHORT).show()}
                            },
                            modifier = Modifier.padding(8.dp).testTag(C.Tag.BookProfile.edit)) {
                            Text("Exchange Book")

                        }
                    }
                    item {
                        ButtonComponent(
                            onClick = {
                                booksRepository.moveBookToArchive(dataBook)
                                Toast.makeText(LocalContext.current, "Book archived successfully", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.padding(8.dp).testTag(C.Tag.BookProfile.edit)) {
                            Text("Archived Book")

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
      modifier = Modifier.padding(vertical = 8.dp).testTag(testTag))
}
