package com.android.bookswap.ui.components

import android.util.Log
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.bookswap.data.DataBook
import com.android.bookswap.resources.C

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
) {
    Column(modifier = modifier) {
        for (book in bookList) {
            Text(
                text = "Title: ${book.title}",
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
