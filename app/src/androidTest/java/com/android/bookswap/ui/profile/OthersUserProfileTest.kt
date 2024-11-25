package com.android.bookswap.ui.profile

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.bookswap.data.DataUser
import com.android.bookswap.model.UserViewModel
import com.android.bookswap.ui.components.TopAppBarComponent
import com.android.bookswap.ui.navigation.NavigationActions
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/**
 *
 * This is a test class for the OtherUserProfileScreen.
 */
@RunWith(AndroidJUnit4::class)
class OthersUserProfileTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    val testUserId = UUID.randomUUID()
    private val standardUser =
        DataUser(
            testUserId,
            "M.",
            "Thommy",
            "Schelby",
            "Thomy.Schelby@test.com",
            "+41234567890",
            0.0,
            0.0,
            "toto.png")

    @Before
    fun setup() {
        val userVM: UserViewModel = mockk()
        every { userVM.getUser(any()) } returns standardUser
        every { userVM.uuid } returns standardUser.userUUID

        composeTestRule.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            OtherUserProfileScreen(userId = testUserId ,userVM = userVM, { TopAppBarComponent(Modifier, navigationActions, "Messages") })
        }
    }

    @Test
    fun testDisplay() {
        assert(true)
    }
}