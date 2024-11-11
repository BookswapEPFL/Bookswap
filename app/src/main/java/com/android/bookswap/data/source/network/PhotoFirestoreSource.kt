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

/** Constants : */
/** Name of the Firestore collection that stores users */
const val PHOTO_COLLECTION_PATH = "photos"
/** Quality of the compressed image */
const val COMPRESSION_QUALITY = 70
/** Offset for the byte array */
const val OFFSET = 0

/**
 * A class that implements the PhotoRepository interface using Firebase Firestore as the data
 * source.
 *
 * @property db The Firestore database instance.
 */
class PhotoFirestoreSource(private val db: FirebaseFirestore) : PhotoRepository {

  /**
   * Generates and returns a new unique ID for a photo in Firestore.
   *
   * @return A new UUID.
   */
  override fun getNewUUID(): UUID {
    return UUID.randomUUID()
  }

  /**
   * Initializes the PhotoFirestoreSource.
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
   * Fetches a specific photo from Firestore by UUID.
   *
   * @param uuid the UUID of the photo to fetch
   * @param callback callback function that receives Result.success(DataPhoto) when operation
   *   succeed of Result.failure(exception) if error
   */
  override fun getPhoto(uuid: UUID, callback: (Result<DataPhoto>) -> Unit) {
    db.collection(PHOTO_COLLECTION_PATH).document(uuid.toString()).get().addOnCompleteListener {
        task ->
      if (task.isSuccessful) {
        val photo = task.result?.let { documentToPhoto(it) }
        if (photo != null) {
          callback(Result.success(photo))
        } else {
          callback(Result.failure(Exception("Photo not found or failed to convert")))
        }
      } else {
        task.exception?.let { callback(Result.failure(it)) }
      }
    }
  }

  // Maybe not in the repository (I think it should be in the viewmodel)
  /**
   * Converts a Bitmap object to a Base64 encoded string.
   *
   * @param bitmap The Bitmap object to convert.
   * @return The Base64 encoded string representation of the bitmap.
   */
  override fun bitmapToBase64(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, baos)
    val byteArray = baos.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
  }

  // Same, maybe not in the repository (I think it should be in the viewmodel)
  /**
   * Converts a Base64 encoded string to a Bitmap object.
   *
   * @param base64 The Base64 encoded string to convert.
   * @return The Bitmap object representation of the Base64 string.
   */
  override fun base64ToBitmap(base64: String): Bitmap {
    val byteArray = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(byteArray, OFFSET, byteArray.size)
  }

  /**
   * Uploads a photo to Firestore.
   *
   * @param dataPhoto The DataPhoto object to be added to Firestore.
   * @param callback Callback function that is called when the photo is successfully added or error
   *   otherwise.
   */
  override fun addPhoto(dataPhoto: DataPhoto, callback: (Result<Unit>) -> Unit) {
    Log.d("PhotoFirestoreRepository", "Attempting to add photo with UUID: ${dataPhoto.uuid}")

    performFirestoreOperation(
        db.collection(PHOTO_COLLECTION_PATH).document(dataPhoto.uuid.toString()).set(dataPhoto),
        {
          Log.d("PhotoFirestoreRepository", "Photo added successfully with UUID: ${dataPhoto.uuid}")
          callback(Result.success(Unit))
        },
        { e ->
          Log.e("PhotoFirestoreRepository", "Failed to add photo: ${e.message}", e)
          callback(Result.failure(e))
        })
  }

  /**
   * Converts a Firestore document to a DataPhoto object.
   *
   * @param document The Firestore document to convert.
   * @return The DataPhoto object if conversion is successful, otherwise null.
   */
  fun documentToPhoto(document: DocumentSnapshot): DataPhoto? {
    return try {
      val uuid = UUID.fromString(document.getString("uuid")) ?: return null
      val url = document.getString("url") ?: ""
      val timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()
      val base64 = document.getString("base64") ?: return null

      DataPhoto(uuid = uuid, url = url, timestamp = timestamp, base64 = base64)
    } catch (e: Exception) {
      Log.e("PhotoFirestoreRepository", "Error converting document to DataPhoto", e)
      null
    }
  }

  /**
   * Helper function to perform Firestore operations (add, update, delete). Executes the provided
   * Firestore task and triggers success or failure callbacks.
   *
   * @param task The Firestore task to execute.
   * @param onSuccess Callback function that is called when the task is successful.
   * @param onFailure Callback function that is called when the task fails.
   */
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
