package com.android.bookswap.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Place
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.android.bookswap.resources.C
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class NavigationActionsTest {
  private val navigationDestination: NavDestination = mockk()
  private val navHostController: NavHostController = mockk()
  private val navigationActions: NavigationActions = NavigationActions(navHostController)

  @Before
  fun setup() {
    every { navigationDestination.route } returns "test"
    every { navHostController.currentDestination } returns navigationDestination
  }

  @Test
  fun navigateToTopLevelDestination() {
    every { navHostController.navigate(any(), any<(NavOptionsBuilder) -> Unit>()) } just runs
    navigationActions.navigateTo(TopLevelDestinations.CHAT)
    verify { navHostController.navigate(C.Route.CHAT_LIST, any<(NavOptionsBuilder) -> Unit>()) }

    navigationActions.navigateTo(TopLevelDestinations.MAP)
    verify { navHostController.navigate(C.Route.MAP, any<(NavOptionsBuilder) -> Unit>()) }

    navigationActions.navigateTo(TopLevelDestinations.PROFILE)
    verify { navHostController.navigate(C.Route.USER_PROFILE, any<(NavOptionsBuilder) -> Unit>()) }

    navigationActions.navigateTo(TopLevelDestinations.NEW_BOOK)
    verify { navHostController.navigate(C.Route.NEW_BOOK, any<(NavOptionsBuilder) -> Unit>()) }
  }

  @Test
  fun navigateToScreen() {
    every { navHostController.navigate(any<String>(), any(), any()) } just runs

    navigationActions.navigateTo(C.Screen.MAP)
    verify { navHostController.navigate(C.Screen.MAP) }

    navigationActions.navigateTo(C.Screen.CHAT)
    verify { navHostController.navigate(C.Screen.CHAT) }

    navigationActions.navigateTo(C.Screen.NEW_BOOK)
    verify { navHostController.navigate(C.Screen.NEW_BOOK) }

    navigationActions.navigateTo(C.Screen.ADD_BOOK_ISBN)
    verify { navHostController.navigate(C.Screen.ADD_BOOK_ISBN) }

    navigationActions.navigateTo(C.Screen.ADD_BOOK_SCAN)
    verify { navHostController.navigate(C.Screen.ADD_BOOK_SCAN) }
  }

  @Test
  fun currentRouteAreCorrect() {
    every { navHostController.currentDestination } returns navigationDestination
    every { navigationDestination.route } returns C.Route.NEW_BOOK

    assertThat(navigationActions.currentRoute(), `is`(C.Route.NEW_BOOK))
  }

  @Test
  fun topLevelDestinationsHaveCorrectIcon() {
    assertThat(TopLevelDestinations.CHAT.icon, `is`(Icons.Filled.MailOutline))
    assertThat(TopLevelDestinations.MAP.icon, `is`(Icons.Outlined.Place))
    assertThat(TopLevelDestinations.NEW_BOOK.icon, `is`(Icons.Outlined.AddCircle))
    assertThat(TopLevelDestinations.PROFILE.icon, `is`(Icons.Outlined.AccountCircle))
  }

  @Test
  fun list_Navigation_Bar_DestinationsCorrectOrder() {
    assertThat(
        List_Navigation_Bar_Destinations,
        `is`(
            listOf(
                TopLevelDestinations.CHAT,
                TopLevelDestinations.NEW_BOOK,
                TopLevelDestinations.MAP)))
  }

  @Test
  fun `go back when previous route is AUTH does nothing`() {
    val mockPreviousBackStackEntry: NavBackStackEntry? = mockk()
    every { mockPreviousBackStackEntry?.destination?.route } returns C.Route.AUTH

    every { navHostController.previousBackStackEntry } returns mockPreviousBackStackEntry

    navigationActions.goBack()

    verify(exactly = 0) { navHostController.popBackStack() }
  }
}
