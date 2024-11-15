package com.android.bookswap.ui.books.add

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.theme.ColorVariable.BackGround
import java.util.UUID

private const val HORIZONTAL_PADDING = 30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToBookScreen(
    repository: BooksRepository,
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
  var selectedGenre by remember { mutableStateOf<BookGenres?>(null) } // Genre selection state
  var expanded by remember { mutableStateOf(false) } // State for dropdown menu
  var expandedLanguage by remember { mutableStateOf(false) } // State for dropdown menu Language
  // Getting the context for showing Toast messages
  val context = LocalContext.current

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
              }

          // Author Input Field
          OutlinedTextField(
              value = author,
              onValueChange = { author = it },
              label = { Text("Author") },
              placeholder = { Text("Enter the author's name") },
              modifier =
                  Modifier.padding(paddingValues).testTag("inputBookAuthor").testTag("Author"),
              colors =
                  OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary // Lighter color for unfocused label
                      ) // Adding padding to the input field
              )
          // Description Input Field
          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Description") },
              placeholder = { Text("Provide a description of the book") },
              modifier = Modifier.testTag("inputBookDescription").testTag("Description"),
              colors =
                  OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary // Lighter color for unfocused label
                      ))
          // Rating Input Field
          OutlinedTextField(
              value = rating,
              onValueChange = { rating = it },
              label = { Text("Rating") },
              placeholder = { Text("Rate the book (e.g. 4.5)") },
              modifier = Modifier.testTag("inputBookRating"),
              colors =
                  OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary // Lighter color for unfocused label
                      ))
          // ISBN Input Field
          OutlinedTextField(
              value = isbn,
              onValueChange = { isbn = it },
              label = { Text("ISBN") },
              placeholder = { Text("Enter the ISBN") },
              modifier = Modifier.testTag("inputBookISBN").testTag("ISBN"),
              colors =
                  OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary // Lighter color for unfocused label
                      ))
          // Photo  Input Field
          OutlinedTextField(
              value = photo,
              onValueChange = { photo = it },
              label = { Text("Photo ") },
              placeholder = { Text("Enter a photo of the books") },
              modifier = Modifier.testTag("inputBookPhoto").testTag("Photo"),
              colors =
                  OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary // Lighter color for unfocused label
                      ))
          // TODO: Add a photo picker here

          // Language Input Field
          OutlinedTextField(
              value = language,
              onValueChange = { language = it },
              label = { Text("Language ") },
              placeholder = { Text("In which language are the book") },
              modifier = Modifier.testTag("inputBookLanguage").testTag("Language"),
              colors =
                  OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary // Lighter color for unfocused label
                      ))
          // Save Button
          Button(
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = Primary, // Light green
                      contentColor = BackGround),
              onClick = {
                // Check if title and ISBN are not blank (required fields)
                if (title.isNotBlank() && isbn.isNotBlank() && selectedGenre != null) {
                  // You can handle book object creation here (e.g., save the book)
                  val book =
                      createDataBook(
                          context,
                          repository.getNewUUID(),
                          title,
                          author,
                          description,
                          rating,
                          photo,
                          language,
                          isbn,
                          listOf(selectedGenre!!),
                      )
                  if (book == null) {
                    Toast.makeText(context, "Invalid argument", Toast.LENGTH_SHORT).show()
                  } else {
                    repository.addBook(book, OnSucess = {}, onFailure = {})
              ExposedDropdownMenuBox(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING.dp),
                  expanded = expanded,
                  onExpandedChange = { expanded = !expanded }) {
                    FieldComponent(
                        value = selectedGenre?.Genre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Genres*") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                        // .background(shape = RoundedCornerShape(100), color =
                        // Secondary).fillMaxWidth()
                        )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()) {
                          BookGenres.values().forEach { genre ->
                            DropdownMenuItem(
                                text = {
                                  Text(
                                      text = genre.Genre,
                                  )
                                },
                                onClick = {
                                  selectedGenre = genre
                                  expanded = false
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
                    rating = it
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag("isbn_field")
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "ISBN*",
                  value = isbn) {
                    isbn = it
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
                  enabled = title.isNotBlank() && isbn.isNotBlank(),
                  onClick = {
                    // Check if title and ISBN are not blank (required fields)
                    if (title.isNotBlank() && isbn.isNotBlank() && selectedGenre != null) {
                      // You can handle book object creation here (e.g., save the book)
                      val book =
                          createDataBook(
                              context,
                              repository.getNewUUID(),
                              title,
                              author,
                              description,
                              rating,
                              photo,
                              language,
                              isbn,
                              listOf(selectedGenre!!))
                      if (book == null) {
                        Toast.makeText(context, "Invalid argument", Toast.LENGTH_SHORT).show()
                      } else {
                        repository.addBook(book, callback = {})
                      }
                    } else {
                      // Show a Toast message if title or ISBN is empty
                      Toast.makeText(context, "Title and ISBN are required.", Toast.LENGTH_SHORT)
                          .show()
                    }
                  }) {
                    Text("Save")
                  }
              // empty Spacer to have space bellow save button
              Spacer(modifier = Modifier)
            }
      })
}

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
      photo = null,
      language = languages,
      isbn = isbn,
      genres = genres,
  )
}
