package com.android.bookswap.ui.books.add

import android.widget.Toast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
}
