package com.android.bookswap.ui.bookAddition

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.bookswap.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class BookAdditionChoiceScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var mockNavController: NavigationActions

  @Before
  fun setUp() {
    mockNavController = mock(NavigationActions::class.java)
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent { BookAdditionChoiceScreen(navController = mockNavController) }
    composeTestRule.onNodeWithTag("button_Manually").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_From ISBN").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_From Photo").assertIsDisplayed()
  }

  @Test
  fun hasClickableComponents() {
    composeTestRule.setContent { BookAdditionChoiceScreen(navController = mockNavController) }
    composeTestRule.onNodeWithTag("button_Manually").assertHasClickAction()
    composeTestRule.onNodeWithTag("button_From ISBN").assertHasClickAction()
    composeTestRule.onNodeWithTag("button_From Photo").assertHasClickAction()
  }

  @Test
  fun buttonManuallyHasCorrectContent() {
    composeTestRule.setContent { BookAdditionChoiceScreen(navController = mockNavController) }
    composeTestRule.onNodeWithTag("leftIcon_Manually", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("button_Manually").assertTextEquals("Manually")
    composeTestRule.onNodeWithTag("rightIcon_Manually", useUnmergedTree = true).assertExists()
  }

  @Test
  fun buttonISBNHasCorrectContent() {
    composeTestRule.setContent { BookAdditionChoiceScreen(navController = mockNavController) }
    composeTestRule.onNodeWithTag("leftPngIcon_From ISBN", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("button_From ISBN").assertTextEquals("From ISBN")
    composeTestRule.onNodeWithTag("rightIcon_From ISBN", useUnmergedTree = true).assertExists()
  }

  @Test
  fun buttonPhotoHasCorrectContent() {
    composeTestRule.setContent { BookAdditionChoiceScreen(navController = mockNavController) }
    composeTestRule.onNodeWithTag("leftPngIcon_From Photo", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("button_From Photo").assertTextEquals("From Photo")
    composeTestRule.onNodeWithTag("rightIcon_From Photo", useUnmergedTree = true).assertExists()
  }
}
