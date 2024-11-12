package com.android.bookswap.data.source.api
/*
  This interface is used to send a chat request to the API
*/
interface ApiService {
  /**
   * Send a chat request to the API
   *
   * @param userMessages List of user messages to send
   * @param onSuccess Callback for a successful response
   * @param onError Callback for handling errors
   */
  fun sendChatRequest(
      userMessages: List<String>,
      onSuccess: (String) -> Unit,
      onError: (String) -> Unit
  )
}
