package com.android.bookswap.utils

import org.json.JSONArray
import org.json.JSONObject

/**
 * Retrieves a JSONObject associated with the specified name, or null if not found.
 *
 * @param name The name of the JSONObject to retrieve.
 * @return The JSONObject associated with the specified name, or null if not found.
 */
fun JSONObject.getJSONObjectOrNull(name: String): JSONObject? {
  return if (this.has(name)) this.getJSONObject(name) else null
}
/**
 * Retrieves a JSONArray associated with the specified name, or null if not found.
 *
 * @param name The name of the JSONArray to retrieve.
 * @return The JSONArray associated with the specified name, or null if not found.
 */
fun JSONObject.getJSONArrayOrNull(name: String): JSONArray? {
  return if (this.has(name)) this.getJSONArray(name) else null
}
/**
 * Retrieves a String associated with the specified name, or null if not found.
 *
 * @param name The name of the String to retrieve.
 * @return The String associated with the specified name, or null if not found.
 */
fun JSONObject.getStringOrNull(name: String): String? {
  return if (this.has(name)) this.getString(name) else null
}
/**
 * Retrieves a String at the specified index, or null if not found.
 *
 * @param index The index of the String to retrieve.
 * @return The String at the specified index, or null if not found.
 */
fun JSONArray.getStringOrNull(index: Int): String? {
  return if (!this.isNull(index)) this.getString(index) else null
}
