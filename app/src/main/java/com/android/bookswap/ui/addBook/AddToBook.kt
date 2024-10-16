package com.android.bookswap.ui.addBook

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.ui.theme.ColorVariable.Accent
import com.android.bookswap.ui.theme.ColorVariable.BackGround
import com.android.bookswap.ui.theme.ColorVariable.Primary
import com.android.bookswap.ui.theme.ColorVariable.Secondary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToBook(repository: BooksRepository) {
  // State variables to store the values entered by the user
  var title by remember { mutableStateOf("") }
  var author by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var rating by remember { mutableStateOf("") }
  var isbn by remember { mutableStateOf("") }
  var photo by remember { mutableStateOf("") }
  var language by remember { mutableStateOf("") }
  // Getting the context for showing Toast messages
  val context = LocalContext.current

  // Scaffold to provide basic UI structure with a top app bar
  Scaffold(
      modifier = Modifier.testTag("addBookScreen"),
      topBar = {
        TopAppBar(
            // Title of the screen
            title = {
              Text(
                  text = "Add Your Book",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold,
                  color = Accent,
                  modifier = Modifier.testTag("addBookTitle"))
            },
            // Icon button for navigation (currently no action defined)
            navigationIcon = {
              IconButton(onClick = {}) {
                // You can add an icon here for the button
              }
            })
      },
      content = { paddingValues ->
        // Column layout to stack input fields vertically with spacing
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Title Input Field
          OutlinedTextField(
              value = title,
              onValueChange = { title = it },
              label = { Text("Title") },
              placeholder = { Text("Enter the book title") },
              modifier =
                  Modifier.padding(paddingValues)
                      .testTag("inputBookTitle")
                      .testTag("Title"), // Light background color inside the ,
              colors =
                  TextFieldDefaults.outlinedTextFieldColors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary, // Lighter color for unfocused label
                  ) // Adding padding to the input field
              )

          // Author Input Field
          OutlinedTextField(
              value = author,
              onValueChange = { author = it },
              label = { Text("Author") },
              placeholder = { Text("Enter the author's name") },
              modifier =
                  Modifier.padding(paddingValues).testTag("inputBookAuthor").testTag("Author"),
              colors =
                  TextFieldDefaults.outlinedTextFieldColors(
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
                  TextFieldDefaults.outlinedTextFieldColors(
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
                  TextFieldDefaults.outlinedTextFieldColors(
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
                  TextFieldDefaults.outlinedTextFieldColors(
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
                  TextFieldDefaults.outlinedTextFieldColors(
                      focusedBorderColor = Secondary, // Custom green for focused border
                      unfocusedBorderColor = Secondary, // Lighter green for unfocused border
                      cursorColor = Secondary, // Custom green for the cursor
                      focusedLabelColor = Secondary, // Custom green for focused label
                      unfocusedLabelColor = Secondary // Lighter color for unfocused label
                      ))
          // Language Input Field
          OutlinedTextField(
              value = language,
              onValueChange = { language = it },
              label = { Text("Language ") },
              placeholder = { Text("In which language are the book") },
              modifier = Modifier.testTag("inputBookLanguage").testTag("Language"),
              colors =
                  TextFieldDefaults.outlinedTextFieldColors(
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
                if (title.isNotBlank() && isbn.isNotBlank()) {
                  // You can handle book object creation here (e.g., save the book)
                  val book =
                      createDataBook(
                          repository.getNewUid(),
                          title,
                          author,
                          description,
                          rating,
                          photo,
                          language,
                          isbn)
                  if (book == null) {
                    Toast.makeText(context, "Invalid argument", Toast.LENGTH_SHORT).show()
                  } else {
                    repository.addBook(book, OnSucess = {}, onFailure = {})
                  }
                } else {
                  // Show a Toast message if title or ISBN is empty
                  Toast.makeText(context, "Title and ISBN are required.", Toast.LENGTH_SHORT).show()
                }
              },
              // Enable the button only if title and ISBN are filled
              enabled = title.isNotBlank() && isbn.isNotBlank(),
              modifier = Modifier.testTag("bookSave").testTag("Save")) {
                // Text displayed on the button
                Text("Save", modifier = Modifier.testTag("bookSave").testTag("Save"))
              }
        }
      })
}

fun createDataBook(
    uuid: UUID,
    title: String,
    author: String,
    description: String,
    ratingStr: String,
    photo: String,
    bookLanguageStr: String,
    isbn: String
): DataBook? {
  // Validate UUID
  if (uuid.toString().isBlank()) {
    println("UUID cannot be empty.")
    return null
  }

  // Validate Title
  if (title.isBlank()) {
    println("Title cannot be empty.")
    return null
  }

  // Validate Author
  if (author.isBlank()) {
    println("Author cannot be empty.")
    return null
  }
  // Validate Rating
  val rating: Int =
      try {
        ratingStr.toInt().also {
          if (it !in 0..5) {
            println("Rating must be between 0 and 5.")
            return null
          }
        }
      } catch (e: NumberFormatException) {
        println("Rating must be a valid number.")
        return null
      }

  // Validate Photo  (assuming basic validation here, just checking if not empty)
  if (photo.isBlank()) {
    println("Photo URL cannot be empty.")
    return null
  }

  // Validate Language
  val languages: BookLanguages =
      try {
        BookLanguages.valueOf(bookLanguageStr.uppercase())
      } catch (e: IllegalArgumentException) {
        println("Invalid language: $bookLanguageStr. Please use one of the supported languages.")
        return null
      }

  // Validate ISBN
  if (isbn.isBlank()) {
    println("ISBN cannot be empty.")
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
      isbn = isbn)
}