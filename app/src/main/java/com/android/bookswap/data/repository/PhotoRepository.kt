package com.android.bookswap.data.repository

import android.graphics.Bitmap
import com.android.bookswap.data.DataPhoto

interface PhotoRepository {
    /** Generates a new unique id for a message */
    fun getNewUid(): String

    /**
     * Initialize the repository
     *
     * @param callback callback function that receives Result.success() when operation succeed of
     *   Result.failure(exception) if error
     */
    fun init(callback: (Result<Unit>) -> Unit)

    /**
     * Get all messages as a list
     *
     * @param callback callback function that receives list of messages if success
     */
    /**
     * Get all photos as a list.
     *
     * @param callback callback function that receives list of photos if success.
     */
    fun getPhotos(
        callback: (Result<List<DataPhoto>>) -> Unit,
    )

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
     * Uploads an image to Firestore.
     *
     * @param base64 the Base64 encoded string of the image.
     * @param metadata the metadata associated with the image.
     * @param collectionPath the Firestore collection path where the image will be stored.
     */
    fun imageToFirestore(base64: String, metadata: Map<String, Any>, collectionPath: String)
}