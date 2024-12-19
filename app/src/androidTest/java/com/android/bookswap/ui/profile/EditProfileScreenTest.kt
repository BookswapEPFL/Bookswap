package com.android.bookswap.ui.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.resources.C
import com.android.bookswap.screen.EditProfileScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val START_COORDINATES = 0.0
const val SHORT_LIST_COORDINATES = 1.0
const val LONG_LIST_COORDINATES = 2.0

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockUserViewModel: UserViewModel = mockk(relaxed = true)
  private lateinit var mockContext: Context
  private var latitude: Double = START_COORDINATES
  private var longitude: Double = START_COORDINATES

  @Before
  fun setup() {
    mockContext = mockk()
    every { mockUserViewModel.uuid } returns UUID.randomUUID()
    every { mockUserViewModel.updateCoordinates(any<List<String>>(), any(), any()) } answers
        {
          val addressList = it.invocation.args[0] as List<*>
          when (addressList.size) {
            2 -> {
              latitude = SHORT_LIST_COORDINATES
              longitude = SHORT_LIST_COORDINATES
            }
            5 -> {
              latitude = LONG_LIST_COORDINATES
              longitude = LONG_LIST_COORDINATES
            }
            else -> {
              latitude = START_COORDINATES
              longitude = START_COORDINATES
            }
          }
        }
  }

  @Test
  fun testDisplay() =
      run(testName = "test alert display") {
        composeTestRule.setContent {
          CompositionLocalProvider(
              LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
                EditProfileDialog(
                    mockContext,
                    { Log.d("EditProfileTest_Dismiss", "User info discarded") },
                    { Log.d("EditProfileTest_Save", "User info saved ${it.printFullname()}") },
                )
              }
        }
        step("try displaying the alert box") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }

            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Greeting", true)
              performTextClearance()
              assertTextContains("Mr.", true)
            }

            closeSoftKeyboard()

            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Firstname", true)
              performTextClearance()
              assertTextContains("John", true)
            }

            closeSoftKeyboard()

            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Lastname", true)
              performTextClearance()
              assertTextContains("Doe", true)
            }

            closeSoftKeyboard()

            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Email", true)
              performTextClearance()
              assertTextContains("John.Doe@example.com", true)
            }

            closeSoftKeyboard()

            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Phone", true)
              performTextClearance()
              assertTextContains("+4122345678", true)
            }

            closeSoftKeyboard()

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            streetBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Street", true)
              performTextClearance()
              assertTextContains("Avenue de la Gare 20", true)
            }

            closeSoftKeyboard()

            cityBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("City", true)
              performTextClearance()
              assertTextContains("Lausanne", true)
            }

            closeSoftKeyboard()

            postalBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("PLZ", true)
              performTextClearance()
              assertTextContains("1003", true)
            }

            closeSoftKeyboard()

            cantonBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Region", true)
              performTextClearance()
              assertTextContains("Vaud", true)
            }

            closeSoftKeyboard()

            countryBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Country", true)
              performTextClearance()
              assertTextContains("Switzerland", true)
            }

            closeSoftKeyboard()

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
      }

  @Test
  fun testEdit() =
      run(testName = "test alert edit") {
        composeTestRule.setContent {
          CompositionLocalProvider(
              LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
                EditProfileDialog(
                    mockContext,
                    { Log.d("EditProfileTest_Dismiss", "User info discarded") },
                    { Log.d("EditProfileTest_Save", "User info saved ${it.printFullname()}") })
              }
        }
        step("try editing the textbox values") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }

            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextContains("Greeting", true)
              performTextInput("Mr.")
              assertTextContains("Mr.", true)
            }

            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextContains("Firstname", true)
              performTextInput("Jones")
              assertTextContains("Jones", true)
            }

            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextContains("Lastname", true)
              performTextInput("Douses")
              assertTextContains("Douses", true)
            }

            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextContains("Email", true)
              performTextInput("Jones.Douses@example.com")
              assertTextContains("Jones.Douses@example.com", true)
            }

            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextContains("Phone", true)
              performTextInput("+41234567890")
              assertTextContains("+41234567890", true)
            }

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            streetBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Street", true)
              performTextClearance()
              performTextInput("Avenue de la Gare 19")
              assertTextContains("Avenue de la Gare 19", true)
            }

            closeSoftKeyboard()

            cityBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("City", true)
              performTextClearance()
              performTextInput("Ecublens")
              assertTextContains("Ecublens", true)
            }

            closeSoftKeyboard()

            postalBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("PLZ", true)
              performTextClearance()
              performTextInput("1002")
              assertTextContains("1002", true)
            }

            closeSoftKeyboard()

            cantonBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Region", true)
              performTextClearance()
              performTextInput("Fribourg")
              assertTextContains("Fribourg", true)
            }

            closeSoftKeyboard()

            countryBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Country", true)
              performTextClearance()
              performTextInput("Liechtenstein")
              assertTextContains("Liechtenstein", true)
            }

            closeSoftKeyboard()

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
      }

  @Test
  fun testEditConfirm() =
      run(testName = "test edit and confirm") {
        composeTestRule.setContent {
          CompositionLocalProvider(
              LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
                EditProfileDialog(
                    mockContext,
                    { Log.d("EditProfileTest_Dismiss", "User info discarded") },
                    { Log.d("EditProfileTest_Save", "User info saved ${it.printFullname()}") })
              }
        }
        step("try editing the textbox values then confirming the changes") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }
            greetingTbx {
              performTextClearance()
              performTextInput("Mr.")
              assertTextEquals("Greeting", "Mr.")
            }
            firstnameTbx {
              performTextClearance()
              performTextInput("Jones")
              assertTextEquals("Firstname", "Jones")
            }
            lastnameTbx {
              performTextClearance()
              performTextInput("Douses")
              assertTextEquals("Lastname", "Douses")
            }
            emailTbx {
              performTextClearance()
              performTextInput("Jones.Douses@example.com")
              assertTextEquals("Email", "Jones.Douses@example.com")
            }
            phoneNumberTbx {
              performTextClearance()
              performTextInput("+41234567890")
              assertTextEquals("Phone", "+41234567890")
            }

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("try pressing on confirm") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            step("") {

              // Scroll to Title
              composeTestRule
                  .onNodeWithTag(C.Tag.edit_profile_screen_container)
                  .performScrollToNode(hasTestTag(C.Tag.TopAppBar.screen_title))

              titleTxt {
                assertIsDisplayed()
                assertTextEquals("Edit Profile")
              }
              greetingTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Greeting", "Mr.")
              }
              firstnameTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Firstname", "Jones")
              }
              lastnameTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Lastname", "Douses")
              }
              emailTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Email", "Jones.Douses@example.com")
              }
              phoneNumberTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Phone", "+41234567890")
              }

              // Scroll to Confirm Button
              composeTestRule
                  .onNodeWithTag(C.Tag.edit_profile_screen_container)
                  .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

              confirmBtn {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Save")
              }
              dismissBtn {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Cancel")
              }
            }
            step("") {
              confirmBtn {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Save")
                performClick()
              }
            }
          }
        }
      }

  @Test
  fun testEditDismiss() =
      run(testName = "test alert display") {
        composeTestRule.setContent {
          CompositionLocalProvider(
              LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
                EditProfileDialog(
                    mockContext,
                    { Log.d("EditProfileTest_Dismiss", "User info discarded") },
                    { Log.d("EditProfileTest_Save", "User info saved ${it.printFullname()}") })
              }
        }
        step("try editing the textbox values then cancelling the changes") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }
            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Mr.")
            }
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones")
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Douses")
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones.Douses@example.com")
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("+41234567890")
            }

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("test if the values are edited") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {

            // Scroll to Title
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.TopAppBar.screen_title))

            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }
            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Greeting", "Mr.")
            }
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Firstname", "Jones")
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Lastname", "Douses")
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Email", "Jones.Douses@example.com")
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Phone", "+41234567890")
            }

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("try pressing on cancel") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            step("") {

              // Scroll to Title
              composeTestRule
                  .onNodeWithTag(C.Tag.edit_profile_screen_container)
                  .performScrollToNode(hasTestTag(C.Tag.TopAppBar.screen_title))

              titleTxt {
                assertIsDisplayed()
                assertTextEquals("Edit Profile")
              }
              greetingTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Greeting", "Mr.")
              }
              firstnameTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Firstname", "Jones")
              }
              lastnameTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Lastname", "Douses")
              }
              emailTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Email", "Jones.Douses@example.com")
              }
              phoneNumberTbx {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Phone", "+41234567890")
              }

              // Scroll to Confirm Button
              composeTestRule
                  .onNodeWithTag(C.Tag.edit_profile_screen_container)
                  .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

              confirmBtn {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Save")
              }
              dismissBtn {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Cancel")
              }
            }
            step("") {
              dismissBtn {
                assertIsDisplayed()
                assertIsEnabled()
                assertTextEquals("Cancel")
                performClick()
              }
            }
          }
        }
      }

  @Test
  fun testReducedAddressInput() =
      run(testName = "test reduced address input") {
        composeTestRule.setContent {
          CompositionLocalProvider(
              LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
                EditProfileDialog(
                    mockContext,
                    { Log.d("EditProfileTest_Dismiss", "User info discarded") },
                    { Log.d("EditProfileTest_Save", "User info saved ${it.printFullname()}") })
              }
        }
        step("Input all necessary address and other fields") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }
            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Mr.")
            }
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones")
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Douses")
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones.Douses@example.com")
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("+41234567890")
            }

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            streetBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Street", true)
              performTextClearance()
              assertTextContains("Avenue de la Gare 20", true)
            }

            closeSoftKeyboard()

            cityBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("City", true)
              performTextClearance()
              performTextInput("Lausanne")
              assertTextContains("Lausanne", true)
            }

            closeSoftKeyboard()

            postalBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("PLZ", true)
              performTextClearance()
              performTextInput("1003")
              assertTextContains("1003", true)
            }

            closeSoftKeyboard()

            cantonBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Region", true)
              performTextClearance()
              assertTextContains("Vaud", true)
            }

            closeSoftKeyboard()

            countryBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Country", true)
              performTextClearance()
              performTextInput("Switzerland")
              assertTextContains("Switzerland", true)
            }

            closeSoftKeyboard()

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("Click button and check") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
              performClick()
            }

            assert(latitude == SHORT_LIST_COORDINATES)
            assert(longitude == SHORT_LIST_COORDINATES)
          }
        }
      }

  @Test
  fun testAllAddressInput() =
      run(testName = "test all address input") {
        composeTestRule.setContent {
          CompositionLocalProvider(
              LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
                EditProfileDialog(
                    mockContext,
                    { Log.d("EditProfileTest_Dismiss", "User info discarded") },
                    { Log.d("EditProfileTest_Save", "User info saved ${it.printFullname()}") })
              }
        }
        step("Input all address and other fields") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }
            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Mr.")
            }
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones")
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Douses")
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones.Douses@example.com")
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("+41234567890")
            }

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            streetBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Street", true)
              performTextClearance()
              performTextInput("Avenue de la Gare 19")
              assertTextContains("Avenue de la Gare 19", true)
            }

            closeSoftKeyboard()

            cityBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("City", true)
              performTextClearance()
              performTextInput("Ecublens")
              assertTextContains("Ecublens", true)
            }

            closeSoftKeyboard()

            postalBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("PLZ", true)
              performTextClearance()
              performTextInput("1002")
              assertTextContains("1002", true)
            }

            closeSoftKeyboard()

            cantonBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Region", true)
              performTextClearance()
              performTextInput("Fribourg")
              assertTextContains("Fribourg", true)
            }

            closeSoftKeyboard()

            countryBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Country", true)
              performTextClearance()
              performTextInput("Liechtenstein")
              assertTextContains("Liechtenstein", true)
            }

            closeSoftKeyboard()

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("Click button and check") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
              performClick()
            }

            assert(latitude == LONG_LIST_COORDINATES)
            assert(longitude == LONG_LIST_COORDINATES)
          }
        }
      }

  @Test
  fun testInvalidAddressInput() =
      run(testName = "test invalid address input") {
        composeTestRule.setContent {
          CompositionLocalProvider(
              LocalAppConfig provides AppConfig(userViewModel = mockUserViewModel)) {
                EditProfileDialog(
                    mockContext,
                    { Log.d("EditProfileTest_Dismiss", "User info discarded") },
                    { Log.d("EditProfileTest_Save", "User info saved ${it.printFullname()}") })
              }
        }
        step("Input all necessary address and other fields") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }
            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Mr.")
            }
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones")
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Douses")
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("Jones.Douses@example.com")
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              performTextInput("+41234567890")
            }

            // Scroll to Confirm Button
            composeTestRule
                .onNodeWithTag(C.Tag.edit_profile_screen_container)
                .performScrollToNode(hasTestTag(C.Tag.EditProfile.confirm))

            streetBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Street", true)
              performTextClearance()
              assertTextContains("Avenue de la Gare 20", true)
            }

            closeSoftKeyboard()

            cityBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("City", true)
              performTextClearance()
              assertTextContains("Lausanne", true)
            }

            closeSoftKeyboard()

            postalBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("PLZ", true)
              performTextClearance()
              performTextInput("1003")
              assertTextContains("1003", true)
            }

            closeSoftKeyboard()

            cantonBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Region", true)
              performTextClearance()
              assertTextContains("Vaud", true)
            }

            closeSoftKeyboard()

            countryBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Country", true)
              performTextClearance()
              assertTextContains("Switzerland", true)
            }

            closeSoftKeyboard()

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("Click button and check") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
              performClick()
            }

            assert(latitude == START_COORDINATES)
            assert(longitude == START_COORDINATES)
          }
        }

        step("Add only country") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            countryBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Country", true)
              performTextClearance()
              performTextInput("Switzerland")
              assertTextContains("Switzerland", true)
            }

            closeSoftKeyboard()

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("Click button and check second time") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
              performClick()
            }

            assert(latitude == START_COORDINATES)
            assert(longitude == START_COORDINATES)
          }
        }
        step("Remove country and add city") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            cityBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("City", true)
              performTextClearance()
              performTextInput("Lausanne")
              assertTextContains("Lausanne", true)
            }

            closeSoftKeyboard()

            countryBox {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Switzerland", true)
              performTextClearance()
              assertTextContains("Switzerland", true)
            }

            closeSoftKeyboard()

            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
            }
            dismissBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Cancel")
            }
          }
        }
        step("Click button and check last time") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            confirmBtn {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Save")
              performClick()
            }

            assert(latitude == START_COORDINATES)
            assert(longitude == START_COORDINATES)
          }
        }
      }
}
