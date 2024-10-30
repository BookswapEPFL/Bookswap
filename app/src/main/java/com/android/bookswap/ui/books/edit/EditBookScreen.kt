package com.android.bookswap.ui.books.edit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.source.network.BooksFirestoreRepository
import com.android.bookswap.ui.books.add.createDataBook
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    booksRepository: BooksFirestoreRepository,
    navigationActions: NavigationActions,
    book : DataBook
) {

    /*val book =
        booksRepository.selectedBook.collectAsState().value
            ?: return Text(text = "No Book selected. Should not happen", color = Color.Red)
    */ // Use this and modify the editBookScreen structure if needed when incorporating in the app navigation

    var title by remember { mutableStateOf(book.title) }
    var author by remember { mutableStateOf(book.author ?: "") }
    var description by remember { mutableStateOf(book.description ?: "") }
    var rating by remember { mutableStateOf(book.rating?.toString() ?:"") }
    var photo by remember { mutableStateOf(book.photo ?: "") }
    var language by remember { mutableStateOf(book.language.toString()) }
    var genres by remember { mutableStateOf(book.genres) }
    var selectedGenre by remember { mutableStateOf<BookGenres?>(null) } // Genre selection state
    var expanded by remember { mutableStateOf(false) } // State for dropdown menu

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.testTag("editBookScreen").background(ColorVariable.BackGround),
        containerColor = ColorVariable.BackGround,  // Sets entire Scaffold background color
        topBar = {
            TopAppBar(
                title = { Text("Edit your Book", modifier = Modifier.testTag("editBookTitle")) },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("goBackButton"),
                        onClick = { navigationActions.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorVariable.BackGround)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(ColorVariable.BackGround),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title Edit Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter the book title") },
                    modifier = Modifier.fillMaxWidth().testTag("inputBookTitle"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = ColorVariable.Secondary,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )

                )

                // Genre Dropdown Edit Field
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth().testTag("GenreDropdown")
                ) {
                    OutlinedTextField(
                        value = selectedGenre?.Genre ?: "Select Genre",
                        onValueChange = {},
                        label = { Text("Genre") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor().testTag("SelectedGenre"),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = ColorVariable.Secondary,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        BookGenres.values().forEach { genre ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = genre.Genre
                                    )
                                },
                                modifier = Modifier.testTag("GenreDropdownItem_${genre.Genre}"),
                                onClick = {
                                    selectedGenre = genre
                                    genres = listOf(genre) // Update genres list with selected genre
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Author Edit Field
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    placeholder = { Text("Enter the author's name") },
                    modifier = Modifier.fillMaxWidth().testTag("inputBookAuthor"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = ColorVariable.Secondary,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                // Description Edit Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Provide a description of the book") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("inputBookDescription"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = ColorVariable.Secondary,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                // Rating Edit Field
                OutlinedTextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Rating") },
                    placeholder = { Text("Rate the book (e.g. 4.5)") },
                    modifier = Modifier.fillMaxWidth().testTag("inputBookRating"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = ColorVariable.Secondary,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                // ISBN Edit Field
                /*OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") },
                    placeholder = { Text("ISBN Number") },
                    modifier = Modifier.fillMaxWidth().testTag("inputBookISBN")
                )*/ //Remove for now but could be added later

                // Photo Edit Field
                OutlinedTextField(
                    value = photo,
                    onValueChange = { photo = it },
                    label = { Text("Photo ") },
                    placeholder = { Text("Enter a photo of the books") },
                    modifier = Modifier.testTag("inputBookPhoto").testTag("inputBookPhoto"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = ColorVariable.Secondary,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                // Language Edit Field
                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("Language ") },
                    placeholder = { Text("In which language are the book") },
                    modifier = Modifier.testTag("inputBookLanguage").testTag("inputBookLanguage"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = ColorVariable.Secondary,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        try {
                            if (title.isBlank()) throw IllegalArgumentException("Title cannot be null or blank")
                            if (author.isBlank()) throw IllegalArgumentException("Author cannot be null or blank")
                            if (description.isBlank()) throw IllegalArgumentException("Description cannot be null or blank")
                            if (rating.isBlank()) throw IllegalArgumentException("Rating cannot be null or blank")
                            if (photo.isBlank()) throw IllegalArgumentException("Photo cannot be null or blank")
                            if (language.isBlank()) throw IllegalArgumentException("Language cannot be null or blank")
                            if (book.isbn.isNullOrBlank()) throw IllegalArgumentException("ISBN cannot be null or blank")
                            if (genres.isEmpty()) throw IllegalArgumentException("Genres cannot be empty")

                            val updatedBook = createDataBook(
                                context = context,
                                uuid = book.uuid,
                                title = title,
                                author = author,
                                description = description,
                                ratingStr = rating,
                                photo = photo,
                                bookLanguageStr = language,
                                isbn = book.isbn,
                                genres = genres
                            )

                            booksRepository.updateBook(
                                updatedBook!!,
                                OnSucess = { navigationActions.goBack() },
                                onFailure = {
                                    Toast.makeText(
                                        context, "Failed to update book.", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        } catch (e: Exception) {
                            Toast.makeText(
                                context, "Error: ${e.message}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("bookSave").testTag("bookDelete"),
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorVariable.Primary
                    )
                ) {
                    Text("Save", color = Color.White)
                }

                Button(
                    onClick = {
                        booksRepository.deleteBooks(
                            book.uuid.toString(),
                            book,
                            OnSucess = { navigationActions.goBack() },
                            onFailure = {
                                Toast.makeText(
                                    context, "Failed to delete book.", Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("bookDelete"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorVariable.Primary
                    )
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
    )
}