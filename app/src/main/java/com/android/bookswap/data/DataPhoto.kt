package com.android.bookswap.data

import android.net.Uri
import java.util.UUID

data class DataPhoto(
    val uid: String, //UUID,
    val url: String,
    val timestamp: Long,
    val base64: String,
  )