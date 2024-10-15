package com.android.bookswap.ui.profile

import android.location.Address
import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.User
import com.android.bookswap.screen.EditProfileScreen
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
class EditProfileScreenTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  fun createAddress(
      locale: Locale = Locale.getDefault(),
      featureName: String = "",
      addressLines: HashMap<Int, String> = HashMap<Int, String>(),
      adminArea: String = "",
      subAdminArea: String = "",
      locality: String = "",
      subLocality: String = "",
      thoroughfare: String = "",
      subThoroughfare: String = "",
      premises: String = "",
      postalCode: String = "",
      countryCode: String = "",
      countryName: String = "",
      latitude: Double = 0.0,
      longitude: Double = 0.0,
      hasLat: Boolean = false,
      hasLon: Boolean = false,
      phone: String = "",
      url: String = ""
  ): Address {
    var addr = Address(locale)
    addr.featureName = featureName
    addr.adminArea = adminArea
    addr.subAdminArea = subAdminArea
    addr.locality = locality
    addr.subLocality = subLocality
    addr.thoroughfare = thoroughfare
    addr.subThoroughfare = subThoroughfare
    addr.premises = premises
    addr.postalCode = postalCode
    addr.countryCode = countryCode
    addr.countryName = countryName
    if (hasLat) addr.latitude = latitude
    if (hasLon) addr.longitude = longitude
    addr.phone = phone
    addr.url = url
    if (addressLines.isNotEmpty())
        for (line in addressLines) addr.setAddressLine(line.key, line.value)
    return addr
  }

  @Before
  fun setup() {
    val myAddress =
        createAddress(
            Locale.UK,
            "dummyAddress",
            HashMap<Int, String>((mapOf((0 to "Example street 1A"), (1 to "Apartment 1")))),
            "exampleAdminArea",
            "dummySubAdmin",
            "Foo-Town",
            "Bar-urbs",
            "egsample-fare",
            "sample-subthrough",
            "ramdomPremise",
            "1337-42",
            "EX",
            "ExampleCountry",
            0.0,
            0.0,
            false,
            false,
            "+000123456789",
            "www.dummyurl.example")
    composeTestRule.setContent {
      EditProfileDialog(
          onDismiss = { /*TODO*/},
          onSave = { Log.d("editUsrAlrt_Save", "User info saved ${it.printFull1Line()}") },
          user =
              User(
                  "M.",
                  "John",
                  "Doe",
                  "John.Doe@example.com",
                  "+41223456789",
                  myAddress,
                  "dummyPic.png",
                  "dummyUUID0000"))
    }
  }

  @Test
  fun testDisplay() =
      run(testName = "test alert display") {
        step("try displaying the alert box") {
          ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
            titleTxt {
              assertIsDisplayed()
              assertTextEquals("Edit Profile")
            }
            greetingTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Greeting", "M.")
            }
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Firstname", "John")
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Lastname", "Doe")
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Email", "John.Doe@example.com")
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              assertTextEquals("Phone", "+41223456789")
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
              assertTextEquals("Greeting", "")
              performTextInput("Mr.")
            }
            firstnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextEquals("Firstname", "")
              performTextInput("Jones")
            }
            lastnameTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextEquals("Lastname", "")
              performTextInput("Douses")
            }
            emailTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextEquals("Email", "")
              performTextInput("Jones.Douses@example.com")
            }
            phoneNumberTbx {
              assertIsDisplayed()
              assertIsEnabled()
              performClick()
              performTextClearance()
              assertTextEquals("Phone", "")
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
      }

  @Test
  fun testEditConfirm() =
      run(testName = "test edit and confirm") {
        step("try editing the textbox values then confirming the changes") {
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
