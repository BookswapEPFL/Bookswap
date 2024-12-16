package com.android.bookswap.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.bookswap.R
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.PhotoRequester
import com.android.bookswap.resources.C
import com.android.bookswap.ui.MAXLENGTHAUTHOR
import com.android.bookswap.ui.MAXLENGTHDESCRIPTION
import com.android.bookswap.ui.MAXLENGTHISBN
import com.android.bookswap.ui.MAXLENGTHTITLE
import com.android.bookswap.ui.theme.ColorVariable.BackGround
import java.util.UUID

private const val HORIZONTAL_PADDING = 30
private const val BUTTON_HEIGHT = 56
private const val DESCRIPTION_MAX_LINE = 5
private const val GENRES_MAX_LINE = 3

/** Composable function to display entry fields for a book.(add or edit) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriesListBookComponent(
    paddingValues: PaddingValues,
    title: MutableState<String>,
    genres: MutableState<List<BookGenres>>,
    author: MutableState<String>,
    description: MutableState<String>,
    rating: MutableState<String>,
    isbn: MutableState<String>,
    photo: MutableState<String>,
    selectedLanguage: MutableState<BookLanguages>,
    photoStorage: PhotoFirebaseStorageRepository,
    buttons: @Composable (modifier: Modifier) -> Unit
) {
  val screenWidth = LocalConfiguration.current.screenWidthDp

  var expanded by remember { mutableStateOf(false) }
  var expandedLanguage by remember { mutableStateOf(false) }

  val context = LocalContext.current

  val photoReq = remember {
    PhotoRequester(context) { result ->
      if (result.isSuccess) {
        photoStorage.addPhotoToStorage(
            photoId = UUID.randomUUID().toString(),
            bitmap = result.getOrThrow().asAndroidBitmap(),
            callback = { resultStorage ->
              resultStorage
                  .onSuccess { url -> photo.value = url }
                  .onFailure { exception ->
                    Log.e("EntriesListBookComponent", "Failed to store image: ${exception.message}")
                  }
            })
      } else {
        Toast.makeText(context, "Image could not be stored.", Toast.LENGTH_LONG).show()
        Log.e("EntriesListBookComponent", "Image could not be stored.")
      }
    }
  }

  photoReq.Init()

  LazyColumn(
      modifier =
          Modifier.background(BackGround)
              .fillMaxWidth()
              .fillMaxHeight()
              .padding(paddingValues)
              .testTag(C.Tag.BookEntryComp.scrollable),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Title Input Field
        item {
          FieldComponent(
              onValueChange = { if (it.length <= MAXLENGTHTITLE) title.value = it },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.title_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = title.value,
              label = { Text(text = stringResource(R.string.label_title)) },
              singleLine = true)
        }

        // Genres Dropdown
        item {
          ExposedDropdownMenuBox(
              modifier = Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING.dp),
              expanded = expanded,
              onExpandedChange = { expanded = !expanded }) {
                FieldComponent(
                    value = genres.value.joinToString { it.Genre },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.label_genres)) },
                    modifier =
                        Modifier.menuAnchor()
                            .fillMaxWidth()
                            .testTag(C.Tag.BookEntryComp.genre_field))
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)) {
                      BookGenres.values().forEach { genre ->
                        val isSelected = genres.value.contains(genre)
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
                                genres.value -= genre
                              } else {
                                genres.value += genre
                              }
                            },
                            modifier =
                                Modifier.testTag(
                                    C.Tag.BookEntryComp.genre_menu + "_${genre.Genre}"))
                      }
                    }
              }
        }

        // Author Input Field
        item {
          FieldComponent(
              onValueChange = { if (it.length <= MAXLENGTHAUTHOR) author.value = it },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.author_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = author.value,
              label = { Text(text = stringResource(R.string.label_author)) },
              singleLine = true)
        }

        // Description Input Field
        item {
          FieldComponent(
              onValueChange = { if (it.length <= MAXLENGTHDESCRIPTION) description.value = it },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.description_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = description.value,
              label = { Text(text = stringResource(R.string.label_description)) },
              maxLines = DESCRIPTION_MAX_LINE)
        }

        // ISBN Input Field
        item {
          FieldComponent(
              onValueChange = {
                if (it.length <= MAXLENGTHISBN && it.all { char -> char.isDigit() || char == '-' })
                    isbn.value = it
              },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.isbn_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = isbn.value,
              label = { Text(text = stringResource(R.string.label_isbn)) },
              singleLine = true)
        }

        // Language Dropdown
        item {
          ExposedDropdownMenuBox(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp)
                      .testTag(C.Tag.BookEntryComp.language_menu),
              expanded = expandedLanguage,
              onExpandedChange = { expandedLanguage = !expandedLanguage }) {
                FieldComponent(
                    value = selectedLanguage.value.languageName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.label_language)) },
                    modifier =
                        Modifier.menuAnchor()
                            .fillMaxWidth()
                            .testTag(C.Tag.BookEntryComp.language_field),
                    maxLines = GENRES_MAX_LINE)
                ExposedDropdownMenu(
                    expanded = expandedLanguage,
                    onDismissRequest = { expandedLanguage = false },
                    modifier = Modifier.fillMaxWidth()) {
                      BookLanguages.values().forEach { language ->
                        DropdownMenuItem(
                            modifier =
                                Modifier.testTag(
                                    C.Tag.BookEntryComp.language_menu +
                                        "_${language.languageName}"),
                            text = { Text(text = language.languageName) },
                            onClick = {
                              selectedLanguage.value = language
                              expandedLanguage = false
                            })
                      }
                    }
              }
        }

        // Photo Input Field
        item {
          ButtonWithIcons(
              text = stringResource(R.string.label_photo),
              leftIconPainter = painterResource(id = R.drawable.photoicon),
              onClick = { photoReq.requestPhoto() },
              buttonWidth = (screenWidth - 2 * HORIZONTAL_PADDING).dp,
              buttonHeight = BUTTON_HEIGHT.dp,
              buttonShape = RoundedCornerShape(100))
        }

        // Rating Input Field
        item {
          var currentRating by remember { mutableStateOf(rating.value.toIntOrNull() ?: 0) }

          RatingStarsComponent(
              currentRating = currentRating,
              onRatingChanged = { newRating ->
                currentRating = newRating
                rating.value = newRating.toString()
              },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.rating_field_stars)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp))
        }

        // Buttons
        item { buttons(Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING.dp)) }

        // Spacer for padding
        item { Spacer(modifier = Modifier) }
      }
}
