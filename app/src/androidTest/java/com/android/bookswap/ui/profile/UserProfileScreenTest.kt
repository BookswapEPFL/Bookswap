package com.android.bookswap.ui.profile

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.DataUser
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.screen.UserProfileScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UserProfileScreenTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    val userVM = UserViewModel("email@example.com")

    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            0.0,
            0.0,
            "dummyPic.png",
            "dummyUUID0000"))
    composeTestRule.setContent { UserProfile(userVM = userVM) }
  }

  @Test
  fun testDisplay() {
    run(testName = "assertContent") {
      step("Start User Profile Screen") {
        ComposeScreen.onComposeScreen<UserProfileScreen>(composeTestRule) {
          titleTxt {
            assertIsDisplayed()
            assertTextEquals("Your Profile")
          }
          fullNameTxt {
            assertIsDisplayed()
            assertTextEquals("M. John Doe")
          }
          emailTxt {
            assertIsDisplayed()
            assertTextEquals("John.Doe@example.com")
          }
          phoneNumberTxt {
            assertIsDisplayed()
            assertTextEquals("+41223456789")
          }
          addressTxt {
            assertIsDisplayed()
            assertTextEquals("0.0, 0.0")
          }
          editProfileBtn {
            assertIsDisplayed()
            assertIsEnabled()
          }
        }
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testEdit() {
    run(testName = "assertEditAction") {
      ComposeScreen.onComposeScreen<UserProfileScreen>(composeTestRule) {
        step("Start User Profile Screen") {
          titleTxt {
            assertIsDisplayed()
            assertTextEquals("Your Profile")
          }
          fullNameTxt {
            assertIsDisplayed()
            assertTextEquals("M. John Doe")
          }
          emailTxt {
            assertIsDisplayed()
            assertTextEquals("John.Doe@example.com")
          }
          phoneNumberTxt {
            assertIsDisplayed()
            assertTextEquals("+41223456789")
          }
          addressTxt {
            assertIsDisplayed()
            assertTextEquals("0.0, 0.0")
          }
          editProfileBtn {
            assertIsDisplayed()
            assertIsEnabled()
            assertHasClickAction()
          }
        }
        step("Click edit button") {
          editProfileBtn {
            assertIsDisplayed()
            assertIsEnabled()
            assertHasClickAction()
            performClick()
          }
        }
      }
    }
  }
}
