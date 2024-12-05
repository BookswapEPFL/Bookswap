package com.android.bookswap.ui.books.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.model.edit.EditBookViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.navigation.BottomNavigationMenu
import com.android.bookswap.ui.navigation.List_Navigation_Bar_Destinations
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditBookScreenTest {

  private val mockViewModel: EditBookViewModel = mockk()

  @MockK private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  private val sampleBook =
      DataBook(
          UUID.randomUUID(),
          "Sample Book",
          "Sample Author",
          "Sample Description",
          4,
          "sample_photo_url",
          BookLanguages.ENGLISH,
          "123456789",
          listOf(BookGenres.FANTASY),
          UUID.randomUUID(),
          false,
          false)

  @Before
  fun setUp() {
    val topAppBar =
      @Composable { s: String? ->
        TopAppBarComponent(
          modifier = Modifier,
          navigationActions = navigationActions,
          title = s ?: navigationActions.currentRoute())
      }
    val bottomAppBar =
      @Composable { s: String? ->
        BottomNavigationMenu(
          onTabSelect = { destination -> navigationActions.navigateTo(destination) },
          tabList = List_Navigation_Bar_Destinations,
          selectedItem = s ?: "")
      }
    MockKAnnotations.init(this)
    every { mockViewModel.deleteBooks(any(), any()) } just runs
    every {
      mockViewModel.updateDataBook(
          any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs

    every { navigationActions.currentRoute() } returns "EDIT_BOOK"
    composeTestRule.setContent { EditBookScreen(mockViewModel, sampleBook,topAppBar = { topAppBar("Edit your Book") }, bottomAppBar = {}) }
  }

  @Test
  fun displayAllComponent() {
    composeTestRule.onNodeWithTag(C.Tag.edit_book_screen_container).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertIsDisplayed()
  }

  @Test
  fun displayEditTitleValueComponent() {
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.screen_title).assertTextEquals("Edit your Book")
  }

  @Test
  fun displayEditButtonComponent() {
    composeTestRule.onNodeWithTag(C.Tag.TopAppBar.back_button).assertIsDisplayed()
  }

  @Test
  fun displayEditSaveValueComponent() {
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.scrollable).performScrollToNode(hasTestTag(C.Tag.BookEntryComp.action_buttons))

    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.action_buttons).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.action_buttons).assertTextEquals("Save")
  }

  @Test
  fun displayEditDeleteValueComponent() {
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.scrollable).performScrollToNode(hasTestTag(C.Tag.BookEntryComp.cancel_button))

    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.cancel_button).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.cancel_button).assertTextEquals("Delete")
  }

  @Test
  fun displayEditBookTitleComponent() {

    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.title_field).assertIsDisplayed()
  }

  @Test
  fun displayEditBookAuthorComponent() {
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.author_field).assertIsDisplayed()
  }

  @Test
  fun displayEditBookDescriptionComponent() {
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.description_field).assertIsDisplayed()
  }

  @Test
  fun displayEditBookRatingComponent() {
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.rating_field).assertIsDisplayed()
  }

  @Test
  fun displayEditBookPhotoComponent() {
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.photo_field).assertIsDisplayed()
  }

  @Test
  fun displayEditBookLanguageComponent() {
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.language_field).assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {

    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.title_field).assertTextContains(sampleBook.title)
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.author_field).assertTextContains(sampleBook.author ?: "")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.description_field)
        .assertTextContains(sampleBook.description ?: "")
    composeTestRule.onNodeWithTag(C.Tag.BookEntryComp.photo_field).assertTextContains(sampleBook.photo ?: "")
    composeTestRule
        .onNodeWithTag(C.Tag.BookEntryComp.language_field).performScrollTo()
      .assertExists()
  }
}
