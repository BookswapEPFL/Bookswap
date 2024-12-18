package com.android.bookswap.data.source.network

import android.util.Log
import com.android.bookswap.R
import com.android.bookswap.data.source.api.ApiService
import org.json.JSONException
import org.json.JSONObject

/**
 * Class to send an image URL to ChatGPT API and parse the response for book information.
 *
 * @param apiService The API service to send the request
 */
class ImageToDataSource(private val apiService: ApiService) {
  // The specific prompt for the image analysis

  /**
   * Function to send an image URL to ChatGPT API and parse the response for book information.
   *
   * @param imageUrl URL of the image to analyze
   * @param onSuccess Callback for a successful response, returns structured book data
   * @param onError Callback for handling errors
   */
  fun analyzeImage(
      imageUrl: String,
      onSuccess: (Map<String, String>) -> Unit,
      onError: (String) -> Unit
  ) {
    // Complete the prompt by appending the image URL
    val fullPrompt = "$PROMPT $imageUrl"
    // Send the request to ChatGPTApiService
    apiService.sendChatRequest(
        userMessages = listOf(fullPrompt),
        onSuccess = { responseContent ->
          Log.i("ImageToDataSource", "Response: $responseContent")
          try {
            // Parse the JSON response into a Map
            val parsedData = parseResponse(responseContent)
            onSuccess(parsedData)
          } catch (e: Exception) {
            onError("Parsing error: ${e.localizedMessage}")
          }
        },
        onError = { error -> onError("API Request Error: $error") })
  }

  /**
   * Parses the ChatGPT response JSON string into a map of book information.
   *
   * @param response JSON string returned by the ChatGPT API
   * @return A map with keys: "title", "author", "description", "language", and "isbn"
   */
  private fun parseResponse(response: String): Map<String, String> {
    // Remove any leading/trailing whitespace and potential triple backticks or other formatting
    // characters
    val cleanResponse = response.trim().removePrefix("```json").removeSuffix("```")

    return try {
      val jsonObject = JSONObject(cleanResponse)

      // Extract the fields as specified
      val title = jsonObject.optString("title", UNDEFINED_ATTRIBUTE)
      val author = jsonObject.optString("author", UNDEFINED_ATTRIBUTE)
      val description = jsonObject.optString("description", UNDEFINED_ATTRIBUTE)
      val language = jsonObject.optString("language", UNDEFINED_ATTRIBUTE)
      val isbn = jsonObject.optString("isbn", UNDEFINED_ATTRIBUTE)

      mapOf(
          "title" to title,
          "author" to author,
          "description" to description,
          "language" to language,
          "isbn" to isbn)
    } catch (e: JSONException) {
      Log.e("ImageToDataSource", "JSON Parsing error: ${e.localizedMessage}")
      mapOf(
          "title" to UNDEFINED_ATTRIBUTE,
          "author" to UNDEFINED_ATTRIBUTE,
          "description" to UNDEFINED_ATTRIBUTE,
          "language" to UNDEFINED_ATTRIBUTE,
          "isbn" to UNDEFINED_ATTRIBUTE)
    }
  }

  companion object {
    const val UNDEFINED_ATTRIBUTE = "N/A"
    val PROMPT = R.string.prompt_book_scanning
  }
}
