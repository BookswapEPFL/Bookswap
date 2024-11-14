package com.android.bookswap.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

/**
 * A composable function that displays a back button.
 *
 * @param navActions The navigation actions to be performed when the button is clicked.
 */
@Composable
fun BackButtonComponent(navActions: NavigationActions) {
  IconButton(onClick = { navActions.goBack() }, modifier = Modifier.testTag("backButton")) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        tint = ColorVariable.Accent,
        contentDescription = "Back",
        modifier = Modifier.testTag("backIcon").size(32.dp))
  }
}
