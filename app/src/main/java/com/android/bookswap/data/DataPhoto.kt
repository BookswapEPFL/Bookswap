package com.android.bookswap.data

import android.net.Uri
import java.util.Base64
import java.util.UUID

data class DataPhoto(
    val uuid: UUID, // UUID,
    val url: String,
    val timestamp: Long,
    val base64: String,
)
