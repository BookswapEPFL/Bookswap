package com.android.bookswap.data

import java.util.UUID

/**
 * Data class representing a photo.
 *
 * @property uuid The unique identifier of the photo.
 * @property url The URL of the photo.
 * @property timestamp The timestamp of when the photo was taken.
 * @property base64 The base64 encoded string of the photo.
 */
data class DataPhoto(
    val uuid: UUID, // UUID,
    val url: String,
    val timestamp: Long,
    val base64: String,
)
