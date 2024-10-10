package com.android.bookswap.utils

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getJSONObjectOrNull(name: String): JSONObject? {
  return if (this.has(name)) this.getJSONObject(name) else null
}

fun JSONObject.getJSONArrayOrNull(name: String): JSONArray? {
  return if (this.has(name)) this.getJSONArray(name) else null
}

fun JSONObject.getStringOrNull(name: String): String? {
  return if (this.has(name)) this.getString(name) else null
}

fun JSONArray.getStringOrNull(index: Int): String? {
  return if (!this.isNull(index)) this.getString(index) else null
}
