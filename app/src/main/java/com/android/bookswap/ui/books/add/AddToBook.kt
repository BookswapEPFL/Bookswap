package com.android.bookswap.ui.books.add

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.InputVerification
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.add.AddToBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.MAXLENGTHAUTHOR
import com.android.bookswap.ui.MAXLENGTHDESCRIPTION
import com.android.bookswap.ui.MAXLENGTHISBN
import com.android.bookswap.ui.MAXLENGTHPHOTO
import com.android.bookswap.ui.MAXLENGTHRATING
import com.android.bookswap.ui.MAXLENGTHTITLE
import com.android.bookswap.ui.components.ButtonComponent
import com.android.bookswap.ui.components.EntriesListBookComponent
import com.android.bookswap.ui.components.FieldComponent
import com.android.bookswap.ui.theme.ColorVariable.BackGround

/**
 * Composable function to display the screen for adding a new book.
 *
 * @param viewModel The viewModel that manage the saving of the book.
 * @param photoStorage The repository that manages the storage of the images
 * @param topAppBar A composable function to display the top app bar.
 * @param bottomAppBar A composable function to display the bottom app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToBookScreen(
    viewModel: AddToBookViewModel,
    photoStorage: PhotoFirebaseStorageRepository,
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
  val genres = remember  { mutableStateOf<List<BookGenres>>(emptyList()) }

  // Getting the context for showing Toast messages
  val context = LocalContext.current
  val inputVerification = InputVerification()

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
              photoStorage,
              buttons = { modifier ->
                  Row(modifier.fillMaxWidth(0.5f)) {
                      ButtonComponent(
                          modifier = Modifier.testTag(C.Tag.BookEntryComp.action_buttons)
                              .fillMaxWidth(),
                          enabled =
                          title.value.isNotBlank() &&
                                  author.value.isNotBlank() &&
                                  language.value != null &&
                                  genres.value.isNotEmpty() &&
                                  (isbn.value.isBlank() || inputVerification.testIsbn(isbn.value)),
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
                                  genres.value,
                                  archived = false,
                                  exchange = true
                              )
                          }) {
                          Text("Save")
                      }
                  }
              })
      }
  )

}

                  /*ButtonComponent(
                  modifier =
                      Modifier.testTag(C.Tag.NewBookManually.save)
                          .align(Alignment.CenterHorizontally)
                          .fillMaxWidth(0.5f),
                  enabled =
                      title.isNotBlank() &&
                          author.isNotBlank() &&
                          selectedLanguage != null &&
                          (isbn.isBlank() || inputVerification.testIsbn(isbn)),
                  onClick = {
                    viewModel.saveDataBook(
                        context,
                        title,
                        author,
                        description,
                        rating,
                        photo,
                        selectedLanguage ?: BookLanguages.OTHER,
                        isbn,
                        selectedGenre?.let { listOf(it) } ?: listOf(BookGenres.OTHER),
                        archived = false,
                        exchange = true)
                  }) {
                    Text("Save")
                  }*/
