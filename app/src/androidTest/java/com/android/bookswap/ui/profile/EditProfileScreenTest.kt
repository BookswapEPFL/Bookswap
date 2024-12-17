package com.android.bookswap.ui.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.model.AppConfig
import com.android.bookswap.model.LocalAppConfig
import com.android.bookswap.model.UserViewModel
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
import org.mockito.kotlin.mock

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest : TestCase() {

  private val mockUserViewModel: UserViewModel = mockk(relaxed = true)
  private lateinit var mockContext: Context

  @Before
  fun setup() {
    mockContext = mockk()
    every { mockUserViewModel.uuid } returns UUID.randomUUID()
  }

  @get:Rule val composeTestRule = createComposeRule()

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
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Firstname", true)
              performTextClearance()
              assertTextContains("John", true)
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Lastname", true)
              performTextClearance()
              assertTextContains("Doe", true)
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Email", true)
              performTextClearance()
              assertTextContains("John.Doe@example.com", true)
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextContains("Phone", true)
              performTextClearance()
              assertTextContains("+4122345678", true)
            }
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
}
