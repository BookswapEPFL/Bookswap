package com.android.bookswap.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import coil.annotation.ExperimentalCoilApi
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.resources.C
import com.android.bookswap.ui.components.BookDisplayComponent
import com.android.bookswap.ui.components.BookListComponent
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Rule
import org.junit.Test

// import org.mockito.kotlin.verify

@ExperimentalCoilApi
class BookDisplayComponentTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displaysImage_whenPhotoUrlIsNotEmpty() {
    val testBook =
        DataBook(
            uuid = UUID.randomUUID(),
            title = "Test Book",
            author = "Test Author",
            description =
                "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
            rating = 3,
            photo = "https://example.com/photo.jpg", // Simulate a valid photo URL
            language = BookLanguages.SPANISH,
            isbn = "978-84-09025-23-5",
            genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
            userId = UUID.randomUUID(),
            false,
            false)

    composeTestRule.setContent { BookDisplayComponent(book = testBook) }

    // Verify the container is displayed
    composeTestRule.onNodeWithTag(C.Tag.BookDisplayComp.image).assertIsDisplayed()

    // Verify the image is displayed inside the container
    composeTestRule.onNodeWithTag(C.Tag.BookDisplayComp.image_picture).assertIsDisplayed()

    // Ensure the gray box is not displayed
    composeTestRule.onNodeWithTag(C.Tag.BookDisplayComp.image_gray_box).assertDoesNotExist()
  }

  @Test
  fun displaysGrayBox_whenPhotoUrlIsEmpty() {
    val testBook =
        DataBook(
            uuid = UUID.randomUUID(),
            title = "Test Book",
            author = "Test Author",
            description =
                "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
            rating = 3,
            photo = null, // No photo URL
            language = BookLanguages.SPANISH,
            isbn = "978-84-09025-23-5",
            genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
            userId = UUID.randomUUID(),
            false,
            false)

    composeTestRule.setContent { BookDisplayComponent(book = testBook) }

    // Verify the container is displayed
    composeTestRule.onNodeWithTag(C.Tag.BookDisplayComp.image).assertIsDisplayed()

    // Verify the gray box is displayed inside the container
    composeTestRule.onNodeWithTag(C.Tag.BookDisplayComp.image_gray_box).assertIsDisplayed()

    // Ensure the image is not displayed
    composeTestRule.onNodeWithTag(C.Tag.BookDisplayComp.image_picture).assertDoesNotExist()
  }

  @Test
  fun bookListComponent_navigatesToBookProfileOnClick() {
    // Arrange
    val navigationActions =
        mockk<NavigationActions>(relaxed = true) // Relaxed mock for automatic behavior
    val bookId = UUID.randomUUID()
    val testBook =
        listOf(
            DataBook(
                uuid = bookId, // Ensure this matches the book being tested
                title = "Test Book",
                author = "Test Author",
                description =
                    "Recuento de la historia de España desde los primeros pobladores hasta la actualidad.",
                rating = 3,
                photo = null, // No photo URL
                language = BookLanguages.SPANISH,
                isbn = "978-84-09025-23-5",
                genres = listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
                userId = UUID.randomUUID(),
                false,
                false))

    composeTestRule.setContent {
      BookListComponent(
          bookList = testBook,
          onBookClick = { clickedBookId ->
            navigationActions.navigateTo("${C.Screen.BOOK_PROFILE}/$clickedBookId")
          })
    }

    // Act
    composeTestRule
        .onNodeWithTag("0_" + C.Tag.BookDisplayComp.book_display_container)
        .performClick()

    // Assert
    verify { navigationActions.navigateTo("${C.Screen.BOOK_PROFILE}/$bookId") }
  }
}
