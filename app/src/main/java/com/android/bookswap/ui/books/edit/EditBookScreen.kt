package com.android.bookswap.ui.books.edit

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.InputVerification
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.EntriesListBookComponent
import com.android.bookswap.ui.theme.ColorVariable
import java.util.UUID

private const val WIDTH_BUTTON = 0.45f
private const val SPACE_BETWEEN_BUTTON = 0.05f / (1f - WIDTH_BUTTON)

/**
 * Composable function to display the Edit Book screen.
 *
 * @param viewModel Manages the updating and deleting of the books
 * @param navigationActions The navigation actions to handle navigation events.
 * @param bookUUID The uuid of the book data to be edited.
 */
@Composable
fun EditBookScreen(
    viewModel: EditBookViewModel,
    photoStorage: PhotoFirebaseStorageRepository,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {},
    bookUUID: UUID
) {
  var bookMutable by remember { mutableStateOf<DataBook?>(null) }

  // Request book when screen load
  LaunchedEffect(Unit) {
    viewModel.getBook(
        uuid = bookUUID,
        onSuccess = { resultBook -> bookMutable = resultBook },
        onFailure = { Log.e("EditScreen", "Error while loading the book") })
  }

  when (bookMutable) {
    null -> CircularProgressIndicator() // If the book has not loaded, show circular loading
    else -> {
      // Get book when book has been successfully been requested
      val book = bookMutable!!
      val title = remember { mutableStateOf(book.title) }
      val author = remember { mutableStateOf(book.author ?: "") }
      val description = remember { mutableStateOf(book.description ?: "") }
      val rating = remember { mutableStateOf(book.rating?.toString() ?: "") }
      val isbn = remember { mutableStateOf(book.isbn ?: "") }
      val photo = remember { mutableStateOf(book.photo ?: "") }
      val language = remember { mutableStateOf(book.language) }
      val genres = remember { mutableStateOf(book.genres) }

      val context = LocalContext.current
      val inputVerification = InputVerification()

      Scaffold(
          modifier =
              Modifier.testTag(C.Tag.edit_book_screen_container)
                  .background(ColorVariable.BackGround),
          containerColor = ColorVariable.BackGround, // Sets entire Scaffold background color
          topBar = topAppBar,
          bottomBar = bottomAppBar,
          content = { paddingValues ->
            EntriesListBookComponent(
                paddingValues,
                title,
                genres,
                author,
                description,
                rating,
                isbn,
                photo,
                language,
                photoStorage,
                buttons = { modifier ->
                  Row(modifier.fillMaxWidth(0.5f)) {
                    ButtonComponent(
                        modifier = Modifier.testTag(C.Tag.EditBook.save).fillMaxWidth(WIDTH_BUTTON),
                        enabled =
                            title.value.isNotBlank() &&
                                author.value.isNotBlank() &&
                                genres.value.isNotEmpty() &&
                                (isbn.value.isBlank() || inputVerification.testIsbn(isbn.value)),
                        onClick = {
                          viewModel.updateDataBook(
                              context,
                              bookUUID,
                              title.value,
                              author.value,
                              description.value,
                              rating.value,
                              photo.value,
                              language.value,
                              isbn.value,
                              genres.value,
                              archived = false,
                              exchange = true)
                        }) {
                          Text("Save")
                        }
                    Spacer(modifier = Modifier.fillMaxWidth(SPACE_BETWEEN_BUTTON))
                    ButtonComponent(
                        modifier = Modifier.testTag(C.Tag.EditBook.delete).fillMaxWidth(),
                        onClick = { viewModel.deleteBook(context, book.uuid) }) {
                          Text("Delete")
                        }
                  }
                })
          })
    }
  }
}
