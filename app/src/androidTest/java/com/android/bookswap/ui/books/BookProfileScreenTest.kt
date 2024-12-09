package com.android.bookswap.ui.books

import android.annotation.SuppressLint
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.action.ViewActions.swipeUp
import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.coEvery
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockNavController: NavigationActions
  private lateinit var mockBookRepo: BooksRepository
  private val testBookId = UUID.randomUUID()
  private val modTestBookId = UUID.randomUUID()
  private val currentUserId = UUID.randomUUID()
  private lateinit var modTestBook: DataBook

  private val testBook =
      DataBook(
          testBookId,
          "Historia de España",
          "Jose Ignacio Pastor Iglesias",
          """
    Recuento de la historia de España desde los primeros pobladores hasta la actualidad. 
    Este libro abarca desde las antiguas civilizaciones ibéricas y celtas hasta la llegada 
    de los romanos y la posterior invasión de los visigodos. Se examinan en detalle los 
    ocho siglos de ocupación musulmana, con especial atención a figuras como Abderramán III 
    y la esplendorosa época de Al-Ándalus. 
    
    También se analizan las cruzadas cristianas para recuperar el territorio, conocidas como 
    la Reconquista, que culminaron en 1492 con la toma de Granada por los Reyes Católicos. 
    Este evento marcó el inicio de una nueva era para España, incluyendo el descubrimiento 
    de América por Cristóbal Colón y el auge del Imperio Español durante los siglos XVI y XVII.
    
    El libro continúa con las guerras napoleónicas, el colapso del Antiguo Régimen y la 
    instauración de la Primera República. Se detallan los turbulentos años de la Guerra Civil 
    Española (1936-1939) y el posterior régimen franquista. Finalmente, se explora la 
    Transición Democrática y el ascenso de España como una nación moderna dentro de la 
    Unión Europea.
    
    Cada capítulo incluye ilustraciones, mapas y cronologías detalladas para facilitar 
    el estudio de los eventos históricos más importantes. Es una lectura obligatoria 
    para cualquier persona interesada en comprender la rica y compleja historia de España, 
    llena de conquistas, descubrimientos, conflictos y reformas.
    """
              .trimIndent(),
          9,
          null,
          BookLanguages.SPANISH,
          "978-84-09025-23-5",
          listOf(BookGenres.HISTORICAL, BookGenres.NONFICTION, BookGenres.BIOGRAPHY),
          currentUserId,
          true,
          true)

  @Before
  fun setUp() {
    mockNavController = mockk()
    mockBookRepo = mockk()

    modTestBook =
        testBook.copy(
            uuid = modTestBookId, description = "Historia de España", photo = "new_photo_url")

    // Mocking the getBook call to return the test book
    coEvery { mockBookRepo.getBook(any(), any(), any()) } answers
        {
          val bookId = it.invocation.args[0] as UUID
          val onSuccess = it.invocation.args[1] as (DataBook) -> Unit
          if (bookId == testBookId) {
            onSuccess(testBook)
          } else if (bookId == modTestBookId) {
            onSuccess(modTestBook)
          }
        }
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBookId, mockBookRepo, navigationActions)
    }

    composeTestRule.onNodeWithTag(C.Tag.BookProfile.imagePlaceholder).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.title).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.author).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.location))
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.language).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.genres).assertIsDisplayed()
    testBook.genres.forEach { genre ->
      composeTestRule.onNodeWithTag(genre.Genre + C.Tag.BookProfile.genre).assertIsDisplayed()
    }
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.isbn).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.date).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.volume).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.issue).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.editorial).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.location).assertIsDisplayed()
  }

  @Test
  fun correctImageIsShownWhenBookHasPhoto() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(modTestBookId, mockBookRepo, navigationActions)
    }

    composeTestRule.onNodeWithTag(C.Tag.BookProfile.image).assertIsDisplayed()
  }

  @SuppressLint("CheckResult")
  @Test
  fun descriptionBoxIsScrollable() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      BookProfileScreen(testBookId, mockBookRepo, navigationActions)
    }

    // Check that the "Synopsis" title is displayed
    composeTestRule.onNodeWithTag(C.Tag.BookProfile.synopsis_label).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.scrollable)
        .performScrollToNode(hasTestTag(C.Tag.BookProfile.synopsis))

    // Check that the description box is displayed
    composeTestRule
        .onNodeWithTag(C.Tag.BookProfile.synopsis, useUnmergedTree = true)
        .assertIsDisplayed()

    // Check that the description box is scrollable
    val scrollNode = composeTestRule.onNodeWithTag(C.Tag.BookProfile.synopsis)

    // Assertion to verify scrolling works by interacting with the scrollable area
    scrollNode.performTouchInput { swipeUp() }

    // Optionally, check that after scrolling, the scroll bar is still visible
    scrollNode.assertIsDisplayed()
  }
}
