package com.android.bookswap.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.profile.ProfileIcon

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
  )
}
