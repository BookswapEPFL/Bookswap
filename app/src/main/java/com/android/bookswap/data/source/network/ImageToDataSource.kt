package com.android.bookswap.data.source.network

import android.content.Context
import android.widget.Toast
import com.android.bookswap.data.source.api.ApiService
import org.json.JSONObject

class ImageToDataSource(private val apiService: ApiService) {
    // The specific prompt for the image analysis
    private val PROMPT = """
        You are an AI designed to analyze images of book covers. Given an image of a book's back cover in URL format, extract the following information in a structured format (JSON) with the specified fields: title, author, description, language, and ISBN. Please ensure that:

        1. If any of the fields are not present or cannot be confidently identified, indicate them with "N/A".
        2. If the image does not appear to be a book cover, return an error message stating "The image does not appear to be a valid book cover."
        3. If the title, author, description, or other fields are in a different language, keep the original text for reference, and provide the appropriate language code using the following classification:
           - FRENCH ("FR")
           - GERMAN ("DE")
           - ENGLISH ("EN")
           - SPANISH ("ES")
           - ITALIAN ("IT")
           - ROMANSH ("RM")
           - OTHER ("OTHER") // All languages that are not yet implemented
        4. The description should be the one written on the books.
        5. The ISBN is often over a barcode, in the bottom left or right corner.

        Example output format:
        {
          "title": "Example Title",
          "author": "Example Author",
          "description": "An example description of the book.",
          "language": "EN",
          "isbn": "1234567890"
        }
        
        The image URL:
    """.trimIndent()

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
            onError = { error ->
                onError("API Request Error: $error")
            }
        )
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
            "isbn" to isbn
        )
    }
}