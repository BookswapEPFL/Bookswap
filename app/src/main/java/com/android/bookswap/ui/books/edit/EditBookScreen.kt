package com.android.bookswap.ui.books.edit

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable
import java.util.UUID

/** Constants * */
private val SCREEN_PADDING = 16.dp
private val ELEMENT_SPACING = 8.dp
private val BUTTON_SPACER_HEIGHT = 16.dp
private const val COLUMN_WIDTH_RATIO = 0.9f // Column width as 90% of screen width
/**
 * Composable function to display the Edit Book screen.
 *
 * @param viewModel Manages the updating and deleting of the books
 * @param navigationActions The navigation actions to handle navigation events.
 * @param bookUUID The uuid of the book data to be edited.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    viewModel: EditBookViewModel,
    navigationActions: NavigationActions,
    bookUUID: UUID
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

      val maxLengthTitle = 50
      val maxLengthAuthor = 50
      val maxLengthDescription = 10000
      val maxLengthRating = 1
      val maxLengthPhoto = 50

      val context = LocalContext.current

      Scaffold(
          modifier =
              Modifier.testTag(C.Tag.edit_book_screen_container)
                  .background(ColorVariable.BackGround),
          containerColor = ColorVariable.BackGround, // Sets entire Scaffold background color
          topBar = {
            TopAppBar(
                title = {
                  Text("Edit your Book", modifier = Modifier.testTag(C.Tag.TopAppBar.screen_title))
                },
                navigationIcon = {
                  IconButton(
                      modifier = Modifier.testTag(C.Tag.TopAppBar.back_button),
                      onClick = { navigationActions.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                      }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(containerColor = ColorVariable.BackGround))
          },
          content = { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(paddingValues)
                        .padding(SCREEN_PADDING)
                        .widthIn(max = columnMaxWidth)
                        .background(ColorVariable.BackGround)
                        .testTag(C.Tag.EditBook.scrollable),
                verticalArrangement = Arrangement.spacedBy(ELEMENT_SPACING)) {
                  // Title Edit Field
                  item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { if (it.length <= maxLengthTitle) title = it },
                        label = { Text("Title") },
                        placeholder = { Text("Enter the book title") },
                        modifier = Modifier.fillMaxWidth().testTag(C.Tag.EditBook.title),
                        colors =
                            TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = ColorVariable.Secondary,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black))
                  }

                  item {
                    // Genre Dropdown Edit Field
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth().testTag(C.Tag.EditBook.genres)) {
                          OutlinedTextField(
                              value = selectedGenre?.Genre ?: "Select Genre",
                              onValueChange = {},
                              label = { Text("Genre") },
                              readOnly = true,
                              trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                              },
                              modifier =
                                  Modifier.menuAnchor().testTag("selected" + C.Tag.EditBook.genre),
                              colors =
                                  TextFieldDefaults.outlinedTextFieldColors(
                                      containerColor = ColorVariable.Secondary,
                                      focusedBorderColor = Color.Black,
                                      unfocusedBorderColor = Color.Black))
                          ExposedDropdownMenu(
                              expanded = expanded, onDismissRequest = { expanded = false }) {
                                BookGenres.values().forEach { genre ->
                                  DropdownMenuItem(
                                      text = { Text(text = genre.Genre) },
                                      modifier =
                                          Modifier.testTag(genre.Genre + C.Tag.EditBook.genre),
                                      onClick = {
                                        selectedGenre = genre
                                        genres =
                                            listOf(genre) // Update genres list with selected genre
                                        expanded = false
                                      })
                                }
                              }
                        }
                  }

                  item {
                    // Author Edit Field
                    OutlinedTextField(
                        value = author,
                        onValueChange = { if (it.length <= maxLengthAuthor) author = it },
                        label = { Text("Author") },
                        placeholder = { Text("Enter the author's name") },
                        modifier = Modifier.fillMaxWidth().testTag(C.Tag.EditBook.author),
                        colors =
                            TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = ColorVariable.Secondary,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black))
                  }

                  item {
                    // Description Edit Field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= maxLengthDescription) description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Provide a description of the book") },
                        modifier = Modifier.fillMaxWidth().testTag(C.Tag.EditBook.synopsis),
                        colors =
                            TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = ColorVariable.Secondary,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black))
                  }

                  item {
                    // Rating Edit Field
                    OutlinedTextField(
                        value = rating,
                        onValueChange = { if (it.length <= maxLengthRating) rating = it },
                        label = { Text("Rating") },
                        placeholder = { Text("Rate the book (e.g. 4.5)") },
                        modifier = Modifier.fillMaxWidth().testTag(C.Tag.EditBook.rating),
                        colors =
                            TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = ColorVariable.Secondary,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black))
                  }

                  item {
                    // Photo Edit Field
                    OutlinedTextField(
                        value = photo,
                        onValueChange = { if (it.length <= maxLengthPhoto) photo = it },
                        label = { Text("Photo ") },
                        placeholder = { Text("Enter a photo of the books") },
                        modifier = Modifier.testTag(C.Tag.EditBook.image),
                        colors =
                            TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = ColorVariable.Secondary,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black))
                  }

                  item {
                    // Language Edit Field
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        label = { Text("Language ") },
                        placeholder = { Text("In which language are the book") },
                        modifier = Modifier.testTag(C.Tag.EditBook.language),
                        colors =
                            TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = ColorVariable.Secondary,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black))
                  }
                  item { Spacer(modifier = Modifier.height(BUTTON_SPACER_HEIGHT)) }

                  item {
                    Button(
                        onClick = {
                          viewModel.updateDataBook(
                              context,
                              book.uuid,
                              title,
                              author,
                              description,
                              rating,
                              photo,
                              enumValues<BookLanguages>().firstOrNull { it.name == language }
                                  ?: BookLanguages.OTHER,
                              book.isbn ?: "",
                              selectedGenre?.let { listOf(it) } ?: listOf(BookGenres.OTHER),
                              book.archived,
                              book.exchange)
                        },
                        modifier = Modifier.fillMaxWidth().testTag(C.Tag.EditBook.save),
                        enabled =
                            title.isNotBlank() &&
                                author.isNotBlank() &&
                                enumValues<BookLanguages>().firstOrNull { it.name == language } !=
                                    null,
                        colors =
                            ButtonDefaults.buttonColors(containerColor = ColorVariable.Primary)) {
                          Text("Save", color = Color.White)
                        }
                  }

                  item {
                    Button(
                        onClick = { viewModel.deleteBook(context, book.uuid) },
                        modifier = Modifier.fillMaxWidth().testTag(C.Tag.EditBook.delete),
                        colors =
                            ButtonDefaults.buttonColors(containerColor = ColorVariable.Primary)) {
                          Text("Delete", color = Color.White)
                        }
                  }
                }
          })
    }
  }
}
