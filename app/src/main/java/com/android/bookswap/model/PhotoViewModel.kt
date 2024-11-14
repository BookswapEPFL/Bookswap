package com.android.bookswap.model


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.bookswap.data.DataPhoto
import com.android.bookswap.data.repository.PhotoRepository
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import kotlinx.coroutines.launch

/**
 * ViewModel for managing photo-related operations.
 *
 * @property photoRepository Repository for handling photo data operations.
 */
class PhotoViewModel(private val photoRepository: PhotoRepository) : ViewModel() {
  /**
   * Generates a new UUID.
   *
   * @return A new UUID.
   */
  fun getNewUid(): UUID {
    return photoRepository.getNewUUID()
  }
  /**
   * Initializes the photo repository.
   *
   * @param callback Callback to be invoked with the result of the initialization.
   */
  fun init(callback: (Result<Unit>) -> Unit) {
    viewModelScope.launch { photoRepository.init(callback) }
  }
  /**
   * Retrieves a photo by its UUID.
   *
   * @param uid UUID of the photo to retrieve.
   * @param onSuccess Callback to be invoked with the retrieved photo.
   * @param onFailure Callback to be invoked with an exception if retrieval fails.
   */
  fun getPhoto(uid: UUID, callback: (Result<DataPhoto>) -> Unit) {
    viewModelScope.launch { photoRepository.getPhoto(uid, callback) }
  }
  /**
   * Converts a Bitmap to a Base64-encoded string.
   *
   * @param bitmap The Bitmap to convert.
   * @return The Base64-encoded string representation of the Bitmap.
   */
  fun bitmapToBase64(bitmap: Bitmap): String {
    return photoRepository.bitmapToBase64(bitmap)
  }
  /**
   * Converts a Base64-encoded string to a Bitmap.
   *
   * @param base64 The Base64-encoded string to convert.
   * @return The Bitmap representation of the Base64-encoded string.
   */
  fun base64ToBitmap(base64: String): Bitmap {
    return photoRepository.base64ToBitmap(base64)
  }
  /**
   * Converts an ImageBitmap to a Bitmap.
   *
   * @param imageBitmap The ImageBitmap to convert.
   * @return The Bitmap representation of the ImageBitmap.
   */
  fun ImageBitmapToBitmap(imageBitmap: ImageBitmap): Bitmap {
    return imageBitmap.asAndroidBitmap()
  }
  /**
   * Composable function to take a photo using the device's camera.
   *
   * @param callback Callback to be invoked with the result of the photo capture.
   */
  @Composable
  fun takePhoto(callback: (Result<DataPhoto>) -> Unit) {
    val photoRequester =
        PhotoRequester(LocalContext.current) { result ->
          result
              .onSuccess { imageBitmap ->
                val bitmap = ImageBitmapToBitmap(imageBitmap)
                val base64 = bitmapToBase64(bitmap)
                val uuid = getNewUid()
                val timestamp = System.currentTimeMillis()
                val dataPhoto = DataPhoto(uuid, "", timestamp, base64)
                callback(Result.success(dataPhoto))
              }
              .onFailure { exception -> callback(Result.failure(exception)) }
        }

    photoRequester.Init()
    photoRequester.requestPhoto()
  }
  /**
   * Converts a Bitmap to a Base64-encoded PNG string.
   *
   * @param bitmap The Bitmap to convert.
   * @return The Base64-encoded PNG string representation of the Bitmap.
   */
  private fun bitmapToPng(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
  }
  /**
   * Converts a Bitmap to a Base64-encoded JPEG string.
   *
   * @param bitmap The Bitmap to convert.
   * @return The Base64-encoded JPEG string representation of the Bitmap.
   */
  private fun bitmapToJpeg(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
  }
  /**
   * Converts a Base64-encoded JPEG string to a Bitmap.
   *
   * @param base64 The Base64-encoded JPEG string to convert.
   * @return The Bitmap representation of the Base64-encoded JPEG string.
   */
  private fun JpegToBitmap(base64: String): Bitmap {
    val decodedString = Base64.decode(base64, Base64.DEFAULT)
    return android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
  }
  /**
   * Converts a Base64-encoded PNG string to a Bitmap.
   *
   * @param base64 The Base64-encoded PNG string to convert.
   * @return The Bitmap representation of the Base64-encoded PNG string.
   */
  private fun PngToBitmap(base64: String): Bitmap {
    val decodedString = Base64.decode(base64, Base64.DEFAULT)
    return android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
  }
  /**
   * Saves a photo.
   *
   * @param bitmap The Bitmap of the photo to save.
   * @param onSuccess Callback to be invoked with the saved photo.
   * @param onFailure Callback to be invoked with an exception if saving fails.
   */
  fun savePhoto(bitmap: Bitmap, onSuccess: (DataPhoto) -> Unit, onFailure: (Exception) -> Unit) {
    val base64 = bitmapToBase64(bitmap)
    val uuid = getNewUid()
    val timestamp = System.currentTimeMillis()

    val dataPhoto = DataPhoto(uuid, url = "", timestamp, base64)
    viewModelScope.launch {
      photoRepository.addPhoto(
          dataPhoto,
          callback = { result ->
            result.onSuccess { onSuccess(dataPhoto) }
            result.onFailure { onFailure(it as Exception) }
          })
    }
  }
  /**
   * Saves a photo from a Base64-encoded JPEG string.
   *
   * @param base64 The Base64-encoded JPEG string of the photo to save.
   * @param onSuccess Callback to be invoked with the saved photo.
   * @param onFailure Callback to be invoked with an exception if saving fails.
   */
  fun savePhotoJPEG(
      base64: String,
      onSuccess: (DataPhoto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    JpegToBitmap(base64).let { bitmap -> savePhoto(bitmap, onSuccess, onFailure) }
  }
  /**
   * Saves a photo from a Base64-encoded PNG string.
   *
   * @param base64 The Base64-encoded PNG string of the photo to save.
   * @param onSuccess Callback to be invoked with the saved photo.
   * @param onFailure Callback to be invoked with an exception if saving fails.
   */
  fun savePhotoPNG(base64: String, onSuccess: (DataPhoto) -> Unit, onFailure: (Exception) -> Unit) {
    savePhoto(PngToBitmap(base64), onSuccess, onFailure)
  }
  /**
   * Converts a URL to a Bitmap.
   *
   * @param urlString The URL of the image to convert.
   * @return The Bitmap representation of the image, or null if conversion fails.
   */
  fun urlToBitmap(urlString: String): Bitmap? {
    return try {
      val url = URL(urlString)
      val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
      connection.doInput = true
      connection.connect()
      val input: InputStream = connection.inputStream
      BitmapFactory.decodeStream(input)
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
  /**
   * Converts a Bitmap to a Base64-encoded URL string.
   *
   * @param bitmap The Bitmap to convert.
   * @return The Base64-encoded URL string representation of the Bitmap.
   */
  fun bitmapToUrl(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
  }
  /**
   * Converts a URL to a DataPhoto object.
   *
   * @param urlString The URL of the image to convert.
   * @return The DataPhoto object representation of the image, or null if conversion fails.
   */
  fun urlToDataPhoto(urlString: String): DataPhoto? {
    return urlToBitmap(urlString)?.let { bitmap ->
      val base64 = bitmapToBase64(bitmap)
      val uuid = getNewUid()
      val timestamp = System.currentTimeMillis()
      DataPhoto(uuid, urlString, timestamp, base64)
    }
  }

  /**
   * Converts a Base64-encoded URL string to a Bitmap.
   *
   * @param base64 The Base64-encoded URL string to convert.
   * @return The Bitmap representation of the Base64-encoded URL string.
   */
  fun bitmapToDataUrl(
      bitmap: Bitmap,
      format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
  ): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(format, 100, outputStream) // Compress the Bitmap to the specified format
    val byteArray = outputStream.toByteArray()
    val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

    // Determine the MIME type based on the format
    val mimeType =
        when (format) {
          Bitmap.CompressFormat.JPEG -> "image/jpeg"
          Bitmap.CompressFormat.PNG -> "image/png"
          Bitmap.CompressFormat.WEBP -> "image/webp"
          else -> "image/png" // default to PNG if format is unknown
        }

    return "data:$mimeType;base64,$base64String"
  }
}
