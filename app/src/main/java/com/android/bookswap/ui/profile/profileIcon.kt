package com.android.bookswap.ui.profile

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.theme.Accent

/** Profile Icon for the top app bar */
@Composable
fun ProfileIcon() {
  IconButton(onClick = { /*TODO*/}, modifier = Modifier.testTag("profileIcon")) {
    Icon(
        Icons.Filled.AccountCircle,
        contentDescription = "Profile Icon",
        tint = Accent,
        modifier = Modifier.size(32.dp))
  }
}
