package com.android.bookswap.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.bookswap.ui.theme.ColorVariable

// Constants
private val PADDING_DP = 16.dp
private val BUTTON_SIZE_DP = 40.dp
private val ICON_SIZE_DP = 32.dp
private val BUTTON_CORNER_RADIUS_DP = 50.dp

@Composable
fun FilterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(PADDING_DP)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .testTag("filterButton")
                .background(color = ColorVariable.BackGround, shape = RoundedCornerShape(size = BUTTON_CORNER_RADIUS_DP)) // Use constant
                .size(BUTTON_SIZE_DP)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Filter",
                tint = ColorVariable.Accent,
                modifier = Modifier.size(ICON_SIZE_DP)
            )
        }
    }
}
