package com.android.bookswap.ui.books.add

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class BookAdditionChoiceScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private val mockNavigationActions: NavigationActions = mockk()
  private val mockPhotoFirebaseStorageRepository: PhotoFirebaseStorageRepository = mockk()
  private val booksRepository: BooksRepository = mockk()

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent {
      BookAdditionChoiceScreen(
          mockNavigationActions,
          photoFirebaseStorageRepository = mockPhotoFirebaseStorageRepository,
          booksRepository = booksRepository)
    }
    composeTestRule.onNodeWithTag("button_Manually").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_From ISBN").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_From Photo").assertIsDisplayed()
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent {
      BookAdditionChoiceScreen(
          mockNavigationActions,
          photoFirebaseStorageRepository = mockPhotoFirebaseStorageRepository,
          booksRepository = booksRepository)
    }
    composeTestRule.onNodeWithTag("button_Manually").assertHasClickAction()
    composeTestRule.onNodeWithTag("button_From ISBN").assertHasClickAction()
    composeTestRule.onNodeWithTag("button_From Photo").assertHasClickAction()
  }
}
