package com.android.bookswap.ui.bookAddition

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
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
}