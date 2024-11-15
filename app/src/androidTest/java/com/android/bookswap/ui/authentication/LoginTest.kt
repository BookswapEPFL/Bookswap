package com.android.bookswap.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.navigation.Screen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
  private lateinit var userVM: UserViewModel
  private lateinit var mocknavi: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()
  // The IntentsTestRule may not reliable.
  @Before
  fun setUp() {
    Intents.init()
    userVM = mockk(relaxed = true)
    mocknavi = mockk(relaxed = true)
  }

  // Release Intents after each test
  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    every { userVM.isStored } returns MutableStateFlow(null)
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      SignInScreen(navigationActions, userVM)
    }
    composeTestRule.onNodeWithTag("login_loginTitle1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("login_loginTitle1").assertTextEquals("Welcome to")
    composeTestRule.onNodeWithTag("login_loginTitle2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("login_loginTitle2").assertTextEquals("BookSwap")

    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    every { userVM.isStored } returns MutableStateFlow(null)
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      SignInScreen(navigationActions, userVM)
    }
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.waitForIdle()
    // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    intended(toPackage("com.google.android.gms"))
  }

  @Test
  fun navigateToNewUserScreenWhenUserIsNotStored() {
    every { userVM.isStored } returns MutableStateFlow(false)
    composeTestRule.setContent { SignInScreen(mocknavi, userVM) }
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.waitForIdle()
    verify { mocknavi.navigateTo(Screen.NEW_USER) }
  }
}