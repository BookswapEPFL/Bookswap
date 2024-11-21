package com.android.bookswap.ui.books.edit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.EntriesListBookComponent
import com.android.bookswap.ui.navigation.NavigationActions

/** Constants * */
private const val COLUMN_WIDTH_RATIO = 0.9f // Column width as 90% of screen width
private const val WIDTH_BUTTON = 0.45f
private const val SPACE_BETWEEN_BUTTON = 0.05f / (1f - WIDTH_BUTTON)

/**
 * Composable function to display the Edit Book screen.
 *
 * @param viewModel
 * @param navigationActions The navigation actions to handle navigation events.
 * @param book The book data to be edited.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    viewModel: EditBookViewModel,
    navigationActions: NavigationActions,
    book: DataBook,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  val title = remember { mutableStateOf(book.title) }
  val author = remember { mutableStateOf(book.author ?: "") }
  val description = remember { mutableStateOf(book.description ?: "") }
  val rating = remember { mutableStateOf(book.rating?.toString() ?: "") }
  val photo = remember { mutableStateOf(book.photo ?: "") }
  val language = remember { mutableStateOf(book.language.toString()) }
  val genres = remember { mutableStateOf(book.genres) }
  val isbn = remember { mutableStateOf(book.isbn ?: "") }

  val context = LocalContext.current

  Scaffold(
      modifier = Modifier.testTag("editBookScreen"),
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
            buttons = { list ->
              ButtonComponent(
                  modifier = Modifier.testTag("save_button").fillMaxWidth(WIDTH_BUTTON),
                  enabled = !list.any { it.isBlank() },
                  onClick = {
                    viewModel.saveDataBook(
                        context,
                        book.uuid,
                        title.value,
                        author.value,
                        description.value,
                        rating.value,
                        photo.value,
                        language.value,
                        isbn.value,
                        genres.value)
                  }) {
                    Text("Save")
                  }
              Spacer(modifier = Modifier.fillMaxWidth(SPACE_BETWEEN_BUTTON))
              ButtonComponent(
                  modifier = Modifier.testTag("delete_button").fillMaxWidth(),
                  onClick = { viewModel.deleteBooks(context, book.uuid) }) {
                    Text("Delete")
                  }
            })
      })
}
