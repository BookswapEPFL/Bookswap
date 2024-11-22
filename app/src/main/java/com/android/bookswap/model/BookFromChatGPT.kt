package com.android.bookswap.model

import android.content.Context
import android.graphics.Bitmap
import com.android.bookswap.R
import com.android.bookswap.data.repository.BooksRepository
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.android.bookswap.data.source.api.ChatGPTApiService
import com.android.bookswap.data.source.api.GoogleBookDataSource
import com.android.bookswap.data.source.network.ImageToDataSource
import java.util.UUID

class BookFromChatGPT(
    private val context: Context,
    private val photoStorageRepository: PhotoFirebaseStorageRepository,
    private val booksRepository: BooksRepository,
) {
  fun addBookFromImage(image: Bitmap, userUUID: UUID, callback: (Error) -> Unit) {
    photoStorageRepository.addPhotoToStorage(UUID.randomUUID().toString(), image) { urlResult ->
      if (urlResult.isFailure) {
        callback(Error.FIREBASE_STORAGE_ERROR)
        return@addPhotoToStorage
      }
      val imageToData = ImageToDataSource(ChatGPTApiService(context))
      imageToData.analyzeImage(
          urlResult.getOrThrow(),
          onSuccess = { map ->
            val isbn = map["isbn"]
            if (isbn == null || isbn == ImageToDataSource.UNDEFINED_ATTRIBUTE) {
              callback(Error.CHATGPT_ANALYZER_ERROR)
              return@analyzeImage
            }

            GoogleBookDataSource(context).getBookFromISBN(isbn, userUUID) { dataBookResult ->
              if (dataBookResult.isFailure) {
                callback(Error.ISBN_ERROR)
                return@getBookFromISBN
              }
              booksRepository.addBook(dataBookResult.getOrThrow()) { bookResult ->
                if (bookResult.isFailure) {
                  callback(Error.BOOK_ADD_ERROR)
                } else {
                  callback(Error.NONE)
                }
              }
            }
          },
          onError = { callback(Error.CHATGPT_ANALYZER_ERROR) })
    }
  }

  companion object {
    enum class Error(val message: Int) {
      NONE(R.string.book_gpt_error_success),
      FIREBASE_STORAGE_ERROR(R.string.book_gpt_error_firebase),
      CHATGPT_ANALYZER_ERROR(R.string.book_gpt_error_chatgpt),
      ISBN_ERROR(R.string.book_gpt_error_isbn),
      BOOK_ADD_ERROR(R.string.app_name)
    }
  }
}
