package com.android.bookswap.data.repository

import android.graphics.Bitmap
import android.health.connect.datatypes.Metadata
import com.android.bookswap.data.DataPhoto
import java.util.UUID

interface PhotoRepository {
    /** Generates a new unique id for a message */
    fun getNewUid(): UUID

    /**
     * Initialize the repository
     *
     * @param callback callback function that receives Result.success() when operation succeed of
     *   Result.failure(exception) if error
     */
    fun init(callback: (Result<Unit>) -> Unit)

    /**
     * Fetches a specific photo from Firestore by UUID
     *
     * @param uid the UUID of the photo to fetch
     * @param callback callback function that receives Result.success(DataPhoto) when operation succeed of
     *   Result.failure(exception) if error
     */
    fun getPhoto(uid: UUID, onSuccess: (DataPhoto) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Converts a Bitmap to a Base64 encoded string.
     *
     * @param bitmap the Bitmap to convert.
     * @return the Base64 encoded string.
     */
    fun bitmapToBase64(bitmap: Bitmap): String

    /**
     * Converts a Base64 encoded string to a Bitmap.
     *
     * @param base64 the Base64 encoded string to convert.
     * @return the Bitmap.
     */
    fun base64ToBitmap(base64: String): Bitmap

    /**
     * Uploads a photo to Firestore.
     *
     * @param dataPhoto the photo data to upload.
     * @param onSuccess callback function that receives Unit if success.
     * @param onFailure callback function that receives an exception if error.
     */
    fun addPhoto(dataPhoto: DataPhoto, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
