package com.android.bookswap.data

import android.net.Uri

data class DataPhoto(
    val id: String,
    val url: String,
    val timestamp: Long,
    val userId: String,
    val bookId: String,
    val uri: Uri? = null,
)
