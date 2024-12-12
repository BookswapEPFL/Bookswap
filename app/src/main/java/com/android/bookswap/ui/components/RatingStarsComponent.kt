package com.android.bookswap.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.bookswap.resources.C
import com.android.bookswap.ui.theme.ColorVariable

val emptyStarColor = Color(0xFFBDBDBD)
@Composable
fun RatingStarsComponent(
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            IconButton(onClick = { onRatingChanged(i) }) {
                Icon(
                    imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (i <= currentRating) "Filled Star" else "Empty Star",
                    tint = if (i <= currentRating) ColorVariable.Accent else emptyStarColor, // Yellow for selected, gray for unselected
                    modifier = Modifier.testTag(if (i <= currentRating) C.Tag.BookEntryComp.rating_star + "_$i" else C.Tag.BookEntryComp.rating_star_empty + "_$i")
                )
            }
        }
    }
}