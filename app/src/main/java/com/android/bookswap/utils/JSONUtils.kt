package com.android.bookswap.utils

import org.json.JSONArray
import org.json.JSONObject

/**
 * Extension function to get a JSONObject by name or return null if it doesn't exist.
 *
 * @param name The name of the JSONObject to retrieve.
 * @return The JSONObject if it exists, or null if it doesn't.
 */
fun JSONObject.getJSONObjectOrNull(name: String): JSONObject? {
  return if (this.has(name)) this.getJSONObject(name) else null
}
/**
 * Extension function to get a JSONArray by name or return null if it doesn't exist.
 *
 * @param name The name of the JSONArray to retrieve.
 * @return The JSONArray if it exists, or null if it doesn't.
 */
fun JSONObject.getJSONArrayOrNull(name: String): JSONArray? {
  return if (this.has(name)) this.getJSONArray(name) else null
}
/**
 * Extension function to get a String by name or return null if it doesn't exist.
 *
 * @param name The name of the String to retrieve.
 * @return The String if it exists, or null if it doesn't.
 */
fun JSONObject.getStringOrNull(name: String): String? {
  return if (this.has(name)) this.getString(name) else null
}
/**
 * Extension function to get a String by index or return null if it doesn't exist.
 *
 * @param index The index of the String to retrieve.
 * @return The String if it exists, or null if it doesn't.
 */
fun JSONArray.getStringOrNull(index: Int): String? {
  return if (!this.isNull(index)) this.getString(index) else null
}
