package com.android.bookswap.utils

import com.android.bookswap.data.DataBook
import io.mockk.Matcher
import io.mockk.MockKMatcherScope

data class DataBookMatcher(val expectedBook: DataBook, val idEq: Boolean) : Matcher<DataBook> {

  override fun match(arg: DataBook?): Boolean {
    return if (idEq) expectedBook == arg else expectedBook == arg?.copy(uuid = expectedBook.uuid)
  }

  override fun toString() = "matchBook(book=$expectedBook, idEq=$idEq)"
}

fun MockKMatcherScope.matchDataBook(dataBook: DataBook, idEq: Boolean = false): DataBook =
    match(DataBookMatcher(dataBook, idEq))
