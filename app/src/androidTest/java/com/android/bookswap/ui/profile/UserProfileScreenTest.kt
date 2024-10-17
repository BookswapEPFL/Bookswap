package com.android.bookswap.ui.profile

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
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
    val userVM = UserViewModel("email@example.com", MockUserRepo())

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

class MockUserRepo : UsersRepository {
  val mockUserList =
      mutableMapOf(
          "usr_01_jd" to
              DataUser(
                  "M.",
                  "John",
                  "Doe",
                  "john.doe@example.com",
                  "+41223456789",
                  0.0,
                  0.0,
                  "",
                  "usr_01_jd"),
          "usr_02_jd" to
              DataUser(
                  "Mr.",
                  "Jones",
                  "Douse",
                  "jon.doe@example.com",
                  "+41234567890",
                  0.0,
                  0.0,
                  "",
                  "usr_02_jd"),
          "usr_03_jd" to
              DataUser(
                  "Ms.",
                  "Jo",
                  "Doe",
                  "jo.doe@example.com",
                  "+41765432198",
                  0.0,
                  0.0,
                  "",
                  "usr_03_jd"),
      )

  override fun init(callback: (Result<Unit>) -> Unit) {
    TODO("Not yet implemented")
  }

  override fun getNewUid(): String {
    return java.util.UUID.randomUUID().toString()
  }

  override fun getUsers(callback: (Result<List<DataUser>>) -> Unit) {
    callback(Result.success(mockUserList.values.toList()))
  }

  override fun getUser(uuid: String, callback: (Result<DataUser>) -> Unit) {
    val usr = mockUserList.get(uuid)
    if (usr != null) {
      callback(Result.success(usr))
    } else {
      callback(Result.failure(Throwable("User not found")))
    }
  }

  override fun addUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    mockUserList.put(dataUser.userId, dataUser)
  }

  override fun updateUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    mockUserList.put(dataUser.userId, dataUser)
  }

  override fun deleteUser(uuid: String, callback: (Result<Unit>) -> Unit) {
    mockUserList.remove(uuid)
  }
}
