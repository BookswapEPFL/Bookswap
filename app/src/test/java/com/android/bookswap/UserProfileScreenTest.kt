package com.android.bookswap

import android.location.Address
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.User
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.screen.UserProfileScreen
import com.android.bookswap.ui.profile.UserProfile
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.util.Locale
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
    val address = Address(Locale.getDefault())
    address.countryCode = "CH"
    address.locality = "Lausanne"
    address.postalCode = "1000"
    address.countryName = "Switzerland"
    address.setAddressLine(0, "Rue de la Gare 1")

    userVM.updateUser(
        User(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
            "dummyPic.png",
            "dummyUUID0000"))
    composeTestRule.setContent { UserProfile(userVM = userVM) }
  }

  @Test
  fun testDisplay() =
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
              assertTextEquals("Rue de la Gare 1, 1000 Lausanne CH, Switzerland")
            }
            editProfileBtn {
              assertIsDisplayed()
              assertIsEnabled()
            }
          }
        }
      }

  @Test
  fun testEdit() =
      run(testName = "assertEditAction") {
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
              assertTextEquals("Rue de la Gare 1, 1000 Lausanne CH, Switzerland")
            }
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
