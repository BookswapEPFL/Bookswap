package com.android.bookswap.ui.profile

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.DefaultMockKs
import com.android.bookswap.data.source.network.PhotoFirebaseStorageSource
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.screen.UserProfileScreen
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
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

  private lateinit var photoStorage: PhotoFirebaseStorageSource
  private lateinit var userVM: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    android.util.Log.wtf("TAG_SETUP", "test setup")
    userVM = DefaultMockKs.mockKUserViewModel

    photoStorage = mockk(relaxed = true)

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      CompositionLocalProvider(LocalAppConfig provides AppConfig(userViewModel = userVM)) {
        UserProfile(
            photoStorage = photoStorage,
            { TopAppBarComponent(Modifier, navigationActions, "Your Profile") })
      }
    }
  }

  @Test
  fun testDisplay() {
    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.fullname)
        .assertExists()
        .assertTextEquals("M. John Doe")
    val str =
        "${composeTestRule.onNodeWithTag(C.Tag.UserProfile.fullname).fetchSemanticsNode().config}"
    android.util.Log.d("TAG_USER_PROFILE_TEST", str)
    run("assertContent") {
      step("Start User Profile Screen") {
        try {
          composeTestRule
              .onNodeWithTag(C.Tag.UserProfile.fullname)
              .assertExists()
              .assertTextEquals("M. John Doe")
        } catch (e: Error) {
          assert(str.contains("Joe")).also { throw e }
        }

        ComposeScreen.onComposeScreen<UserProfileScreen>(composeTestRule) {
          titleTxt { assertIsDisplayed() }
          fullNameTxt {
            assertIsDisplayed()
            // assertTextEquals("M. John Doe")
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
            assertTextEquals("address")
          }
          editProfileBtn {
            assertIsDisplayed()
            assertIsEnabled()
          }
        }
      }
    }
  }

  @Test
  fun testTakePhoto() {
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.profileImage).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.profileImage).performClick()
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.profileImageBox).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.UserProfile.take_photo).assertExists()
  }

  @Test
  fun testEdit() {
    composeTestRule
        .onNodeWithTag(C.Tag.UserProfile.fullname)
        .assertExists()
        .assertTextEquals("M. John Doe")
    run("assertEditAction") {
      ComposeScreen.onComposeScreen<UserProfileScreen>(composeTestRule) {
        step("Start User Profile Screen") {
          fullNameTxt {
            assertIsDisplayed()
            // assertTextEquals("M. John Doe")
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
            assertTextEquals("address")
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
