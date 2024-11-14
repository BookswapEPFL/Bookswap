package com.android.bookswap.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Route
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any

class NewUserScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var userVM: UserViewModel

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    userVM = mockk(relaxed = true)
  }

  @Test
  fun allComponentsAreDisplayedWithCorrectTexts() {
    composeTestRule.setContent { NewUserScreen(navigationActions, userVM) }
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
  fun clickOnCreateButtonWithInvalidEmailDoesNotNavigate() {

    composeTestRule.setContent { NewUserScreen(navigationActions, userVM) }


    composeTestRule.onNodeWithTag("greetingTF").performTextInput("Mr.")
    composeTestRule.onNodeWithTag("firstnameTF").performTextInput("John")
    composeTestRule.onNodeWithTag("lastnameTF").performTextInput("Doe")
    composeTestRule.onNodeWithTag("emailTF").performTextInput("john.doe.com") // Email invalide
    composeTestRule.onNodeWithTag("phoneTF").performTextInput("+4122345678")

    composeTestRule.onNodeWithTag("CreateButton").performClick()

    verify(exactly = 0) { navigationActions.navigateTo(Route.MAP) }

    composeTestRule
        .onNodeWithTag("emailError")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Invalid email format")
  }

  @Test
  fun clickOnCreateButtonWithEmptyFieldsShowsErrors() {
    justRun {
      userVM.updateUser(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    }
    composeTestRule.setContent { NewUserScreen(navigationActions, userVM) }

    composeTestRule.onNodeWithTag("greetingTF").performTextInput("")
    composeTestRule.onNodeWithTag("firstnameTF").performTextInput("")
    composeTestRule.onNodeWithTag("lastnameTF").performTextInput("")
    composeTestRule.onNodeWithTag("emailTF").performTextInput("notanemail")
    composeTestRule.onNodeWithTag("phoneTF").performTextInput("")

    composeTestRule.onNodeWithTag("CreateButton").performClick()

    verify(exactly = 0) { navigationActions.navigateTo(Route.MAP) }

    composeTestRule
        .onNodeWithTag("firstnameError")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("First name required")

    composeTestRule
        .onNodeWithTag("lastnameError")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Last name required")

    composeTestRule
        .onNodeWithTag("emailError")
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Invalid email format")
  }
}
