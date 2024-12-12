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
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.model.add.AddToBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
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
      mockViewModel.saveDataBook(
          any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavigationActions(navController)

      AddToBookScreen(mockViewModel)
    }

    // Mock Toast messages for testing purposes
    mockkStatic(Toast::class)
    val toastMock = mockk<Toast>()
    every { toastMock.show() } returns Unit
    every { Toast.makeText(any(), any<String>(), any()) } returns toastMock
  }

  @Test
  fun testSaveButtonDisabledInitially() {
    // Check if the Save button is initially disabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsNotEnabled()
  }

  @Test
  fun testSaveButtonEnabledWhenRequiredFieldsAreFilled() {
    // Fill in the Title and ISBN fields
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.title).performTextInput("My Book Title")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.isbn).performTextInput("978-3-16-148410-0")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.author).performTextInput("My Book Author")
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()
    composeTestRule.onNodeWithText("French").performClick()
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).performClick()
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsEnabled()
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.isbn).performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsNotEnabled()
  }

  @Test
  fun testDropdownMenuIsInitiallyClosed() {
    // Verify that the dropdown menu is initially not expanded
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).assertIsDisplayed()
    composeTestRule.onNodeWithText("Language*").assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuOpensOnClick() {
    // Simulate clicking the dropdown to expand it
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

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
    // Expand the dropdown menu:
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

    // Click on a specific language ("English")
    composeTestRule.onNodeWithText("English").performClick()

    // Verify the language field updates with the selected language
    composeTestRule.onNodeWithText("English").assertExists()
  }

  @Test
  fun testDropdownMenuClosesAfterSelection() {
    // Expand the dropdown menu
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.language).performClick()

    // Select a language to close the dropdown
    composeTestRule.onNodeWithText("English").performClick()

    // Ensure the dropdown items are no longer displayed (here we juste looks that Italian is not
    // visible)
    composeTestRule.onNodeWithText("Italian").assertDoesNotExist()
  }
}
