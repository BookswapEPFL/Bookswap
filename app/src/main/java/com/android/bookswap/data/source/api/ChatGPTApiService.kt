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
    val messageList = userMessages.map { m -> ChatGPTMessage(role = "user", content = m) }
    val chatGPTRequest = ChatGPTRequest(model = "gpt-4o-mini", messages = messageList)

    // Create the JSON body for the request
    val jsonBody =
        JSONObject().apply {
          put("model", chatGPTRequest.model)
          put(
              "messages",
              JSONArray().apply {
                chatGPTRequest.messages.forEach { message ->
                  put(
                      JSONObject().apply {
                        put("role", message.role)
                        put("content", message.content)
                      })
                }
              })
        }

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
                  Log.e("ChatGPTApiService", "Error: ${error.localizedMessage}")
                  onError("Request error: ${error.localizedMessage}")
                }) {
          // Add the API key and content type to the request headers
          override fun getHeaders(): MutableMap<String, String> {
            return hashMapOf(
                "Authorization" to "Bearer $apiKey", "Content-Type" to "application/json")
          }
        }
    requestQueue.add(jsonObjectRequest)
  }
}
