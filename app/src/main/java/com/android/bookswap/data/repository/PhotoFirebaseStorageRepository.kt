package com.android.bookswap.data.repository

import android.graphics.Bitmap

interface PhotoFirebaseStorageRepository {
  /**
   * Initializes the photo storage repository.
   *
   * @param callback A callback function that is invoked with the result of the initialization.
   */
  fun init(callback: (Result<Unit>) -> Unit)
  /**
   * Adds a photo to the storage.
   *
   * @param photoId The unique identifier for the photo.
   * @param bitmap The bitmap representation of the photo.
   * @param callback A callback function that is invoked with the result of the operation.
   */
  fun addPhotoToStorage(photoId: String, bitmap: Bitmap, callback: (Result<String>) -> Unit)
  /**
   * Deletes a photo from the storage using its unique identifier.
   *
   * @param photoId The unique identifier for the photo.
   * @param callback A callback function that is invoked with the result of the operation.
   */
  fun deletePhotoFromStorage(photoId: String, callback: (Result<Unit>) -> Unit)
  /**
   * Deletes a photo from the storage using its URL.
   *
   * @param photoUrl The URL of the photo.
   * @param callback A callback function that is invoked with the result of the operation.
   */
  fun deletePhotoFromStorageWithUrl(photoUrl: String, callback: (Result<Unit>) -> Unit)
}
