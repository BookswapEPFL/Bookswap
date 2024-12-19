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
import java.util.UUID
import org.junit.Before
import org.junit.Test

class BookFromChatGPTTest {

  private val context: Context = mockk()
  private val photoFirebaseStorageRepository: PhotoFirebaseStorageRepository = mockk()
  private val booksRepository: BooksRepository = mockk()
  private val bitmap: Bitmap = mockk()
  private val uuid = UUID.randomUUID()

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
      every { photoFirebaseStorageRepository.deletePhotoFromStorageWithUrl(any(), any()) } answers {
          secondArg<(Result<Unit>) -> Unit>()(Result.success(Unit))
      }

    mockkConstructor(ChatGPTApiService::class)
    mockkConstructor(ImageToDataSource::class)
    every { anyConstructed<ImageToDataSource>().analyzeImage(testURL, any(), any()) } answers
        {
          val map = hashMapOf("isbn" to testISBN)
          secondArg<(Map<String, String>) -> Unit>()(map)
        }

    mockkConstructor(GoogleBookDataSource::class)
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(testISBN, any(), any()) } answers
        {
          val dataBook: DataBook = mockk()
          every {
            dataBook.copy(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
          } returns dataBook
          every { dataBook.uuid } returns UUID.randomUUID()
          thirdArg<(Result<DataBook>) -> Unit>()(Result.success(dataBook))
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
    val callback = mockk<(BookFromChatGPT.Companion.ErrorType, UUID?) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, uuid, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.ErrorType.FIREBASE_STORAGE_ERROR, null)
    }
  }

  @Test
  fun `error on analyzeImage return error`() {
    every { anyConstructed<ImageToDataSource>().analyzeImage(any(), any(), any()) } answers
        {
          thirdArg<(String) -> Unit>()("")
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.ErrorType, UUID?) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, uuid, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.ErrorType.CHATGPT_ANALYZER_ERROR, null)
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
    val callback = mockk<(BookFromChatGPT.Companion.ErrorType, UUID?) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, uuid, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.ErrorType.CHATGPT_ANALYZER_ERROR, null)
    }
  }

  @Test
  fun `error on getBookFromISBN return error`() {
    every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(any(), any(), any()) } answers
        {
          thirdArg<(Result<DataBook>) -> Unit>()(Result.failure(Exception("")))
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.ErrorType, UUID?) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, uuid, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.ErrorType.ISBN_ERROR, null)
    }
  }

  @Test
  fun `error on addBook return error`() {
    every { booksRepository.addBook(any(), any()) } answers
        {
          secondArg<(Result<Unit>) -> Unit>()(Result.failure(Exception("")))
        }

    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.ErrorType, UUID?) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, uuid, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.ErrorType.BOOK_ADD_ERROR, null)
    }
  }

  @Test
  fun `book is added correctly`() {
    val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)
    val callback = mockk<(BookFromChatGPT.Companion.ErrorType, UUID?) -> Unit>(relaxed = true)
    bookFromChatGPT.addBookFromImage(bitmap, uuid, callback)

    verify(exactly = 1, timeout = 500) {
      callback.invoke(BookFromChatGPT.Companion.ErrorType.NONE, any())
    }
  }

    @Test
    fun `photo is deleted and not added to dataBook photo attribute`() {

        // Mock the DataBook creation to ensure the `photo` attribute remains null or empty
        val testURL = "AN_URL"
        val testISBN = "9780435123437"
        val expectedUuid = uuid // Use the predefined UUID
        val dataBook: DataBook = mockk(relaxed = true) {
            every { uuid } returns expectedUuid
            every { copy(photo = any()) } throws IllegalStateException("Photo should not be added")
        }

        every { anyConstructed<GoogleBookDataSource>().getBookFromISBN(testISBN, any(), any()) } answers {
            thirdArg<(Result<DataBook>) -> Unit>()(Result.success(dataBook))
        }

        val callback = mockk<(BookFromChatGPT.Companion.ErrorType, UUID?) -> Unit>(relaxed = true)
        val bookFromChatGPT = BookFromChatGPT(context, photoFirebaseStorageRepository, booksRepository)

        // Call the method
        bookFromChatGPT.addBookFromImage(bitmap, uuid, callback)

        // Verify that the photo is deleted
        verify(exactly = 1, timeout = 500) {
            photoFirebaseStorageRepository.deletePhotoFromStorageWithUrl(testURL, any())
        }

        // Verify the book is added with no photo attribute modification
        verify(exactly = 1, timeout = 500) {
            booksRepository.addBook(dataBook, any())
        }

        // Ensure callback is invoked with no error
        verify(exactly = 1, timeout = 500) {
            callback.invoke(BookFromChatGPT.Companion.ErrorType.NONE, any())
        }
    }
}
