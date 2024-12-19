package com.android.bookswap.ui.books.add

import android.widget.Toast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.model.add.AddToBookViewModel
import com.android.bookswap.model.isNetworkAvailable
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
  private lateinit var photoStorage: PhotoFirebaseStorageRepository

  @Before
  fun init() {
    // Mock the ViewModel save method to run without side effects
    photoStorage = mockk()
    every { photoStorage.addPhotoToStorage(any(), any(), any()) } just runs
    every {
      mockViewModel.saveDataBook(
          any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavigationActions(navController)

      AddToBookScreen(mockViewModel, photoStorage)
    }
    mockkStatic(::isNetworkAvailable)
    every { isNetworkAvailable(any()) } returns true
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
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.title_field).performTextInput("My Book Title")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.isbn_field)
        .performTextInput("978-3-16-148410-0")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.author_field)
        .performTextInput("My Book Author")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_field).performClick()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.genre_menu + "_Fantasy").performClick()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).performClick()
    composeTestRule.onNodeWithText("French").performClick()
    // Check if the Save button is now enabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).performClick()
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsEnabled()
  }

  @Test
  fun testSaveButtonDisabledWhenTitleIsEmpty() {
    // Fill in the ISBN field but leave the Title field empty
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.isbn_field).performTextInput("1234567890")

    // Check if the Save button is still disabled
    composeTestRule.onNodeWithTag(C.Tag.NewBookManually.save).assertIsNotEnabled()
  }
}
