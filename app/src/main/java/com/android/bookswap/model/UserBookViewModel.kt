package com.android.bookswap.model

import android.util.Log
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserBookViewModel(
    private val booksRepository: BooksRepository,
) {
    suspend fun getBooks(bookList: List<UUID>): List<DataBook> {
        return suspendCoroutine { continuation ->
            val dataBookList = mutableListOf<DataBook>()
            val errors = mutableListOf<Exception>()

            for (bookId in bookList) {
                booksRepository.getBook(
                    bookId,
                    { dataBook ->
                        dataBookList.add(dataBook)
                        if (dataBookList.size + errors.size == bookList.size) {
                            continuation.resume(dataBookList)
                        }
                    },
                    { exception ->
                        errors.add(exception)
                        if (dataBookList.size + errors.size == bookList.size) {
                            continuation.resumeWithException(Exception("Errors occurred: $errors"))
                        }
                    }
                )
            }
        }
    }

}

