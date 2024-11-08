package com.android.bookswap.data.source.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.android.bookswap.data.DataPhoto
import com.android.bookswap.data.repository.PhotoRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.UUID

const val PHOTO_COLLECTION_PATH = "photos"

class PhotoFirestoreSource(private val db: FirebaseFirestore) : PhotoRepository {

  // Generates and returns a new unique ID for a photo in Firestore
  override fun getNewUid(): UUID {
    return UUID.randomUUID()
  }

  override fun init(callback: (Result<Unit>) -> Unit) {
    try {
      callback(Result.success(Unit))
    } catch (e: Exception) {
      Log.e("PhotoSource", "Initialization failed: ${e.message}")
      callback(Result.failure(e))
    }
  }

  // Fetches a specific photo from Firestore by UUID
  override fun getPhoto(uid: UUID, onSuccess: (DataPhoto) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(PHOTO_COLLECTION_PATH).document(uid.toString()).get().addOnCompleteListener { task
      ->
      if (task.isSuccessful) {
        val photo = task.result?.let { documentToPhoto(it) }
        if (photo != null) {
          onSuccess(photo)
        } else {
          onFailure(Exception("Photo not found or failed to convert"))
        }
      } else {
        task.exception?.let { onFailure(it) }
      }
    }
  }

  // Maybe not in the repository (I think it should be in the viewmodel)
  override fun bitmapToBase64(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
    val byteArray = baos.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
  }

  // Same, maybe not in the repository (I think it should be in the viewmodel)
  override fun base64ToBitmap(base64: String): Bitmap {
    val byteArray = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
  }

  // Uploads a photo to Firestore
  override fun addPhoto(
      dataPhoto: DataPhoto,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.d("PhotoFirestoreRepository", "Attempting to add photo with UUID: ${dataPhoto.uid}")

    performFirestoreOperation(
        db.collection(PHOTO_COLLECTION_PATH).document(dataPhoto.uid.toString()).set(dataPhoto),
        {
          Log.d("PhotoFirestoreRepository", "Photo added successfully with UUID: ${dataPhoto.uid}")
          onSuccess()
        },
        { e ->
          Log.e("PhotoFirestoreRepository", "Failed to add photo: ${e.message}", e)
          onFailure(e)
        })
  }

  // Converts a Firestore document to a DataPhoto object
  fun documentToPhoto(document: DocumentSnapshot): DataPhoto? {
    return try {
      val uid =
          document.getString("uid") ?: return null // UUID.fromString(document.getString("uid"))
      val url = document.getString("url") ?: ""
      val timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()
      val base64 = document.getString("base64") ?: return null

      DataPhoto(uid = uid, url = url, timestamp = timestamp, base64 = base64)
    } catch (e: Exception) {
      Log.e("PhotoFirestoreRepository", "Error converting document to DataPhoto", e)
      null
    }
  }

  // Helper function to perform Firestore operations (add, update, delete)
  private fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { onFailure(it) }
      }
    }
  }
}
