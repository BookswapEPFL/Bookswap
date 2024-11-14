package com.android.bookswap.ui.components

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
import androidx.compose.ui.unit.sp
import com.android.bookswap.data.DataBook
import com.android.bookswap.ui.theme.ColorVariable

val scaleFactor = 1
val PADDING_HORIZONTAL_DP = 4.dp * scaleFactor
val PADDING_VERTICAL_DP = 4.dp * scaleFactor
val IMAGE_HEIGHT_DP = 82.dp * scaleFactor
val IMAGE_WIDTH_DP = 72.dp * scaleFactor
val MAX_RATING = 5
val PRIMARY_TEXT_FONT_SP = 22.sp * scaleFactor
val SECONDARY_TEXT_FONT_SP = 16.sp * scaleFactor
val STAR_HEIGHT_DP = 30.dp * scaleFactor
val STAR_SIZE_DP = 26.dp * scaleFactor
val STAR_INNER_SIZE_DP = STAR_SIZE_DP / 2
val WIDTH_TITLE_BOX_DP = 150.dp

@Composable
fun BookDisplayComponent(modifier: Modifier = Modifier, book: DataBook) {
  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .heightIn(
                  min = IMAGE_HEIGHT_DP + PADDING_VERTICAL_DP * 2,
                  max = IMAGE_HEIGHT_DP + PADDING_VERTICAL_DP * 2)
              .padding(PADDING_HORIZONTAL_DP, PADDING_VERTICAL_DP),
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically) {
        // Image Box
        Box(
            modifier =
                Modifier.height(IMAGE_HEIGHT_DP)
                    .width(IMAGE_WIDTH_DP)
                    .testTag("mapDraggableMenuBookBoxImage"),
            contentAlignment = Alignment.Center,
        ) {
          // Image of the books, will be added at a later date
          // We didn't discussed about how we will store the image or how we
          // will
          // encode them
          Box(
              modifier = Modifier.fillMaxSize().background(Color.Gray) // Placeholder for the image
              )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {

          // Column for Title and Author text components
          Column(
              modifier =
                  Modifier.weight(1f, true)
                      .padding(horizontal = PADDING_HORIZONTAL_DP)
                      .heightIn(max = IMAGE_HEIGHT_DP + PADDING_VERTICAL_DP * 2)
                      .testTag("mapDraggableMenuBookBoxMiddle")) {
                Text(
                    text = book.title,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    color = ColorVariable.Accent,
                    modifier =
                        Modifier.weight(1f, true)
                            // .padding(bottom = PADDING_VERTICAL_DP)
                            .testTag("mapDraggableMenuBookBoxTitle"))
                Text(
                    text = book.author ?: "",
                    overflow = TextOverflow.Ellipsis,
                    color = ColorVariable.AccentSecondary,
                    maxLines = 1,
                    modifier = Modifier.testTag("mapDraggableMenuBookBoxAuthor"))
              }

          // Column for rating and genres
          Column(
              modifier =
                  Modifier.requiredWidth(STAR_SIZE_DP * 5 + PADDING_HORIZONTAL_DP * 2)
                      .width(STAR_SIZE_DP * 5 + PADDING_HORIZONTAL_DP * 2)
                      .testTag("mapDraggableMenuBookRight"),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("mapDraggableMenuBookBoxRating"),
                    horizontalArrangement = Arrangement.Center) {
                      // leave all stars empty if no rating
                      DisplayStarReview(book.rating ?: 0)
                    }
                // text for the tags of the book, will be added at a later date
                // It isn't decided how we will handle the tag for the books
                Text(
                    text = book.genres.joinToString(separator = ", ") { it.Genre },
                    modifier =
                        Modifier.fillMaxWidth()
                            .clipToBounds()
                            .testTag("mapDraggableMenuBookBoxTag"),
                    fontSize = SECONDARY_TEXT_FONT_SP,
                    overflow = TextOverflow.Ellipsis,
                    color = ColorVariable.AccentSecondary)
              }
        }
      }
}

@Composable
private fun DisplayStarReview(rating: Int) {
  for (i in 1..rating) {
    Box(modifier = Modifier.width(STAR_SIZE_DP).testTag("mapDraggableMenuBookBoxStar")) {
      Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = "Star Icon",
          tint =
              MaterialTheme.colorScheme.outline.let {
                it.copy(1f, it.red * 0.75f, it.green * 0.75f, it.blue * 0.75f)
              },
          modifier = Modifier.size(STAR_SIZE_DP))
    }
  }
  for (i in rating + 1..MAX_RATING) {
    // Hollow star
    // Icons.Outlined.Star doesn't work, it displays the
    // Icons.Filled.Star
    Box(modifier = Modifier.width(STAR_SIZE_DP).testTag("mapDraggableMenuBookBoxEmptyStar")) {
      Icon(
          imageVector = Icons.TwoTone.Star,
          contentDescription = "Star Icon",
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(STAR_SIZE_DP))
      /*
         Icon(
             imageVector = Icons.Filled.Star,
             contentDescription = "Star Icon",
             tint = ColorVariable.BackGround,
             modifier = Modifier.size(STAR_INNER_SIZE_DP).align(Alignment.Center))
      // */
    }
  }
}
