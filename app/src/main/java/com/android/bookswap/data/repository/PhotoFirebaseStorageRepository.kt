package com.android.bookswap.data.repository

import android.graphics.Bitmap

interface PhotoFirebaseStorageRepository {

  fun init(callback: (Result<Unit>) -> Unit)

  fun addPhotoToStorage(photoId: String, bitmap: Bitmap, callback: (Result<String>) -> Unit)
  // getphoto is useless (as we can use the url to directly retrieve the picture and show it )
  fun deletePhotoFromStorage(photoId: String, callback: (Result<Unit>) -> Unit)

  fun deletePhotoFromStorageWithUrl(photoUrl: String, callback: (Result<Unit>) -> Unit)
}
