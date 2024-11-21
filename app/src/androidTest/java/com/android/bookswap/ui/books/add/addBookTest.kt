package com.android.bookswap.ui.books.add

import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.model.add.AddToBookViewModel
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
    every {
      mockViewModel.saveDataBook(any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs
    mockkStatic(Toast::class)
    val toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock
  }

  @Test
  fun testSaveButtonDisabledInitially() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithTag("save_button").assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithTag("title_field").performTextInput("My Book Title")
    composeTestRule.onNodeWithTag("author_field").performTextInput("author")
    composeTestRule.onNodeWithTag("language_field").performTextInput("French")
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithTag("save_button").performClick()
    composeTestRule.onNodeWithTag("save_button").assertIsEnabled()
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithTag("isbn_field").performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithTag("save_button").assertIsNotEnabled()
  }

  @Test
  fun testDropdownMenuIsInitiallyClosed() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Verify that the dropdown menu is initially not expanded
    composeTestRule.onNodeWithTag("language_field").assertIsDisplayed()
    composeTestRule.onNodeWithText("Language*").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuOpensOnClick() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Simulate clicking the dropdown to expand it
    composeTestRule.onNodeWithTag("language_field").performClick()

    // Verify that dropdown items are displayed:
    composeTestRule.onNodeWithText("French").assertIsDisplayed()
    composeTestRule.onNodeWithText("German").assertIsDisplayed()
    composeTestRule.onNodeWithText("English").assertIsDisplayed()
    composeTestRule.onNodeWithText("Spanish").assertIsDisplayed()
    composeTestRule.onNodeWithText("Italian").assertIsDisplayed()
    composeTestRule.onNodeWithText("Romansh").assertIsDisplayed()
    composeTestRule.onNodeWithText("Other").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuItemSelection() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Expand the dropdown menu:
    composeTestRule.onNodeWithTag("language_field").performClick()

    // Click on a specific language ("English")
    composeTestRule.onNodeWithText("English").performClick()

    // Verify the language field updates with the selected language
    composeTestRule.onNodeWithText("English").assertExists()
  }

  @Test
  fun testDropdownMenuClosesAfterSelection() {
    composeTestRule.setContent { AddToBookScreen(mockViewModel) }

    // Expand the dropdown menu
    composeTestRule.onNodeWithTag("language_field").performClick()

    // Select a language to close the dropdown
    composeTestRule.onNodeWithText("English").performClick()

    // Ensure the dropdown items are no longer displayed (here we juste looks that Italian is not
    // visible)
    composeTestRule.onNodeWithText("Italian").assertDoesNotExist()
  }
}
