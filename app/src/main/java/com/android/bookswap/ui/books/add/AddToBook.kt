package com.android.bookswap.ui.books.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.BookGenres
import com.android.bookswap.model.add.AddBookViewModel
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.theme.ColorVariable.BackGround

private const val HORIZONTAL_PADDING = 30
/**
 * Composable function to display the screen for adding a new book.
 *
 * @param repository The repository to interact with book data.
 * @param topAppBar A composable function to display the top app bar.
 * @param bottomAppBar A composable function to display the bottom app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToBookScreen(
    viewModel: AddBookViewModel,
    topAppBar: @Composable () -> Unit = {},
    bottomAppBar: @Composable () -> Unit = {}
) {
  // State variables to store the values entered by the user
  var title by remember { mutableStateOf("") }
  var author by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var rating by remember { mutableStateOf("") }
  var isbn by remember { mutableStateOf("") }
  var photo by remember { mutableStateOf("") }
  var language by remember { mutableStateOf("") }
  var genres by remember {
    mutableStateOf<List<BookGenres>>(emptyList())
  } // Genre selection state
  var expanded by remember { mutableStateOf(false) } // State for dropdown menu
  // Getting the context for showing Toast messages
  val context = LocalContext.current
  val necessaryEntries = listOf(title, author, description)

  // Scaffold to provide basic UI structure with a top app bar
  Scaffold(
      modifier = Modifier.testTag("addBookScreen"),
      topBar = topAppBar,
      bottomBar = bottomAppBar,
      content = { paddingValues ->
        // Column layout to stack input fields vertically with spacing
        Column(
            modifier =
                Modifier.background(BackGround)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween) {
              // Title Input Field
              FieldComponent(
                  modifier =
                      Modifier.testTag("title_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Title*",
                  value = title) {
                    title = it
                  }
              ExposedDropdownMenuBox(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp)
                          .testTag("genre_field"),
                  expanded = expanded,
                  onExpandedChange = { expanded = !expanded }) {
                    FieldComponent(
                        value = genres.joinToString { it.Genre },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Genres*") },
                        modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)) {
                          BookGenres.values().forEach { genre ->
                            val isSelected = genres.contains(genre)
                            DropdownMenuItem(
                                text = {
                                  Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = genre.Genre,
                                        modifier = Modifier.align(Alignment.CenterVertically))
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (isSelected) {
                                      Icon(
                                          imageVector = Icons.Default.Check,
                                          contentDescription = "Selected")
                                    }
                                  }
                                },
                                onClick = {
                                  if (isSelected) {
                                    genres -= genre
                                  } else {
                                    genres += genre
                                  }
                                })
                          }
                        }
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag("author_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Author",
                  value = author) {
                    author = it
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag("description_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Description",
                  value = description) {
                    description = it
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag("rating_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Rating",
                  value = rating) {
                    if (it == "" ||
                        (it.all { c -> c.isDigit() } &&
                            it.toIntOrNull() != null &&
                            it.toIntOrNull() in 0..5)) { // Ensure all characters are digits
                      rating = it
                    }
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag("isbn_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "ISBN*",
                  value = isbn) {
                    if (it.all { c -> c.isDigit() } && it.length <= 13) {
                      isbn = it
                    }
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag("photo_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Photo",
                  value = photo) {
                    photo = it
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag("language_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Language",
                  value = language) {
                    language = it
                  }
              ButtonComponent(
                  modifier =
                      Modifier.testTag("save_button")
                          .align(Alignment.CenterHorizontally)
                          .fillMaxWidth(0.5f),
                  enabled = !necessaryEntries.any { it.isBlank() },
                  onClick = {
                    viewModel.saveDataBook(
                        context,
                        title,
                        author,
                        description,
                        rating,
                        photo,
                        language,
                        isbn,
                        genres)
                  }) {
                    Text("Save")
                  }
              // empty Spacer to have space bellow save button
              Spacer(modifier = Modifier)
            }
      })
}
