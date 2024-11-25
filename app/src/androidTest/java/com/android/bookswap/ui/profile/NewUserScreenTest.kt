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
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
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
        .onNodeWithTag(C.Tag.TopAppBar.screen_title)
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Welcome")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.personal_info)
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Please fill in your personal information to start BookSwapping")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.profile_pic)
        .assertExists()
        .assertIsDisplayed()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.greeting)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Greeting")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.firstname)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Firstname")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.lastname)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Lastname")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.email)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Email")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.phone)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Phone")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.confirm)
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Create")
        .assertHasClickAction()
  }

  @Test
  fun clickOnCreateButtonWithInvalidEmailDoesNotNavigate() {

    composeTestRule.setContent { NewUserScreen(navigationActions, userVM) }

    composeTestRule.onNodeWithTag(C.Tag.NewUser.greeting).performTextInput("Mr.")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.firstname).performTextInput("John")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.lastname).performTextInput("Doe")
    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.email)
        .performTextInput("john.doe.com") // Email invalide
    composeTestRule.onNodeWithTag(C.Tag.NewUser.phone).performTextInput("+4122345678")

    composeTestRule.onNodeWithTag(C.Tag.NewUser.confirm).performClick()

    verify(exactly = 0) { navigationActions.navigateTo(C.Route.MAP) }

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.email_error)
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

    composeTestRule.onNodeWithTag(C.Tag.NewUser.greeting).performTextInput("")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.firstname).performTextInput("")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.lastname).performTextInput("")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.email).performTextInput("notanemail")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.phone).performTextInput("")

    composeTestRule.onNodeWithTag(C.Tag.NewUser.confirm).performClick()

    verify(exactly = 0) { navigationActions.navigateTo(C.Route.MAP) }

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.firstname_error)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("First name required")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.lastname_error)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Last name required")

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.email_error)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Invalid email format")
  }
}
