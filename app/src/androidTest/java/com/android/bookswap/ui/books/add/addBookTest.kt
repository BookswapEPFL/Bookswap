package com.android.bookswap.ui.books.add

import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.model.add.AddToBookViewModel
import com.android.bookswap.resources.C
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddToBookTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockViewModel: AddToBookViewModel = mockk()

  @Before
  fun init() {
    // Mock the ViewModel save method to run without side effects
    every {
      mockViewModel.saveDataBook(any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs

    // Mock Toast messages for testing purposes
    mockkStatic(Toast::class)
    val toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock
  }

  @Test
  fun testSaveButtonDisabledInitially() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Verify that the Save button is initially disabled
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.action_buttons).assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Fill in the required fields (Title and Author and language)
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.title_field).performTextInput("My Book Title")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.author_field).performTextInput("Kaaris")

    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookEntryComp.language_field))
    // Simulate a click to open the language dropdown menu
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).performClick()
    // Select a language
    composeTestRule.onNodeWithText("English").performClick()
    // close the dropdown
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).performClick()

    // Verify that the Save button is now enabled
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookEntryComp.language_field))
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.action_buttons).performClick()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.action_buttons).assertIsEnabled()
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Fill the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.isbn_field).performTextInput("1234567890")

    // Verify that the Save button remains disabled
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.action_buttons).assertIsNotEnabled()
  }

  @Test
  fun testDropdownMenuGenresOpensOnClick() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Simulate a click to open the genres dropdown menu
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_field).performClick()

    // Verify that dropdown menu items are displayed
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_menu + "_Fiction").assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_menu + "_Fantasy").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuGenresSelection() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Simulate a click to open the dropdown menu
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_field).performClick()

    // Select a specific genre
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_menu + "_Fiction").performClick()

    // Verify that the selected genre is now visible in the field
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_field).assertIsDisplayed()
  }

  @Test
  fun testLanguageDropdownOpensAndCloses() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookEntryComp.language_field))
    // Simulate a click to open the language dropdown menu
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).performClick()

    // Verify that several languages are displayed
    composeTestRule.onNodeWithText("French").assertIsDisplayed()
    composeTestRule.onNodeWithText("English").assertIsDisplayed()

    // Select a language
    composeTestRule.onNodeWithText("English").performClick()
    // close the dropdown
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).performClick()
  }
}
