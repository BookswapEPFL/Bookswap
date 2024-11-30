package com.android.bookswap.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.DataBook
import com.android.bookswap.resources.C
import com.android.bookswap.ui.theme.ColorVariable

/** Padding values for horizontal and vertical padding in dp. */
val PADDING_HORIZONTAL_DP = 4.dp
val PADDING_VERTICAL_DP = 4.dp

/** Dimensions for the image in dp. */
val IMAGE_HEIGHT_DP = 82.dp
val IMAGE_WIDTH_DP = 72.dp

/** Maximum rating value. */
val MAX_RATING = 5

/** Size of the star icon in dp. */
val STAR_SIZE_DP = 26.dp
/**
 * Composable function to display a book's information.
 *
 * This function displays a book's image, title, author, rating, and genres in a row layout. The
 * image is currently a placeholder, and the actual image implementation will be added later.
 *
 * @param modifier Modifier to be applied to the component.
 * @param book The book data to be displayed.
 */
@Composable
fun BookDisplayComponent(modifier: Modifier = Modifier, book: DataBook) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Log.e("BookDisplayComponent", "Rendering book: ${book.title} by ${book.author}")
        Text(
            text = "Title: ${book.title}",
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Author: ${book.author ?: "Unknown"}",
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
    }
}
