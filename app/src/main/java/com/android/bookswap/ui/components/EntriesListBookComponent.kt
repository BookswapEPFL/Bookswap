package com.android.bookswap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.model.InputVerification
import com.android.bookswap.resources.C
import com.android.bookswap.ui.theme.ColorVariable.BackGround

private const val HORIZONTAL_PADDING = 30

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
    selectedLanguage: MutableState<BookLanguages?>,
    buttons: @Composable (modifier: Modifier) -> Unit
) {
  val verifier = InputVerification()

  var expanded by remember { mutableStateOf(false) }
  var expandedLanguage by remember { mutableStateOf(false) }

  LazyColumn(
      modifier =
          Modifier.background(BackGround).fillMaxWidth().fillMaxHeight().padding(paddingValues).testTag(C.Tag.BookEntryComp.scrollable),
      verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Title Input Field
        item {
          FieldComponent(
              onValueChange = { title.value = it },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.title_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = title.value,
              label = { Text(text = "Title*") })
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
                    label = { Text(text = "Genres*") },
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
              onValueChange = { author.value = it },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.author_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = author.value,
              label = { Text(text = "Author*") })
        }

        // Description Input Field
        item {
          FieldComponent(
              onValueChange = { description.value = it },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.description_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = description.value,
              label = { Text(text = "Description*") })
        }

        // Rating Input Field
        item {
          FieldComponent(
              onValueChange = {
                if (it == "" || (it.all { c -> c.isDigit() } && it.toIntOrNull() in 0..5)) {
                  rating.value = it
                }
              },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.rating_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = rating.value,
              label = { Text(text = "Rating*(0-5)") })
        }

        // ISBN Input Field
        item {
          FieldComponent(
              onValueChange = {
                if (verifier.testIsbn(it)) {
                  isbn.value = it
                }
              },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.isbn_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = isbn.value,
              label = { Text(text = "ISBN*") })
        }

        // Photo Input Field
        item {
          FieldComponent(
              onValueChange = { photo.value = it },
              modifier =
                  Modifier.testTag(C.Tag.BookEntryComp.photo_field)
                      .fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp),
              value = photo.value,
              label = { Text(text = "Pics*") })
        }

        // Language Dropdown
        item {
          ExposedDropdownMenuBox(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = HORIZONTAL_PADDING.dp)
                      .testTag(C.Tag.BookEntryComp.language_field),
              expanded = expandedLanguage,
              onExpandedChange = { expandedLanguage = !expandedLanguage }) {
                FieldComponent(
                    value = selectedLanguage.value?.languageCode ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Language*") },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag(C.Tag.BookEntryComp.language_menu))
                ExposedDropdownMenu(
                    expanded = expandedLanguage,
                    onDismissRequest = { expandedLanguage = false },
                    modifier = Modifier.fillMaxWidth()) {
                      BookLanguages.values().forEach { language ->
                        DropdownMenuItem(
                            modifier = Modifier.testTag(C.Tag.BookEntryComp.language_menu + "_${language.languageCode}"),
                            text = { Text(text = language.languageCode) },
                            onClick = {
                              selectedLanguage.value = language
                              expandedLanguage = false
                            })
                      }
                    }
              }
        }

        // Buttons
        item { buttons(Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING.dp)) }

        // Spacer for padding
        item { Spacer(modifier = Modifier) }
      }
}
