package com.android.bookswap.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import com.android.bookswap.data.DataBook
import com.android.bookswap.resources.C
import java.util.UUID

val DIVIDER_THICKNESS_DP = Dp.Hairline
/**
 * Displays a list of books in a lazy column.
 *
 * This composable function takes a list of `DataBook` objects and displays them in a scrollable
 * column. If the list is empty, it shows a message indicating that there are no books to display.
 * Each book is displayed using the `BookDisplayComponent` composable, and a horizontal divider is
 * placed between each book item.
 *
 * @param modifier The modifier to be applied to the LazyColumn.
 * @param bookList The list of `DataBook` objects to be displayed. Defaults to an empty list.
 */
@Composable
fun BookListComponent(
    modifier: Modifier = Modifier,
    bookList: List<DataBook> = emptyList(),
    onBookClick: (UUID) -> Unit
) {
  LazyColumn(
      modifier = modifier.fillMaxWidth().testTag(C.Tag.BookListComp.book_list_container),
      state = rememberLazyListState(),
      contentPadding = PaddingValues(PADDING_HORIZONTAL_DP, PADDING_VERTICAL_DP),
      horizontalAlignment = Alignment.CenterHorizontally,
      flingBehavior = ScrollableDefaults.flingBehavior(),
      userScrollEnabled = true,
  ) {
    if (bookList.isEmpty()) {
      item {
        Text(text = "No books to display", Modifier.testTag(C.Tag.BookListComp.empty_list_text))
      }
    } else {
      itemsIndexed(bookList) { i, book ->
        BookDisplayComponent(
            modifier = Modifier
                .testTag("${i}_" + C.Tag.BookDisplayComp.book_display_container)
                .clickable { onBookClick(book.uuid) }, // Pass book's ID to the click handler
            book = book)
        if (i < bookList.size - 1) {
          HorizontalDivider(
              modifier = Modifier.testTag(C.Tag.BookListComp.divider),
          )
        }
      }
    }
  }
}
