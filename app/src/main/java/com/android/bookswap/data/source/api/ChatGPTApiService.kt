package com.android.bookswap.data.source.api

import android.content.Context
import android.util.Log
import com.android.bookswap.BuildConfig
import com.android.bookswap.utils.createJsonObjectRequestOpenAI
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

/**
 * Data class representing a message for ChatGPT.
 *
 * @property role The role of the message sender, either "user" or "assistant".
 * @property content The content of the message.
 */
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
  /**
   * Send a chat request to the OpenAI API.
   *
   * @param userMessages List of user messages to send.
   * @param onSuccess Callback for a successful response.
   * @param onError Callback for handling errors.
   */
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
    val jsonObjectRequestOpenAI =
        createJsonObjectRequestOpenAI(
            Request.Method.POST, apiUrl, jsonBody, apiKey, onSuccess, onError)
    requestQueue.add(jsonObjectRequestOpenAI)
  }
}
