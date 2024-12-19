package com.android.bookswap.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
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
  fun addBookFromImage(image: Bitmap, userUUID: UUID, callback: (ErrorType, UUID?) -> Unit) {
    // Send the image to firestore storage
    photoStorageRepository.addPhotoToStorage(UUID.randomUUID().toString(), image) { urlResult ->
      if (urlResult.isFailure) {
        callback(ErrorType.FIREBASE_STORAGE_ERROR, null)
        return@addPhotoToStorage
      }

      Log.i("BookFromChatGPT", "Sending photo to ChatGPT")

      // Send the url to ChatGPT
      val photoUrl = urlResult.getOrThrow()
      val imageToData = ImageToDataSource(ChatGPTApiService(context))
      imageToData.analyzeImage(
          photoUrl,
          onSuccess = { map ->
            // Get isbn from result and removes the '-' characters
            val isbn = map["isbn"]?.replace("-", "")
            if (isbn == null || isbn == ImageToDataSource.UNDEFINED_ATTRIBUTE) {
              photoStorageRepository.deletePhotoFromStorageWithUrl(photoUrl) {_ -> }
              callback(ErrorType.CHATGPT_ANALYZER_ERROR, null)
              return@analyzeImage
            }

            // Request book from ISBN
            GoogleBookDataSource(context).getBookFromISBN(isbn, userUUID) { dataBookResult ->
              if (dataBookResult.isFailure) {
                photoStorageRepository.deletePhotoFromStorageWithUrl(photoUrl) {_ -> }
                callback(ErrorType.ISBN_ERROR, null)
                return@getBookFromISBN
              }

              // Add photo url to dataBook
              val dataBook = dataBookResult.getOrThrow()//.copy(photo = urlResult.getOrThrow())

              // Add book
              booksRepository.addBook(dataBook) { bookResult ->
                if (bookResult.isFailure) {
                  Log.i("DeleteBookAfterGPT", "Deleting photo from storage")
                  photoStorageRepository.deletePhotoFromStorageWithUrl(photoUrl) { deleteResult ->
                    if (deleteResult.isFailure) {
                      callback(ErrorType.FIREBASE_STORAGE_ERROR, null)
                    } else {
                      callback(ErrorType.BOOK_ADD_ERROR, null)
                    }
                  }
                } else {
                  Log.i("DeleteBookAfterGPT", "Deleting photo from storage")
                  photoStorageRepository.deletePhotoFromStorageWithUrl(photoUrl) { _ -> }
                  callback(ErrorType.NONE, dataBookResult.getOrThrow().uuid)
                }
              }
            }
          },
          onError = {
            Log.i("DeleteBookAfterGPT", "Deleting photo from storage")
            photoStorageRepository.deletePhotoFromStorageWithUrl(photoUrl) { deleteResult ->
              if (deleteResult.isFailure) {
                callback(ErrorType.FIREBASE_STORAGE_ERROR, null)
              } else {
                callback(ErrorType.CHATGPT_ANALYZER_ERROR, null)
              }
            }
          })
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
