package com.android.bookswap.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val CHAT = "Chat"
  const val PROFILE = "Profile"
  const val MAP = "Map"
  const val NEWBOOK = "NewBook"
  const val AUTH = "Auth"
}

object Screen {
  const val AUTH = "Auth Screen"
  const val CHATLIST = "ChatList Screen"
  const val CHAT = "Chat Screen"
  const val MAP = "Map Screen"
  const val NEWBOOK = "NewBook Screen"
  const val ADD_BOOK_MANUALLY = "AddBookManually Screen"
  const val ADD_BOOK_SCAN = "AddBookScan Screen"
  const val ADD_BOOK_ISBN = "AddBookISBN Screen"
  const val SETTINGS = "Settings Screen"
  const val FILTER = "Filter Screen"
  const val EDIT_BOOK = "EditBook Screen"
  const val PROFILE = "Profile Screen"
  const val NEW_USER = "New User Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val CHAT =
      TopLevelDestination(route = Route.CHAT, icon = Icons.Filled.MailOutline, textId = "Chat")
  val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Outlined.Place, textId = "Map")
  val NEW_BOOK =
      TopLevelDestination(
          route = Route.NEWBOOK, icon = Icons.Outlined.AddCircle, textId = "New Book")
  val PROFILE =
      TopLevelDestination(
          route = Route.PROFILE, icon = Icons.Outlined.AccountCircle, textId = "Profile")
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
        if (destination.route != Route.AUTH) {
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
  open fun navigateTo(screen: String, otherUserUUID: String) {
    if (screen == Screen.CHAT.toString()) {
      val route = "$screen/$otherUserUUID"
      // Only navigate if the route is different from the current route
      if (!isCurrentDestination(route)) {
        navController.navigate(route)
      }
    } else {
      val route =
          when (screen) {
            Screen.EDIT_BOOK -> "$screen/$otherUserUUID"
            else -> screen
          }
      navController.navigate(route)
    }
  }

  /*
  open fun navigateTo(screen: String, id: String? = null) {
    val route = when (screen) {
      Screen.EDIT_BOOK -> "$screen/$id"
      else -> screen
    }
    navController.navigate(route)
  }*/

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
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }

  private fun isCurrentDestination(route: String): Boolean {
    // Retrieve the current route and check if it starts with the same route name (as checking
    // equality of the route name didn't worked)
    val currentRoute = currentRoute()
    return currentRoute.startsWith(route)
  }
}
