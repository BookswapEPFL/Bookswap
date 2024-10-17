package com.android.bookswap.ui.books.add

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ISBNAddTest : TestCase() {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun elementsAreDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            AddISBNScreen(navigationActions)
        }

        val isbnField = composeTestRule.onNodeWithTag("isbn_field")
        isbnField.assertIsDisplayed()
        Assert.assertEquals(
            "ISBN*", isbnField.fetchSemanticsNode().config[SemanticsProperties.Text][0].text)

        composeTestRule.onNodeWithTag("isbn_searchButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("isbn_searchButton").assertHasClickAction()
    }

    @Test
    fun isbnFieldWork() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            AddISBNScreen(navigationActions)
        }
        val isbnField = composeTestRule.onNodeWithTag("isbn_field")

        isbnField.performTextInput("testEmpty")
        Assert.assertEquals(
            "", isbnField.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)

        isbnField.performTextClearance()
        isbnField.performTextInput("12845")
        Assert.assertEquals(
            "12845", isbnField.fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
    }
}
