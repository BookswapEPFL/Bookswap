package com.android.bookswap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
  var expanded by remember { mutableStateOf(false) }
  var expandedLanguage by remember { mutableStateOf(false) }
  Column(
      modifier =
          Modifier.background(BackGround).fillMaxWidth().fillMaxHeight().padding(paddingValues),
      verticalArrangement = Arrangement.SpaceBetween) {
        // Title Input Field
        FieldComponent(
            modifier =
                Modifier.testTag("title_field")
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp),
            labelText = "Title*",
            value = title.value) {
              title.value = it
            }
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING.dp),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
              FieldComponent(
                  value = genres.value.joinToString { it.Genre },
                  onValueChange = {},
                  readOnly = true,
                  label = { Text(text = "Genres") },
                  modifier = Modifier.menuAnchor().fillMaxWidth().testTag("genre_field"))
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
                          modifier = Modifier.testTag("genre_menu_${genre.Genre}"))
                    }
                  }
            }
        FieldComponent(
            modifier =
                Modifier.testTag("author_field")
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp),
            labelText = "Author*",
            value = author.value) {
              author.value = it
            }
        FieldComponent(
            modifier =
                Modifier.testTag("description_field")
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp),
            labelText = "Description",
            value = description.value) {
              description.value = it
            }
        FieldComponent(
            modifier =
                Modifier.testTag("rating_field")
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp),
            labelText = "Rating",
            value = rating.value) {
              if (it == "" ||
                  (it.all { c -> c.isDigit() } &&
                      it.toIntOrNull() != null &&
                      it.toIntOrNull() in 0..5)) { // Ensure all characters are digits
                rating.value = it
              }
            }
        FieldComponent(
            modifier =
                Modifier.testTag("isbn_field")
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp),
            labelText = "ISBN",
            value = isbn.value) {
              if (it.all { c -> c.isDigit() } && it.length <= 13) {
                isbn.value = it
              }
            }
        FieldComponent(
            modifier =
                Modifier.testTag("photo_field")
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp),
            labelText = "Photo",
            value = photo.value) {
              photo.value = it
            }
        ExposedDropdownMenuBox(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_PADDING.dp)
                    .testTag("language_field"),
            expanded = expandedLanguage,
            onExpandedChange = { expandedLanguage = !expandedLanguage }) {
              FieldComponent(
                  value = selectedLanguage.value?.languageCode ?: "",
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
                            selectedLanguage.value = language
                            expandedLanguage = false
                          })
                    }
                  }
            }
        buttons(
            Modifier.align(Alignment.CenterHorizontally)
                .padding(horizontal = HORIZONTAL_PADDING.dp))
        // empty Spacer to have space bellow save button
        Spacer(modifier = Modifier)
      }
}
