package com.android.bookswap.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewUserScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var userVM: UserViewModel
  private lateinit var photoStorage: PhotoFirebaseStorageSource

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    userVM = mockk(relaxed = true)
    photoStorage = mockk(relaxed = true)
    composeTestRule.setContent { NewUserScreen(navigationActions, photoStorage) }
  }

  @Test
  fun allComponentsAreDisplayedWithCorrectTexts() {
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
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.confirm))

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.confirm)
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Create")
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag(C.Tag.AddressFields.address)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Street")

    composeTestRule
        .onNodeWithTag(C.Tag.AddressFields.city)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("City")

    composeTestRule
        .onNodeWithTag(C.Tag.AddressFields.postal)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("PLZ")

    composeTestRule
        .onNodeWithTag(C.Tag.AddressFields.canton)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Region")

    composeTestRule
        .onNodeWithTag(C.Tag.AddressFields.country)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Country")
  }

  @Test
  fun clickOnCreateButtonWithInvalidEmailDoesNotNavigate() {

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.confirm))

    composeTestRule.onNodeWithTag(C.Tag.NewUser.confirm).performClick()

    composeTestRule
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.greeting))

    composeTestRule.onNodeWithTag(C.Tag.NewUser.greeting).performTextInput("Mr.")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.firstname).performTextInput("John")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.lastname).performTextInput("Doe")
    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.email)
        .performTextInput("john.doe.com") // Email invalide
    composeTestRule.onNodeWithTag(C.Tag.NewUser.phone).performTextInput("+4122345678")

    composeTestRule
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.confirm))

    composeTestRule.onNodeWithTag(C.Tag.AddressFields.city).performTextInput("Lausanne")
    composeTestRule.onNodeWithTag(C.Tag.AddressFields.country).performTextInput("Switzerland")

    composeTestRule.onNodeWithTag(C.Tag.NewUser.confirm).performClick()

    verify(exactly = 0) { navigationActions.navigateTo(C.Route.MAP) }

    composeTestRule
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.email))

    composeTestRule
        .onNodeWithTag(C.Tag.NewUser.email_error)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("Invalid email format")
  }

  @Test
  fun clickOnCreateButtonWithInvalidAddressDoesNotNavigate() {
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.confirm))

    composeTestRule.onNodeWithTag(C.Tag.NewUser.confirm).performClick()

    composeTestRule
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.greeting))

    composeTestRule.onNodeWithTag(C.Tag.NewUser.greeting).performTextInput("Mr.")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.firstname).performTextInput("John")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.lastname).performTextInput("Doe")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.email).performTextInput("john.doe@gmail.com")
    composeTestRule.onNodeWithTag(C.Tag.NewUser.phone).performTextInput("+4122345678")

    composeTestRule
        .onNodeWithTag(C.Tag.new_user_screen_container)
        .performScrollToNode(hasTestTag(C.Tag.NewUser.confirm))

    composeTestRule.onNodeWithTag(C.Tag.AddressFields.country).performTextInput("Switzerland")

    composeTestRule.onNodeWithTag(C.Tag.NewUser.confirm).performClick()

    verify(exactly = 0) { navigationActions.navigateTo(C.Route.MAP) }

    composeTestRule
        .onNodeWithTag(C.Tag.AddressFields.cityError)
        .assertExists()
        .assertIsDisplayed()
        .assertTextContains("City required")
  }
}
