package com.android.bookswap.data.source.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class ChatGptApiCallTest {
  private lateinit var mockContext: Context
  private lateinit var mockRequestQueue: RequestQueue
  private lateinit var chatGPTApiService: ChatGPTApiService
  private lateinit var request: Request<*>

  @Before
  fun setUp() {

    mockContext = mockk()

    mockRequestQueue = mockk()
    request = mockk()

    mockkStatic(Volley::class)
    every { Volley.newRequestQueue(any()) } returns mockRequestQueue

    chatGPTApiService = ChatGPTApiService(mockContext, mode = false)
  }

  @Test
  fun `test sendChatRequest adds request to requestQueue`() {

    val userMessages = listOf("Hello")
    val onSuccess: (String) -> Unit = mockk(relaxed = true)
    val onError: (String) -> Unit = mockk(relaxed = true)
    var check = 0
    every { mockRequestQueue.add(any<Request<*>>()) } answers
        {
          check++
          return@answers request
        }

    chatGPTApiService.sendChatRequest(userMessages, onSuccess, onError)

    assertEquals(1, check)
  }
}
