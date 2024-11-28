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
  fun addBookFromImage(image: Bitmap, userUUID: UUID, callback: (ErrorType) -> Unit) {
    // Send the image to firestore storage
    photoStorageRepository.addPhotoToStorage(UUID.randomUUID().toString(), image) { urlResult ->
      if (urlResult.isFailure) {
        callback(ErrorType.FIREBASE_STORAGE_ERROR)
        return@addPhotoToStorage
      }

      // Send the url to ChatGPT
      val imageToData = ImageToDataSource(ChatGPTApiService(context))
      imageToData.analyzeImage(
          urlResult.getOrThrow(),
          onSuccess = { map ->
            // Get isbn from result
            val isbn = map["isbn"]
            if (isbn == null || isbn == ImageToDataSource.UNDEFINED_ATTRIBUTE) {
              callback(ErrorType.CHATGPT_ANALYZER_ERROR)
              return@analyzeImage
            }

            // Request book from ISBN
            GoogleBookDataSource(context).getBookFromISBN(isbn, userUUID) { dataBookResult ->
              if (dataBookResult.isFailure) {
                callback(ErrorType.ISBN_ERROR)
                return@getBookFromISBN
              }

              // Add book
              booksRepository.addBook(dataBookResult.getOrThrow()) { bookResult ->
                if (bookResult.isFailure) {
                  callback(ErrorType.BOOK_ADD_ERROR)
                } else {
                  callback(ErrorType.NONE)
                }
              }
            }
          },
          onError = { callback(ErrorType.CHATGPT_ANALYZER_ERROR) })
    }
  }

  companion object {
    enum class ErrorType(val message: Int) {
      NONE(R.string.book_gpt_error_success),
      FIREBASE_STORAGE_ERROR(R.string.book_gpt_error_firebase),
      CHATGPT_ANALYZER_ERROR(R.string.book_gpt_error_chatgpt),
      ISBN_ERROR(R.string.book_gpt_error_isbn),
      BOOK_ADD_ERROR(R.string.app_name)
    }
  }
}
