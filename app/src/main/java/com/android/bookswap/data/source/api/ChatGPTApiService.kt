package com.android.bookswap.data.source.api

import android.content.Context
import android.util.Log
import com.android.bookswap.BuildConfig
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

data class ChatGPTMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

data class ChatGPTRequest(val model: String, val messages: List<ChatGPTMessage>)

/**
 * Service to interact with the OpenAI ChatGPT API.
 *
 * @param context the context from where the request is made
 * @param mode true for production mode, false for testing mode
 */
class ChatGPTApiService(context: Context, mode: Boolean = true) : ApiService {
  private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
  private val apiKey = if (mode) BuildConfig.OPENAI_API_KEY else ""
  private val apiUrl = "https://api.openai.com/v1/chat/completions"

  // Send a chat request to the OpenAI API
  override fun sendChatRequest(
      userMessages: List<String>,
      onSuccess: (String) -> Unit,
      onError: (String) -> Unit
  ) {
    val messageList =
        userMessages.map { m ->
          if (m.contains("The image URL: ")) {
            val parts = m.split("The image URL: ")
            val textContent = parts[0].trim()
            val imageUrl = parts.getOrNull(1)?.trim() ?: ""
            ChatGPTMessage(
                role = "user",
                content =
                    JSONArray()
                        .apply {
                          put(
                              JSONObject().apply {
                                put("type", "text")
                                put("text", textContent)
                              })
                          put(
                              JSONObject().apply {
                                put("type", "image_url")
                                put("image_url", JSONObject().apply { put("url", imageUrl) })
                              })
                        }
                        .toString() // Convert JSONArray to String
                )
          } else {
            ChatGPTMessage(
                role = "user",
                content =
                    JSONArray()
                        .apply {
                          put(
                              JSONObject().apply {
                                put("type", "text")
                                put("text", m)
                              })
                        }
                        .toString() // Convert JSONArray to String
                )
          }
        }

    val chatGPTRequest = ChatGPTRequest(model = "gpt-4o-mini", messages = messageList)

    // Create the JSON body for the request
    val jsonBody =
        JSONObject().apply {
          put("model", chatGPTRequest.model)
          put(
              "messages",
              JSONArray().apply {
                chatGPTRequest.messages.forEach { message ->
                  val content =
                      try {
                        JSONArray(message.content) // Parse as JSONArray
                      } catch (e: Exception) {
                        message.content
                      }
                  put(
                      JSONObject().apply {
                        put("role", message.role)
                        put("content", content)
                      })
                }
              })
        }
    Log.i("ChatGPTApiService", "Request to openAI API with: $jsonBody")

    // Create the request and listen for the response
    val jsonObjectRequest =
        object :
            JsonObjectRequest(
                Method.POST,
                apiUrl,
                jsonBody,
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
                    Log.e(
                        "ChatGPTApiService",
                        "Status Code: $statusCode, Response Body: $responseBody")
                  }

                  val errorMessage = error.localizedMessage ?: "Unknown error"
                  Log.e("ChatGPTApiService", "Error message: $errorMessage")
                  onError("Request error: $errorMessage")
                }) {
          // Add the API key and content type to the request headers
          override fun getHeaders(): MutableMap<String, String> {
            return hashMapOf(
                "Authorization" to "Bearer $apiKey", "Content-Type" to "application/json")
          }
          // Override the default timeout (in milliseconds)
          override fun getRetryPolicy(): com.android.volley.RetryPolicy {
            return com.android.volley.DefaultRetryPolicy(
                30000, // Timeout in milliseconds (e.g., 30 seconds)
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
          }
        }
    requestQueue.add(jsonObjectRequest)
  }
}
