package com.android.bookswap.data.repository

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow

interface PhotoFirebaseStorageRepository {

    fun init(callback: (Result<Unit>) -> Unit)

    fun addPhotoToStorage(photoId: String, bitmap: Bitmap, callback: (Result<String>) -> Unit)
    //getphoto is useless (as we can use the url to directly retrieve the picture and show it )
}