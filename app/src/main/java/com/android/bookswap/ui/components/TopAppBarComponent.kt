package com.android.bookswap.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.profile.ProfileIcon
import com.android.bookswap.ui.theme.ColorVariable

/**
 * A composable function that displays a top app bar with a title, back button, and profile icon.
 *
 * @param modifier A [Modifier] for this composable.
 * @param navigationActions An instance of [NavigationActions] to handle navigation events.
 * @param title The title to be displayed in the top app bar. Defaults to the current route.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComponent(
    modifier: Modifier = Modifier,
    navigationActions: NavigationActions,
    title: String = navigationActions.currentRoute()
) {
  TopAppBar(
      title = { Text(text = title, Modifier.testTag("TopAppBar_Title")) },
      modifier = modifier.testTag("TopAppBar"),
      { BackButtonComponent(navActions = navigationActions) },
      { ProfileIcon(navigationActions = navigationActions) },
      TopAppBarDefaults.windowInsets,
      TopAppBarDefaults.topAppBarColors(ColorVariable.BackGround))
}
