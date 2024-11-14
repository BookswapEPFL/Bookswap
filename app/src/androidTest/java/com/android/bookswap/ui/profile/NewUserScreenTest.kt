package com.android.bookswap.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Route
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewUserScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
  }

  @Test
  fun allComponentsAreDisplayedWithCorrectTexts() {
    composeTestRule.setContent { NewUserScreen(navigationActions) }
    composeTestRule
        .onNodeWithTag("welcomeTxt")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Welcome")

    composeTestRule
        .onNodeWithTag("personalInfoTxt")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Please fill in your personal information to start BookSwapping")

    composeTestRule
        .onNodeWithTag("profilPics")
        .assertExists()
        .assertIsDisplayed()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag("greetingTF")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Greeting")

    composeTestRule
        .onNodeWithTag("firstnameTF")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Firstname")

    composeTestRule
        .onNodeWithTag("lastnameTF")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Lastname")

    composeTestRule
        .onNodeWithTag("emailTF")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Email")

    composeTestRule
        .onNodeWithTag("phoneTF")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Phone")

    composeTestRule
        .onNodeWithTag("CreateButton")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Create")
        .assertHasClickAction()
  }

  @Test
  fun clickOnCreateButtonNavigatesToMap() {
    composeTestRule.setContent { NewUserScreen(navigationActions) }

    composeTestRule.onNodeWithTag("CreateButton").performClick()
    verify { navigationActions.navigateTo(Route.MAP) }
  }
}
