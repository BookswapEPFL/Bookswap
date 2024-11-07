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

class PhotoRequester(
    private val context: Context,
    private val callback: (Result<ImageBitmap>) -> Unit
) {
  private var initialized = false

  private lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
  private lateinit var cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>

  private var fileUri: Uri? = null

  @Composable
  fun Init() {
    initialized = true

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

  fun requestPhoto() {
    if (!initialized) throw Error("Always call Init() before using requestPhoto()")

    permissionLauncher.launch(Manifest.permission.CAMERA)
  }

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
