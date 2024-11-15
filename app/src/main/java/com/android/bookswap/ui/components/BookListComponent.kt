package com.android.bookswap.ui.components

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
import com.android.bookswap.ui.books.BookProfileScreen

val DIVIDER_THICKNESS_DP = Dp.Hairline

@Composable
fun BookListComponent(
    modifier: Modifier = Modifier,
    bookList: List<DataBook> = emptyList(),
) {
  LazyColumn(
      modifier = modifier.fillMaxWidth().testTag("BookListColumn"),
      state = rememberLazyListState(),
      contentPadding = PaddingValues(PADDING_HORIZONTAL_DP, PADDING_VERTICAL_DP),
      horizontalAlignment = Alignment.CenterHorizontally,
      flingBehavior = ScrollableDefaults.flingBehavior(),
      userScrollEnabled = true,
  ) {
    if (bookList.isEmpty()) {
      item { Text(text = "No books to display", Modifier.testTag("mapDraggableMenuNoBook")) }
    } else {
      itemsIndexed(bookList) { i, book ->
        BookDisplayComponent(Modifier.testTag("mapDraggableMenuBookBox${i}"), book = book)
        if (i < bookList.size - 1) {
          HorizontalDivider(
              modifier = Modifier.testTag("mapDraggableMenuBookBoxDivider"),
          )
          BookProfileScreen(book,{}, {})
        }
      }
    }
  }
}
