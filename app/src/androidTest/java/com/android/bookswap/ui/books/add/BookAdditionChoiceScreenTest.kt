package com.android.bookswap.ui.books.add

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.mockk
import java.util.UUID
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
          booksRepository = booksRepository,
          userUUID = UUID.randomUUID())
    }
    composeTestRule
        .onNodeWithTag("Manually" + C.Tag.NewBookChoice.btnWIcon.button)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("From ISBN" + C.Tag.NewBookChoice.btnWIcon.button)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("From Photo" + C.Tag.NewBookChoice.btnWIcon.button)
        .assertIsDisplayed()
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent {
      BookAdditionChoiceScreen(
          mockNavigationActions,
          photoFirebaseStorageRepository = mockPhotoFirebaseStorageRepository,
          booksRepository = booksRepository,
          userUUID = UUID.randomUUID())
    }
    composeTestRule
        .onNodeWithTag("Manually" + C.Tag.NewBookChoice.btnWIcon.button)
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("From ISBN" + C.Tag.NewBookChoice.btnWIcon.button)
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("From Photo" + C.Tag.NewBookChoice.btnWIcon.button)
        .assertHasClickAction()
  }
}
