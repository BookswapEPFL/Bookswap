package com.android.bookswap.data.source.network

import android.graphics.Bitmap
import android.util.Log
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class PhotoFirebaseStorageSource(private val storage: FirebaseStorage) :
    PhotoFirebaseStorageRepository {

  override fun init(callback: (Result<Unit>) -> Unit) {
    try {
      callback(Result.success(Unit))
    } catch (e: Exception) {
      Log.e("PhotoSource", "Initialization failed: ${e.message}")
      callback(Result.failure(e))
    }
  }

  override fun addPhotoToStorage(
      photoId: String,
      bitmap: Bitmap,
      callback: (Result<String>) -> Unit
  ) {
    // Convert Bitmap to a JPEG Byte Array
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageData = baos.toByteArray()

    // Define a storage reference with a unique photoId
    val storageRef = storage.reference.child("images/$photoId.jpg")

    storageRef
        .putBytes(imageData)
        .addOnSuccessListener {
          // Get the download URL after successful upload
          storageRef.downloadUrl
              .addOnSuccessListener { uri ->
                callback(Result.success(uri.toString())) // Return the URL
              }
              .addOnFailureListener { e -> callback(Result.failure(e)) }
        }
        .addOnFailureListener { e -> callback(Result.failure(e)) }
  }
}
