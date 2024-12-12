package com.android.bookswap.utils

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
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

/**
 * Create a JsonObjectRequest for the OpenAI ChatGPT API.
 *
 * @param method The HTTP method to use.
 * @param url The URL to send the request to.
 * @param body The JSON body of the request.
 * @param apiKey The API key to use for authorization.
 * @param onSuccess The success callback function.
 * @param onError The error callback function.
 * @return The JsonObjectRequest object.
 */
fun createJsonObjectRequestOpenAI(
    method: Int,
    url: String,
    body: JSONObject,
    apiKey: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
): JsonObjectRequest {
  return object :
      JsonObjectRequest(
          method,
          url,
          body,
          Response.Listener { response ->
            try {
              val choicesArray = response.getJSONArray("choices")
              val messageContent =
                  choicesArray.getJSONObject(0).getJSONObject("message").getString("content")

              onSuccess(messageContent)
            } catch (e: Exception) {
              Log.e("ChatGPTApiService", "Error: ${e.localizedMessage}")
              onError("Parsing error: ${e.localizedMessage}")
            }
          },
          Response.ErrorListener { error ->
            Log.e("ChatGPTApiService", "Full error details: ", error)

            if (error.networkResponse != null) {
              val statusCode = error.networkResponse.statusCode
              val responseBody =
                  error.networkResponse.data?.let { String(it) } ?: "No response body"
              Log.e("ChatGPTApiService", "Status Code: $statusCode, Response Body: $responseBody")
            }

            val errorMessage = error.localizedMessage ?: "Unknown error"
            Log.e("ChatGPTApiService", "Error message: $errorMessage")
            onError("Request error: $errorMessage")
          }) {
    /**
     * Override the default headers for the request.
     *
     * @return A mutable map containing the headers for the request.
     */
    override fun getHeaders(): MutableMap<String, String> {
      return hashMapOf("Authorization" to "Bearer $apiKey", "Content-Type" to "application/json")
    }
    /**
     * Override the default retry policy for the request.
     *
     * @return The retry policy to use for the request.
     */
    override fun getRetryPolicy(): com.android.volley.RetryPolicy {
      return com.android.volley.DefaultRetryPolicy(
          30000, // Timeout in milliseconds (e.g., 30 seconds)
          com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
          com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }
  }
}
