package com.android.bookswap.model

import android.Manifest
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import com.android.bookswap.BuildConfig
import java.io.File

/**
 * Easy code abstraction allowing the user to open their photo app and take a photo of their choice
 * and then confirm it. Please call [Init] (Composable) in your UI (doesn't matter where)
 *
 * @param context the ui context
 * @param callback what to do when the user has finished (or failed) taking a photo
 */
class PhotoRequester(
    private val context: Context,
    private val callback: (Result<ImageBitmap>) -> Unit
) {
  private var initialized = false // Confirm that init was called

  private lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
  private lateinit var cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>

  private var fileUri: Uri? = null // Temp file path

  @Composable
  fun Init() {
    initialized = true

    // When a photo is called
    cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
          if (isImageSaved) {
            val source = ImageDecoder.createSource(context.contentResolver, fileUri!!)

            callback(Result.success(ImageDecoder.decodeBitmap(source).asImageBitmap()))
          } else {
            fileUri = null
            callback(Result.failure(Exception("Image could not be saved in phone.")))
          }
        }
    // Ask for permission before using the camera.
    permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            permissionGranted ->
          if (permissionGranted) {
            fileUri = getTempUri()
            cameraLauncher.launch(fileUri)
          } else {
            callback(Result.failure(Exception("Permission denied.")))
          }
        }
  }

  /** Request a photo from the user, this will ends-up calling [callback] */
  fun requestPhoto() {
    if (!initialized) throw Error("Always call Init() before using requestPhoto()")

    permissionLauncher.launch(Manifest.permission.CAMERA)
  }

  /** Generate a Uri for temporary file storage in the app cache. */
  private fun getTempUri(): Uri {
    val tempFile =
        File.createTempFile(
            "temp_image_file_", /* prefix */
            ".jpg", /* suffix */
            context.cacheDir /* cache directory */)

    // Create sandboxed url for this temp file - needed for the camera API
    val uri =
        FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider", /* needs to match the provider information in the manifest */
            tempFile)

    return uri
  }
}
