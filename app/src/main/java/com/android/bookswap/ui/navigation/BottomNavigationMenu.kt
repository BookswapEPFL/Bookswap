package com.android.bookswap.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.theme.ColorVariable

/** Constants * */
private val BOTTOM_NAV_HEIGHT = 60.dp
private val ROUNDED_CORNER_SIZE = 60.dp

/**
 * This Composable function creates a bottom navigation menu for the app Displaying different tabs
 * (destinations) based on the provided list of destinations (tabList).
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  BottomNavigation(
      modifier = Modifier.fillMaxWidth().height(BOTTOM_NAV_HEIGHT).testTag("bottomNavigationMenu"),
      backgroundColor = ColorVariable.Primary, // Color of the bottom navigation bar
      content = {
        tabList.forEach { tab ->
          BottomNavigationItem(
              icon = {
                // Display the icon for each tab
                Icon(tab.icon, contentDescription = null, tint = ColorVariable.BackGround)
              },
              selected = tab.route == selectedItem,
              onClick = { onTabSelect(tab) },
              modifier = Modifier.clip(RoundedCornerShape(ROUNDED_CORNER_SIZE)).testTag(tab.textId))
        }
      },
  )
}
