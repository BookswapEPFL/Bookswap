package com.android.bookswap.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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

/** Maximum number of lines for the author text. */
const val MAX_LINE = 1

/** Weight for the title text in the column layout. */
const val TITLE_WEIGHT = 1f

/** Weight for the author and text column layout. */
const val COLUMN_WEIGHT = 1f

/** Default rating value. */
const val DEFAULT_RATING_VALUE = 0
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
      modifier =
          modifier
              .fillMaxWidth()
              .heightIn(
                  min = IMAGE_HEIGHT_DP + PADDING_VERTICAL_DP * 2,
                  max = IMAGE_HEIGHT_DP + PADDING_VERTICAL_DP * 2)
              .padding(PADDING_HORIZONTAL_DP, PADDING_VERTICAL_DP),
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically) {
        Log.i("BookDisplayComponent", "Displaying book: ${book.title} by ${book.author}")

        // Image Box
        Box(
            modifier =
                Modifier.height(IMAGE_HEIGHT_DP)
                    .width(IMAGE_WIDTH_DP)
                    .testTag(C.Tag.BookDisplayComp.image),
            contentAlignment = Alignment.Center,
        ) {
          // Show the book picture of the contact or the default icon
          val photoUrl = book.photo ?: ""

          // If the photo URL is not empty, display the image:
          if (photoUrl.isNotEmpty()) {
            Log.i("BookDisplayComponent", "Photo URL: ${photoUrl}")
            AsyncImage(
                model = photoUrl,
                contentDescription = "Book Picture",
                modifier = Modifier.fillMaxSize().testTag(C.Tag.BookDisplayComp.image_picture),
                contentScale = ContentScale.Crop)
          } else { // If the photo URL is empty, display the Gray Box
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(Color.Gray)
                        .testTag(C.Tag.BookDisplayComp.image_gray_box))
          }
        }

        // Space between the image and text
        Spacer(modifier = Modifier.width(PADDING_HORIZONTAL_DP * 2))

        // Column for Title and Author text components
        Column(
            modifier =
                Modifier.weight(COLUMN_WEIGHT, true)
                    .padding(horizontal = PADDING_HORIZONTAL_DP)
                    .heightIn(max = IMAGE_HEIGHT_DP + PADDING_VERTICAL_DP * 2)
                    .testTag(C.Tag.BookDisplayComp.middle_container)) {
              Text(
                  text = book.title,
                  overflow = TextOverflow.Ellipsis,
                  style = MaterialTheme.typography.titleLarge,
                  color = ColorVariable.Accent,
                  modifier =
                      Modifier.weight(TITLE_WEIGHT, true)
                          // .padding(bottom = PADDING_VERTICAL_DP)
                          .testTag(C.Tag.BookDisplayComp.title))
              Text(
                  text = book.author ?: "",
                  overflow = TextOverflow.Ellipsis,
                  color = ColorVariable.AccentSecondary,
                  maxLines = MAX_LINE,
                  modifier = Modifier.testTag(C.Tag.BookDisplayComp.author))
            }

        // Column for rating and genres
        Column(
            modifier =
                Modifier.requiredWidth(STAR_SIZE_DP * MAX_RATING + PADDING_HORIZONTAL_DP * 2)
                    .width(STAR_SIZE_DP * MAX_RATING + PADDING_HORIZONTAL_DP * 2)
                    .testTag(C.Tag.BookDisplayComp.right_container),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  modifier = Modifier.fillMaxWidth().testTag(C.Tag.BookDisplayComp.rating),
                  horizontalArrangement = Arrangement.Center) {
                    // leave all stars empty if no rating
                    DisplayStarReview(book.rating ?: DEFAULT_RATING_VALUE)
                  }
              // text for the tags of the book, will be added at a later date
              // It isn't decided how we will handle the tag for the books
              Text(
                  text = book.genres.joinToString(separator = ", ") { it.Genre },
                  modifier =
                      Modifier.fillMaxWidth().clipToBounds().testTag(C.Tag.BookDisplayComp.genres),
                  overflow = TextOverflow.Ellipsis,
                  color = ColorVariable.AccentSecondary)
            }
      }
}

/** Constants */

/** Star Alpha tint */
const val STAR_TINT_ALPHA = 1f

/** Star Alpha tint */
const val STAR_TINT_RED_FACTOR = 0.75f

/** Star Green Factor */
const val STAR_TINT_GREEN_FACTOR = 0.75f

/** Star Blue Factor */
const val STAR_TINT_BLUE_FACTOR = 0.75f

/**
 * Displays a star rating for a book.
 *
 * This function displays filled stars for the given rating and hollow stars for the remaining up to
 * the maximum rating. The stars are displayed in a row layout.
 *
 * @param rating The rating value to be displayed, ranging from 0 to MAX_RATING.
 */
@Composable
private fun DisplayStarReview(rating: Int) {
  for (i in 1..rating) {
    Box(modifier = Modifier.width(STAR_SIZE_DP).testTag(C.Tag.BookDisplayComp.filled_star)) {
      Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = "Star Icon",
          tint =
              MaterialTheme.colorScheme.outline.let {
                it.copy(
                    STAR_TINT_ALPHA,
                    it.red * STAR_TINT_RED_FACTOR,
                    it.green * STAR_TINT_GREEN_FACTOR,
                    it.blue * STAR_TINT_BLUE_FACTOR)
              },
          modifier = Modifier.size(STAR_SIZE_DP))
    }
  }
  for (i in rating + 1..MAX_RATING) {
    // Hollow star
    Box(modifier = Modifier.width(STAR_SIZE_DP).testTag(C.Tag.BookDisplayComp.hollow_star)) {
      Icon(
          imageVector = Icons.TwoTone.Star,
          contentDescription = "Star Icon",
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(STAR_SIZE_DP))
    }
  }
}
