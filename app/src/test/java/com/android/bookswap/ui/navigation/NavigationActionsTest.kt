package com.android.bookswap.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Place
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class NavigationActionsTest {
  private val navigationDestination: NavDestination = mockk()
  private val navHostController: NavHostController = mockk()
  private val navigationActions: NavigationActions = NavigationActions(navHostController)

  @Test
  fun navigateToTopLevelDestination() {
    every { navHostController.navigate(any(), any<(NavOptionsBuilder) -> Unit>()) } just runs
    navigationActions.navigateTo(TopLevelDestinations.CHAT)
    verify { navHostController.navigate(Route.CHAT, any<(NavOptionsBuilder) -> Unit>()) }

    navigationActions.navigateTo(TopLevelDestinations.MAP)
    verify { navHostController.navigate(Route.MAP, any<(NavOptionsBuilder) -> Unit>()) }

    navigationActions.navigateTo(TopLevelDestinations.PROFILE)
    verify { navHostController.navigate(Route.PROFILE, any<(NavOptionsBuilder) -> Unit>()) }

    navigationActions.navigateTo(TopLevelDestinations.NEW_BOOK)
    verify { navHostController.navigate(Route.NEWBOOK, any<(NavOptionsBuilder) -> Unit>()) }
  }

  @Test
  fun navigateToScreen() {
    every { navHostController.navigate(any<String>(), any(), any()) } just runs

    navigationActions.navigateTo(Screen.MAP)
    verify { navHostController.navigate(Screen.MAP) }

    navigationActions.navigateTo(Screen.CHAT)
    verify { navHostController.navigate(Screen.CHAT) }

    navigationActions.navigateTo(Screen.NEWBOOK)
    verify { navHostController.navigate(Screen.NEWBOOK) }

    navigationActions.navigateTo(Screen.ADD_BOOK_ISBN)
    verify { navHostController.navigate(Screen.ADD_BOOK_ISBN) }

    navigationActions.navigateTo(Screen.ADD_BOOK_SCAN)
    verify { navHostController.navigate(Screen.ADD_BOOK_SCAN) }
  }

  @Test
  fun currentRouteAreCorrect() {
    every { navHostController.currentDestination } returns navigationDestination
    every { navigationDestination.route } returns Route.NEWBOOK

    assertThat(navigationActions.currentRoute(), `is`(Route.NEWBOOK))
  }

  @Test
  fun goBackCallPopBackStack() {
    every { navHostController.popBackStack() } returns true
    navigationActions.goBack()

    verify { navHostController.popBackStack() }
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
}
