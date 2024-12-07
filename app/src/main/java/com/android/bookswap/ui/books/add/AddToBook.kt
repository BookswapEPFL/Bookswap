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
import com.android.bookswap.model.InputVerification
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.resources.C
import com.android.bookswap.ui.MAXLENGTHAUTHOR
import com.android.bookswap.ui.MAXLENGTHDESCRIPTION
import com.android.bookswap.ui.MAXLENGTHISBN
import com.android.bookswap.ui.MAXLENGTHPHOTO
import com.android.bookswap.ui.MAXLENGTHRATING
import com.android.bookswap.ui.MAXLENGTHTITLE
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.theme.ColorVariable.BackGround
import java.util.UUID

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
  // var language by remember { mutableStateOf("") }
  var selectedLanguage by remember {
    mutableStateOf<BookLanguages?>(null)
  } // Language selection state
  var selectedGenre by remember { mutableStateOf<BookGenres?>(null) } // Genre selection state
  var expanded by remember { mutableStateOf(false) } // State for dropdown menu
  var expandedLanguage by remember { mutableStateOf(false) } // State for dropdown menu Language
  // Getting the context for showing Toast messages
  val context = LocalContext.current
  val inputVerification = InputVerification()

  val appConfig = LocalAppConfig.current

  // Scaffold to provide basic UI structure with a top app bar
  Scaffold(
      modifier = Modifier.testTag(C.Tag.new_book_manual_screen_container),
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
                      Modifier.testTag(C.Tag.NewBookManually.title)
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Title*",
                  value = title,
                  maxLength = MAXLENGTHTITLE) {
                    title = it
                  }
              ExposedDropdownMenuBox(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp)
                          .testTag(C.Tag.NewBookManually.genres),
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
                      Modifier.testTag(C.Tag.NewBookManually.author)
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Author",
                  value = author,
                  maxLength = MAXLENGTHAUTHOR) {
                    author = it
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag(C.Tag.NewBookManually.synopsis)
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Description",
                  value = description,
                  maxLength = MAXLENGTHDESCRIPTION) {
                    description = it
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag(C.Tag.NewBookManually.rating)
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Rating",
                  value = rating,
                  maxLength = MAXLENGTHRATING) {
                    rating = it
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag(C.Tag.NewBookManually.isbn)
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "ISBN*",
                  value = isbn,
                  maxLength = MAXLENGTHISBN) {
                    isbn = it
                    Log.d("ISBN Input", "Updated ISBN: $isbn")
                  }
              FieldComponent(
                  modifier =
                      Modifier.testTag(C.Tag.NewBookManually.photo)
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Photo",
                  value = photo,
                  maxLength = MAXLENGTHPHOTO) {
                    photo = it
                  }
              /*
              FieldComponent(
                  modifier =
                      Modifier.testTag(C.Tag.NewBookManually.language)
                          .fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp),
                  labelText = "Language",
                  value = language) {
                    language = it
                  }*/
              // Language Dropdown:
              ExposedDropdownMenuBox(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = HORIZONTAL_PADDING.dp)
                          .testTag(C.Tag.NewBookManually.language),
                  expanded = expandedLanguage,
                  onExpandedChange = { expandedLanguage = !expandedLanguage }) {
                    FieldComponent(
                        value = selectedLanguage?.languageCode ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Language*") },
                        modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(
                        expanded = expandedLanguage,
                        onDismissRequest = { expandedLanguage = false },
                        modifier = Modifier.fillMaxWidth()) {
                          BookLanguages.values().forEach { language ->
                            DropdownMenuItem(
                                text = {
                                  Text(
                                      text = language.languageCode,
                                  )
                                },
                                onClick = {
                                  selectedLanguage = language
                                  expandedLanguage = false
                                })
                          }
                        }
                  }

              ButtonComponent(
                  modifier =
                      Modifier.testTag(C.Tag.NewBookManually.save)
                          .align(Alignment.CenterHorizontally)
                          .fillMaxWidth(0.5f),
                  enabled = title.isNotBlank() && isbn.isNotBlank(),
                  onClick = {
                    // Check if title and ISBN are not blank (required fields)
                    if (title.isNotBlank() &&
                        isbn.isNotBlank() &&
                        inputVerification.testIsbn(isbn) &&
                        selectedGenre != null) {
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
                              selectedLanguage.toString(),
                              isbn,
                              listOf(selectedGenre!!),
                              appConfig.userViewModel.uuid)

                      if (book == null) {
                        Log.e("AddToBookScreen", "Invalid argument")
                        Toast.makeText(context, "Invalid argument", Toast.LENGTH_SHORT).show()
                      } else {
                        Log.d("AddToBookScreen", "Adding book: $book")
                        repository.addBook(
                            book,
                            callback = {
                              if (it.isSuccess) {
                                val newBookList =
                                    appConfig.userViewModel.getUser().bookList + book.uuid
                                appConfig.userViewModel.updateUser(bookList = newBookList)
                                Toast.makeText(context, "${book.title} added", Toast.LENGTH_LONG)
                                    .show()
                              } else {
                                val error = it.exceptionOrNull()!!
                                Log.e("AddToBookScreen", it.toString())
                                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                              }
                            })
                      }
                    } else {
                      // Show a Toast message if title or ISBN is empty
                      Log.e("AddToBookScreen", "Title and ISBN are required.")
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
