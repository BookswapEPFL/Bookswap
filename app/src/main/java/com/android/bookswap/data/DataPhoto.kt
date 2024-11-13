package com.android.bookswap.data

import java.util.UUID

data class DataPhoto(
    val uuid: UUID, // UUID,
    val url: String,
    val timestamp: Long,
    val base64: String,
)
