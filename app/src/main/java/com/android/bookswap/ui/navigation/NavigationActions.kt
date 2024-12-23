package com.android.bookswap.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.android.bookswap.resources.C

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val CHAT =
      TopLevelDestination(
          route = C.Route.CHAT_LIST, icon = Icons.Filled.MailOutline, textId = "Chat")
  val MAP = TopLevelDestination(route = C.Route.MAP, icon = Icons.Outlined.Place, textId = "Map")
  val NEW_BOOK =
      TopLevelDestination(
          route = C.Route.NEW_BOOK, icon = Icons.Outlined.AddCircle, textId = "New Book")
  val PROFILE =
      TopLevelDestination(
          route = C.Route.USER_PROFILE, icon = Icons.Outlined.AccountCircle, textId = "Profile")
}
/** List of top level destinations that are shown in the bottom navigation bar */
val List_Navigation_Bar_Destinations =
    listOf(TopLevelDestinations.CHAT, TopLevelDestinations.NEW_BOOK, TopLevelDestinations.MAP)

open class NavigationActions(
    private val navController: NavHostController,
) {
  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {

    // Only navigate if the route is different from the current route
    if (!isCurrentDestination(destination.route)) {
      navController.navigate(destination.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
          inclusive = true
        }

        // Avoid multiple copies of the same destination when reelecting same item
        launchSingleTop = true

        // Restore state when reelecting a previously selected item
        if (destination.route != C.Route.AUTH) {
          restoreState = true
        }
      }
    }
  }

  /**
   * Navigate to the specified screen with optional parameters.
   *
   * @param screen The screen to navigate to
   * @param user1 The first user to pass to the screen
   * @param user2 The second user to pass to the screen
   */
  open fun navigateTo(screen: String, UUID: String) {
    val screen_address =
        when (screen) {
          C.Screen.CHAT -> "$screen/$UUID"
          C.Screen.EDIT_BOOK -> "$screen/$UUID"
          C.Screen.OTHERS_USER_PROFILE -> "$screen/$UUID"
          else -> screen
        }
    navigateTo(screen_address)
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    // Only navigate if the route is different from the current route
    if (!isCurrentDestination(screen)) {
      navController.navigate(screen)
    }
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    val previousBackStackEntry = navController.previousBackStackEntry

    // Check if the previous route is C.Route.AUTH
    if (previousBackStackEntry?.destination?.route != C.Route.AUTH) {
      navController.popBackStack()
    }
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }
  /**
   * Check if the given route is the current destination.
   *
   * @param route The route to check.
   * @return True if the current route starts with the given route, false otherwise.
   */
  private fun isCurrentDestination(route: String): Boolean {
    // Retrieve the current route and check if it starts with the same route name (as checking
    // equality of the route name didn't worked)
    val currentRoute = currentRoute()
    return currentRoute.startsWith(route)
  }
}
