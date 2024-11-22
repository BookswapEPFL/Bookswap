package com.android.bookswap.model

import android.content.Context
import android.graphics.Bitmap
import com.android.bookswap.data.DataBook
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.data.source.api.ChatGPTApiService
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.data.source.network.ImageToDataSource
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class BookFromChatGPTTest {

  private val context: Context = mockk()
  private val photoFirebaseStorageRepository: PhotoFirebaseStorageRepository = mockk()
  private val booksRepository: BooksRepository = mockk()
  private val bitmap: Bitmap = mockk()

  @Before
  fun setup() {
    val queue: RequestQueue = mockk()
    mockkStatic(Volley::class)
    every { Volley.newRequestQueue(any()) } returns queue

    val testURL = "AN_URL"
    val testISBN = "9780435123437"
    every { photoFirebaseStorageRepository.addPhotoToStorage(any(), any(), any()) } answers
        {
          thirdArg<(Result<String>) -> Unit>()(Result.success(testURL))
        }

    mockkConstructor(ChatGPTApiService::class)
    mockkConstructor(ImageToDataSource::class)
    every { anyConstructed<ImageToDataSource>().analyzeImage(testURL, any(), any()) } answers
        {
          val map = hashMapOf("isbn" to testISBN)
          secondArg<(Map<String, String>) -> Unit>()(map)
        }

    mockkConstructor(GoogleBookDataSource::class)
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(testISBN, any()) } answers
        {
          val dataBook: DataBook = mockk()
          secondArg<(Result<DataBook>) -> Unit>()(Result.success(dataBook))
        }

    every { booksRepository.addBook(any(), any()) } answers
        {
          secondArg<(Result<Unit>) -> Unit>()(Result.success(Unit))
        }
  }

  @Test
  fun `error on addPhotoToStorage return error`() {
    every { photoFirebaseStorageRepository.addPhotoToStorage(any(), any(), any()) } answers
        {
          thirdArg<(Result<String>) -> Unit>()(Result.failure(Exception("")))
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.Error) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.Error.FIREBASE_STORAGE_ERROR)
    }
  }

  @Test
  fun `error on analyzeImage return error`() {
    every { anyConstructed<ImageToDataSource>().analyzeImage(any(), any(), any()) } answers
        {
          thirdArg<(String) -> Unit>()("")
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.Error) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.Error.CHATGPT_ANALYZER_ERROR)
    }
  }

  @Test
  fun `error on analyzeImage isbn error`() {
    every { anyConstructed<ImageToDataSource>().analyzeImage(any(), any(), any()) } answers
        {
          val map = hashMapOf("isbn" to ImageToDataSource.UNDEFINED_ATTRIBUTE)
          secondArg<(Map<String, String>) -> Unit>()(map)
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.Error) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.Error.CHATGPT_ANALYZER_ERROR)
    }
  }

  @Test
  fun `error on getBookFromISBN return error`() {
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(any(), any()) } answers
        {
          secondArg<(Result<DataBook>) -> Unit>()(Result.failure(Exception("")))
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.Error) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.Error.ISBN_ERROR)
    }
  }

  @Test
  fun `error on addBook return error`() {
    every { booksRepository.addBook(any(), any()) } answers
        {
          secondArg<(Result<Unit>) -> Unit>()(Result.failure(Exception("")))
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.Error) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.Error.BOOK_ADD_ERROR)
    }
  }

  @Test
  fun `book is added correctly`() {
    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.Error) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, callback)

    verify(exactly = 1, timeout = 500) { callback.invoke(BookFromChatGPT.Companion.Error.NONE) }
  }
}
