package com.android.bookswap.ui.books.add

import androidx.compose.foundation.layout.Row
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
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.add.AddToBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.EntriesListBookComponent

/**
 * Composable function to display the screen for adding a new book.
 *
 * @param viewModel The viewModel that manage the saving of the book.
 * @param topAppBar A composable function to display the top app bar.
 * @param bottomAppBar A composable function to display the bottom app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToBookScreen(
    viewModel: AddToBookViewModel,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  // State variables to store the values entered by the user
  val title = remember { mutableStateOf("") }
  val author = remember { mutableStateOf("") }
  val description = remember { mutableStateOf("") }
  val rating = remember { mutableStateOf("") }
  val isbn = remember { mutableStateOf("") }
  val photo = remember { mutableStateOf("") }
  val language = remember { mutableStateOf<BookLanguages?>(null) }
  val genres = remember { mutableStateOf<List<BookGenres>>(emptyList()) } // Genre selection state
  val context = LocalContext.current

  val appConfig = LocalAppConfig.current

  // Scaffold to provide basic UI structure with a top app bar
  Scaffold(
      modifier = Modifier.testTag(C.Tag.new_book_manual_screen_container),
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
              Row(modifier.fillMaxWidth(0.5f)) {
                ButtonComponent(
                    modifier = Modifier.testTag(C.Tag.BookEntryComp.action_buttons).fillMaxWidth(),
                    enabled =
                        title.value.isNotBlank() &&
                            author.value.isNotBlank() &&
                            language.value != null,
                    onClick = {
                      viewModel.saveDataBook(
                          context,
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
              }
            })
      })
}
/**
 * Creates a DataBook instance after validating the input parameters.
 *
 * @param context The context for showing Toast messages.
 * @param uuid The unique identifier for the book.
 * @param title The title of the book.
 * @param author The author of the book.
 * @param description The description of the book.
 * @param ratingStr The rating of the book as a string.
 * @param photo The URL of the book's photo.
 * @param bookLanguageStr The language of the book as a string.
 * @param isbn The ISBN of the book.
 * @param genres The list of genres the book belongs to.
 * @return A DataBook instance if all validations pass, null otherwise.
 */
fun createDataBook(
    context: Context,
    uuid: UUID,
    title: String,
    author: String,
    description: String,
    ratingStr: String,
    photo: String,
    bookLanguageStr: String,
    isbn: String,
    genres: List<BookGenres>,
    userId: UUID
): DataBook? {
  // Validate UUID
  if (uuid.toString().isBlank()) {
    Log.e("AddToBookScreen", "UUID cannot be empty.")
    Toast.makeText(context, "UUID cannot be empty.", Toast.LENGTH_LONG).show()
    return null
  }

  // Validate Title
  if (title.isBlank()) {
    Log.e("AddToBookScreen", "Title cannot be empty.")
    Toast.makeText(context, "Title cannot be empty.", Toast.LENGTH_LONG).show()

    return null
  }

  // Validate Author
  if (author.isBlank()) {
    Log.e("AddToBookScreen", "Author cannot be empty.")
    Toast.makeText(context, "Author cannot be empty.", Toast.LENGTH_LONG).show()

    return null
  }

  // Validate Rating
  val rating: Int =
      try {
        ratingStr.toInt().also {
          if (it !in 0..5) {
            Log.e("AddToBookScreen", "Rating must be between 0 and 5.")
            Toast.makeText(context, "Rating must be between 0 and 5.", Toast.LENGTH_LONG).show()

            return null
          }
        }
      } catch (e: NumberFormatException) {
        Log.e("AddToBookScreen", "Rating must be a valid number.")
        Toast.makeText(context, "Rating must be a valid number.", Toast.LENGTH_LONG).show()

        return null
      }

  // Validate Photo (assuming basic validation here, just checking if not empty)
  if (photo.isBlank()) {
    Log.e("AddToBookScreen", "Photo URL cannot be empty.")
    Toast.makeText(context, "Photo URL cannot be empty.", Toast.LENGTH_LONG).show()

    return null
  }

  // Validate Language
  val languages: BookLanguages =
      try {
        BookLanguages.valueOf(bookLanguageStr.uppercase())
      } catch (e: IllegalArgumentException) {
        Log.e(
            "AddToBookScreen",
            "Invalid language: $bookLanguageStr. Please use one of the supported languages.")
        Toast.makeText(context, "Invalid language: $bookLanguageStr.", Toast.LENGTH_LONG).show()

        return null
      }

  // Validate ISBN
  if (isbn.isBlank()) {
    Log.e("AddToBookScreen", "ISBN cannot be empty.")
    Toast.makeText(context, "ISBN cannot be empty.", Toast.LENGTH_LONG).show()

    return null
  }

  // If all validations pass, return a new DataBook instance
  return DataBook(
      uuid = uuid,
      title = title,
      author = author,
      description = description,
      rating = rating,
      photo = photo,
      language = languages,
      isbn = isbn,
      genres = genres,
      userId = userId,
      archived = false,
      exchange = false)
}
