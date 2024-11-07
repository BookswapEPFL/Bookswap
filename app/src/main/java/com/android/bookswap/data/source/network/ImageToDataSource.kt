package com.android.bookswap.data.source.network

import com.android.bookswap.R
import com.android.bookswap.data.source.api.ApiService
import org.json.JSONObject

/**
 * Class to send an image URL to ChatGPT API and parse the response for book information.
 *
 * @param apiService The API service to send the request
 */
class ImageToDataSource(private val apiService: ApiService) {
  // The specific prompt for the image analysis
  private val PROMPT = R.string.prompt

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
    val jsonObject = JSONObject(response)

    // Extract the fields as specified
    val title = jsonObject.optString("title", "N/A")
    val author = jsonObject.optString("author", "N/A")
    val description = jsonObject.optString("description", "N/A")
    val language = jsonObject.optString("language", "N/A")
    val isbn = jsonObject.optString("isbn", "N/A")

    return mapOf(
        "title" to title,
        "author" to author,
        "description" to description,
        "language" to language,
        "isbn" to isbn)
  }
}
