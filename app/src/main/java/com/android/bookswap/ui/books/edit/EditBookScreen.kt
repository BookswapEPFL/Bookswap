package com.android.bookswap.ui.books.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth

import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.resources.C

import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.EntriesListBookComponent

import com.android.bookswap.ui.books.add.createDataBook
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import java.util.UUID


/** Constants * */
private const val WIDTH_BUTTON = 0.45f
private const val SPACE_BETWEEN_BUTTON = 0.05f / (1f - WIDTH_BUTTON)

/**
 * Composable function to display the Edit Book screen.
 *
 * @param viewModel Manages the updating and deleting of the books
 * @param topAppBar Top bar of the application
 * @param bottomAppBar bottom bar of the application
 * @param navigationActions The navigation actions to handle navigation events.
 * @param bookUUID The uuid of the book data to be edited.
 */
@Composable
fun EditBookScreen(
    viewModel: EditBookViewModel,
    navigationActions: NavigationActions,
    bookUUID: UUID,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}

) {

  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val columnMaxWidth = screenWidth * COLUMN_WIDTH_RATIO

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
      var title by remember { mutableStateOf(book.title) }
      var author by remember { mutableStateOf(book.author ?: "") }
      var description by remember { mutableStateOf(book.description ?: "") }
      var rating by remember { mutableStateOf(book.rating?.toString() ?: "") }
      var photo by remember { mutableStateOf(book.photo ?: "") }
      var language by remember { mutableStateOf(book.language.toString()) }
      var genres by remember { mutableStateOf(book.genres) }
      var selectedGenre by remember { mutableStateOf<BookGenres?>(null) } // Genre selection state
      var expanded by remember { mutableStateOf(false) } // State for dropdown menu

    }}
    val maxLengthTitle = 50
      val maxLengthAuthor = 50
      val maxLengthDescription = 10000
      val maxLengthRating = 1
      val maxLengthPhoto = 50

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
      
}}
