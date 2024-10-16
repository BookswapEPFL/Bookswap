package com.android.bookswap.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Place
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class NavigationActionsTest {
  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun navigateToTopLevelDestination() {
    navigationActions.navigateTo(TopLevelDestinations.CHAT)
    verify(navHostController).navigate(eq(Route.CHAT), any<(NavOptionsBuilder) -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.MAP)
    verify(navHostController).navigate(eq(Route.MAP), any<(NavOptionsBuilder) -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.PROFILE)
    verify(navHostController).navigate(eq(Route.PROFIL), any<(NavOptionsBuilder) -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.NEW_BOOK)
    verify(navHostController).navigate(eq(Route.NEWBOOK), any<(NavOptionsBuilder) -> Unit>())
  }

  @Test
  fun navigateToScreen() {
    navigationActions.navigateTo(Screen.MAP)
    verify(navHostController).navigate(Screen.MAP)

    navigationActions.navigateTo(Screen.CHAT)
    verify(navHostController).navigate(Screen.CHAT)

    navigationActions.navigateTo(Screen.NEWBOOK)
    verify(navHostController).navigate(Screen.NEWBOOK)

    navigationActions.navigateTo(Screen.ADD_BOOK_ISBN)
    verify(navHostController).navigate(Screen.ADD_BOOK_ISBN)

    navigationActions.navigateTo(Screen.ADD_BOOK_SCAN)
    verify(navHostController).navigate(Screen.ADD_BOOK_SCAN)
  }

  @Test
  fun currentRouteAreCorrect() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.NEWBOOK)

    navigationActions.navigateTo(TopLevelDestinations.NEW_BOOK)
    assertThat(navigationActions.currentRoute(), `is`(Route.NEWBOOK))
  }

  @Test
  fun goBackCallPopBackStack() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
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
