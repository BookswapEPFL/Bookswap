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
import com.android.bookswap.resources.C
import com.android.bookswap.ui.navigation.NavigationActions
import com.android.bookswap.ui.theme.ColorVariable

/**
 * Composable function for a back button component.
 *
 * @param navActions Navigation actions to handle the back button click.
 */
@Composable
fun BackButtonComponent(navActions: NavigationActions) {
  IconButton(
      onClick = { navActions.goBack() }, modifier = Modifier.testTag(C.Tag.TopAppBar.back_button)) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            tint = ColorVariable.Accent,
            contentDescription = "Back",
            modifier = Modifier.testTag(C.Tag.TopAppBar.back_icon).size(32.dp))
      }
}
