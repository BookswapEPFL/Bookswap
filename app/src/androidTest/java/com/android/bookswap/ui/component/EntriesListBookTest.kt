package com.android.bookswap.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.EntriesListBookComponent
import org.junit.Rule
import org.junit.Test

class EntriesListBookTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testTitleFieldInput() {
    composeTestRule.setContent {
      EntriesListBookComponent(
          paddingValues = PaddingValues(),
          title = mutableStateOf(""),
          genres = mutableStateOf(emptyList()),
          author = mutableStateOf(""),
          description = mutableStateOf(""),
          rating = mutableStateOf(""),
          isbn = mutableStateOf(""),
          photo = mutableStateOf(""),
          selectedLanguage = mutableStateOf(null),
          buttons = {})
    }

    val titleFieldTag = C.Tag.BookEntryComp.title_field

    composeTestRule.onNodeWithTag(titleFieldTag).performTextInput("Test Title")
    composeTestRule.onNodeWithTag(titleFieldTag).assertTextContains("Test Title")
  }

  @Test
  fun testGenresDropdownSelection() {
    composeTestRule.setContent {
      EntriesListBookComponent(
          paddingValues = PaddingValues(),
          title = mutableStateOf(""),
          genres = mutableStateOf(mutableListOf()),
          author = mutableStateOf(""),
          description = mutableStateOf(""),
          rating = mutableStateOf(""),
          isbn = mutableStateOf(""),
          photo = mutableStateOf(""),
          selectedLanguage = mutableStateOf(null),
          buttons = {})
    }

    val genresDropdownTag = C.Tag.BookEntryComp.genre_field

    // Open dropdown
    composeTestRule.onNodeWithTag(genresDropdownTag).performClick()

    // Select a genre
    val genreToSelect = BookGenres.SCIENCEFICTION.Genre
    composeTestRule.onNodeWithTag("${C.Tag.BookEntryComp.genre_menu}_$genreToSelect").performClick()
    composeTestRule.onNodeWithTag(genresDropdownTag).performClick()
    // Verify selection
    composeTestRule.onNodeWithTag(genresDropdownTag).assertTextContains(genreToSelect)
  }

  @Test
  fun testRatingFieldAcceptsValidInput() {
    composeTestRule.setContent {
      EntriesListBookComponent(
          paddingValues = PaddingValues(),
          title = mutableStateOf(""),
          genres = mutableStateOf(emptyList()),
          author = mutableStateOf(""),
          description = mutableStateOf(""),
          rating = mutableStateOf(""),
          isbn = mutableStateOf(""),
          photo = mutableStateOf(""),
          selectedLanguage = mutableStateOf(null),
          buttons = {})
    }
    // Input valid rating
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.rating_star_empty + "_4", useUnmergedTree = true)
        .performClick()
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.rating_star + "_4", useUnmergedTree = true)
        .assertExists()

    // Input invalid rating
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.rating_star_empty + "_5", useUnmergedTree = true)
        .assertExists()
  }

  @Test
  fun testLanguageDropdownSelection() {
    composeTestRule.setContent {
      EntriesListBookComponent(
          paddingValues = PaddingValues(),
          title = mutableStateOf(""),
          genres = mutableStateOf(emptyList()),
          author = mutableStateOf(""),
          description = mutableStateOf(""),
          rating = mutableStateOf(""),
          isbn = mutableStateOf(""),
          photo = mutableStateOf(""),
          selectedLanguage = mutableStateOf(null),
          buttons = {})
    }

    val languageDropdownTag = C.Tag.BookEntryComp.language_field

    // Open dropdown
    composeTestRule.onNodeWithTag(languageDropdownTag).performClick()

    // Select a language
    val languageToSelect = BookLanguages.ENGLISH.languageName
    composeTestRule
        .onNodeWithTag("${C.Tag.BookEntryComp.language_menu}_$languageToSelect")
        .performClick()

    // Verify selection
    composeTestRule.onNodeWithText(languageToSelect).assertExists()
  }

  @Test
  fun testHasInitialValue() {
    composeTestRule.setContent {
      EntriesListBookComponent(
          paddingValues = PaddingValues(),
          title = mutableStateOf("Title"),
          genres = mutableStateOf(listOf(BookGenres.FICTION)),
          author = mutableStateOf("Author"),
          description = mutableStateOf("Description"),
          rating = mutableStateOf("5"),
          isbn = mutableStateOf("1234567890"),
          photo = mutableStateOf("https://example.com/photo.jpg"),
          selectedLanguage = mutableStateOf(BookLanguages.ENGLISH),
          buttons = { modifier ->
            Button(modifier = modifier, onClick = {}) { Text(text = "Save") }
          })
    }
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.title_field).assertTextContains("Title")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.genre_field)
        .assertTextContains(BookGenres.FICTION.Genre)
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.author_field).assertTextContains("Author")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.description_field)
        .assertTextContains("Description")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.rating_star + "_5", useUnmergedTree = true)
        .assertExists()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.isbn_field).assertTextContains("1234567890")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.photo_field)
        .assertTextContains("https://example.com/photo.jpg")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).assertTextContains("English")
  }
}
