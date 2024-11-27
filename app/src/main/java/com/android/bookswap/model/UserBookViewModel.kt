package com.android.bookswap.model

import android.util.Log
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import java.util.UUID

class UserBookViewModel(
    private val booksRepository: BooksRepository,
    )
{
    fun getBooks(bookList: List<UUID>): List<DataBook> {
        var DataBookList = mutableListOf<DataBook>()
        for (bookId in bookList) {
            booksRepository.getBook(
                bookId,
                { dataBook ->
                    DataBookList.add(dataBook)
                },
                { exception ->
                    Log.e("UserBookViewModel", "Error getting book with UUID: $bookId here is the error: $exception")
                }
            )
        }
        return DataBookList
    }
}