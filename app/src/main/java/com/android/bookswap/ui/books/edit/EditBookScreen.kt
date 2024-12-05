package com.android.bookswap.ui.books.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.EntriesListBookComponent

/** Constants * */
private const val WIDTH_BUTTON = 0.45f
private const val SPACE_BETWEEN_BUTTON = 0.05f / (1f - WIDTH_BUTTON)

/**
 * Composable function to display the Edit Book screen.
 *
 * @param viewModel Manages the updating and deleting of the books
 * @param book The book data to be edited.
 * @param topAppBar Top bar of the application
 * @param bottomAppBar bottom bar of the application
 */
@Composable
fun EditBookScreen(
    viewModel: EditBookViewModel,
    book: DataBook,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  val title = remember { mutableStateOf(book.title) }
  val author = remember { mutableStateOf(book.author ?: "") }
  val description = remember { mutableStateOf(book.description ?: "") }
  val rating = remember { mutableStateOf(book.rating?.toString() ?: "") }
  val photo = remember { mutableStateOf(book.photo ?: "") }
  val language = remember { mutableStateOf<BookLanguages?>(null) }
  val genres = remember { mutableStateOf(book.genres) }
  val isbn = remember { mutableStateOf(book.isbn ?: "") }

  val context = LocalContext.current

  Scaffold(
      modifier = Modifier.testTag(C.Tag.edit_book_screen_container),
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
            buttons = { modifier ->
              Row(modifier.fillMaxWidth()) {
                ButtonComponent(
                    modifier =
                        Modifier.testTag(C.Tag.BookEntryComp.action_buttons)
                            .fillMaxWidth(WIDTH_BUTTON),
                    enabled =
                        title.value.isNotBlank() &&
                            author.value.isNotBlank() &&
                            language.value != null,
                    onClick = {
                      viewModel.updateDataBook(
                          context,
                          book.uuid,
                          title.value,
                          author.value,
                          description.value,
                          rating.value,
                          photo.value,
                          language.value!!,
                          isbn.value,
                          genres.value)
                    }) {
                      Text("Save")
                    }
                Spacer(modifier = Modifier.fillMaxWidth(SPACE_BETWEEN_BUTTON))
                ButtonComponent(
                    modifier = Modifier.testTag(C.Tag.BookEntryComp.cancel_button).fillMaxWidth(),
                    onClick = { viewModel.deleteBooks(context, book.uuid) }) {
                      Text("Delete")
                    }
              }
            })
      })
}
