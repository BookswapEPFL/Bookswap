package com.android.bookswap.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.resources.C
import com.android.bookswap.ui.theme.ColorVariable

/** Constants * */
val BOTTOM_NAV_HEIGHT = 60.dp
private val ROUNDED_CORNER_SIZE = 60.dp

/**
 * This Composable function creates a bottom navigation menu for the app, displaying different tabs
 * (destinations) based on the provided list of destinations (tabList).
 *
 * @param onTabSelect A lambda function to handle tab selection events.
 * @param tabList A list of [TopLevelDestination] representing the tabs to be displayed.
 * @param selectedItem A string representing the currently selected tab.
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  NavigationBar(
      modifier =
          Modifier.fillMaxWidth()
              .height(BOTTOM_NAV_HEIGHT)
              .testTag(C.Tag.bottom_navigation_menu_container),
      containerColor = ColorVariable.Primary) {
        tabList.forEach { tab ->
          NavigationBarItem(
              modifier = Modifier.testTag(tab.route + C.Tag.BottomNavMenu.nav_item),
              selected = tab.route == selectedItem,
              onClick = { onTabSelect(tab) },
              icon = {
                Icon(
                    tab.icon,
                    contentDescription = tab.route + C.Tag.BottomNavMenu.nav_icon,
                    tint = ColorVariable.BackGround)
              },
              colors =
                  NavigationBarItemDefaults.colors(
                      indicatorColor = ColorVariable.Secondary.copy(0.4f)))
        }
      }
}
