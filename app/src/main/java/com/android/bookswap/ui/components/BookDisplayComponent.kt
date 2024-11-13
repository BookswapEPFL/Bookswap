package com.android.bookswap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.bookswap.data.DataBook
import com.android.bookswap.ui.theme.ColorVariable

val PADDING_HORIZONTAL_DP = 8.dp
val PADDING_VERTICAL_DP = 4.dp
val IMAGE_HEIGHT_DP = 82.dp
val IMAGE_WIDTH_DP = 72.dp
val MAX_RATING = 5
val PRIMARY_TEXT_FONT_SP = 20.sp
val SECONDARY_TEXT_FONT_SP = 16.sp
val STAR_HEIGHT_DP = 30.dp
val STAR_SIZE_DP = 26.dp
val STAR_INNER_SIZE_DP = STAR_SIZE_DP / 2
val WIDTH_TITLE_BOX_DP = 150.dp

@Composable
fun BookDisplayComponent(modifier: Modifier = Modifier, book: DataBook) {
  Row(modifier = modifier) {
    // Image Box
    Box(
        modifier =
            Modifier.height(IMAGE_HEIGHT_DP)
                .width(IMAGE_WIDTH_DP)
                .padding(start = PADDING_HORIZONTAL_DP, end = PADDING_HORIZONTAL_DP)
                .testTag("mapDraggableMenuBookBoxImage")) {
          // Image of the books, will be added at a later date
          // We didn't discussed about how we will store the image or how we
          // will
          // encode them
          Box(
              modifier = Modifier.fillMaxSize().background(Color.Gray) // Placeholder for the image
              )
        }

    // Column for text content
    Column(
        modifier =
            Modifier.padding(vertical = PADDING_VERTICAL_DP)
                .width(WIDTH_TITLE_BOX_DP)
                .testTag("mapDraggableMenuBookBoxMiddle")) {
          Text(
              text = book.title,
              color = ColorVariable.Accent,
              fontSize = PRIMARY_TEXT_FONT_SP,
              modifier =
                  Modifier.padding(bottom = PADDING_VERTICAL_DP)
                      .width(WIDTH_TITLE_BOX_DP)
                      .testTag("mapDraggableMenuBookBoxTitle"))
          Text(
              text = book.author ?: "",
              color = ColorVariable.AccentSecondary,
              fontSize = SECONDARY_TEXT_FONT_SP,
              modifier =
                  Modifier.width(WIDTH_TITLE_BOX_DP).testTag("mapDraggableMenuBookBoxAuthor"))
        }
    Column(modifier = Modifier.fillMaxWidth().testTag("mapDraggableMenuBookRight")) {
      Row(
          modifier =
              Modifier.fillMaxWidth()
                  .height(STAR_HEIGHT_DP)
                  .testTag("mapDraggableMenuBookBoxRating")) {
            // leave all stars empty if no rating
            DisplayStarReview(book.rating ?: 0)
          }
      // text for the tags of the book, will be added at a later date
      // It isn't decided how we will handle the tag for the books
      Text(
          text = book.genres.joinToString(separator = ", ") { it.Genre },
          modifier = Modifier.fillMaxWidth().testTag("mapDraggableMenuBookBoxTag"),
          fontSize = SECONDARY_TEXT_FONT_SP,
          color = ColorVariable.AccentSecondary)
    }
  }
}

@Composable
private fun DisplayStarReview(rating: Int) {
  for (i in 1..rating) {
    Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = "Star Icon",
        tint = Color.Black,
        modifier = Modifier.size(STAR_SIZE_DP).testTag("mapDraggableMenuBookBoxStar"))
  }
  for (i in rating + 1..MAX_RATING) {
    // Hollow star
    // Icons.Outlined.Star doesn't work, it displays the
    // Icons.Filled.Star
    Box(modifier = Modifier.width(STAR_SIZE_DP).testTag("mapDraggableMenuBookBoxEmptyStar")) {
      Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = "Star Icon",
          tint = Color.Black,
          modifier = Modifier.size(STAR_SIZE_DP))
      Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = "Star Icon",
          tint = ColorVariable.BackGround,
          modifier = Modifier.size(STAR_INNER_SIZE_DP).align(Alignment.Center))
    }
  }
}
