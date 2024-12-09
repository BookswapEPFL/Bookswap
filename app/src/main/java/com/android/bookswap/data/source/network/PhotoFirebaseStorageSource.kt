package com.android.bookswap.data.source.network

import android.graphics.Bitmap
import android.util.Log
import com.android.bookswap.data.repository.PhotoFirebaseStorageRepository
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

/** Constants : */
/** Quality of the compressed image */
const val QUALITY_COMPRESSION = 100

/**
 * A class that implements the PhotoFirebaseStorageRepository interface using Firebase Storage as
 * the data source.
 *
 * @property storage The Firebase Storage instance.
 */
class PhotoFirebaseStorageSource(private val storage: FirebaseStorage) :
    PhotoFirebaseStorageRepository {

  /**
   * Initializes the PhotoFirebaseStorageSource.
   *
   * @param callback A callback function that receives Result.success(Unit) on success or
   *   Result.failure(exception) on failure.
   */
  override fun init(callback: (Result<Unit>) -> Unit) {
    try {
      callback(Result.success(Unit))
    } catch (e: Exception) {
      Log.e("PhotoSource", "Initialization failed: ${e.message}")
      callback(Result.failure(e))
    }
  }

  /**
   * Adds a photo to Firebase Storage.
   *
   * @param photoId The ID of the photo to add.
   * @param bitmap The photo to add.
   * @param callback A callback function that receives Result.success(String) with the URL of the
   *   photo on success or Result.failure(exception) on failure.
   */
  override fun addPhotoToStorage(
      photoId: String,
      bitmap: Bitmap,
      callback: (Result<String>) -> Unit
  ) {
    // Convert Bitmap to a JPEG Byte Array
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_COMPRESSION, baos)
    val imageData = baos.toByteArray()

    // Define a storage reference with a unique photoId
    val storageRef = storage.reference.child("images/$photoId.jpg")

    storageRef
        .putBytes(imageData)
        .addOnSuccessListener {
          // Get the download URL after successful upload
          storageRef.downloadUrl
              .addOnSuccessListener { url ->
                callback(Result.success(url.toString())) // Return the URL
              }
              .addOnFailureListener { e -> callback(Result.failure(e)) }
        }
        .addOnFailureListener { e -> callback(Result.failure(e)) }
  }

  /**
   * Deletes a photo from Firebase Storage.
   *
   * @param photoId The ID of the photo to delete.
   * @param callback A callback function that receives Result.success(Unit) on success or
   *   Result.failure(exception) on failure.
   */
  override fun deletePhotoFromStorage(photoId: String, callback: (Result<Unit>) -> Unit) {
    val storageRef = storage.reference.child("images/$photoId.jpg")
    storageRef
        .delete()
        .addOnSuccessListener { callback(Result.success(Unit)) }
        .addOnFailureListener { e -> callback(Result.failure(e)) }
  }

  /**
   * Deletes a photo from Firebase Storage using the photo URL.
   *
   * @param photoUrl The URL of the photo to delete.
   * @param callback A callback function that receives Result.success(Unit) on success or
   *   Result.failure(exception) on failure.
   */
  override fun deletePhotoFromStorageWithUrl(photoUrl: String, callback: (Result<Unit>) -> Unit) {
    val regex = Regex("""images%2F([a-zA-Z0-9\-]+)""")
    val matchResult = regex.find(photoUrl)

    if (matchResult != null && matchResult.groups[1] != null) {
      val photoId = matchResult.groups[1]!!.value
      deletePhotoFromStorage(photoId, callback)
    } else {
      callback(Result.failure(IllegalArgumentException("Invalid photo URL")))
    }
  }
}
