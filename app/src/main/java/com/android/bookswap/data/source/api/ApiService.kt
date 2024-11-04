package com.android.bookswap.data.source.api
/*
   This interface is used to send a chat request to the API
 */
interface ApiService {
    fun sendChatRequest(
        userMessages: List<String>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    )
}