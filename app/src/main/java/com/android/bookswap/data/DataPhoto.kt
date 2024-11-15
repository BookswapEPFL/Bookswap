package com.android.bookswap.data

import java.util.UUID

/**
 * Represents a photo with various properties.
 *
 * @param uuid Unique identifier for the photo
 * @param url URL of the photo
 * @param timestamp Timestamp of when the photo was taken
 * @param base64 Base64 encoded string of the photo
 */
data class DataPhoto(
    val uuid: UUID, // UUID,
    val url: String,
    val timestamp: Long,
    val base64: String,
)
