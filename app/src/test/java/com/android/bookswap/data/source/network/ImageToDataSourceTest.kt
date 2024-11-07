package com.android.bookswap.data.source.network

import com.android.bookswap.data.source.api.ApiService
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertTrue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import net.bytebuddy.implementation.InvokeDynamic.lambda
import org.junit.Before
import org.junit.Test

class ImageToDataSourceTest {
  private lateinit var apiService: ApiService
  private lateinit var imageToDataSource: ImageToDataSource
  private val imageUrl = "https://example.com/book-cover.jpg"
  private val expectedPrompt =
      """
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

        The image URL: $imageUrl
    """
          .trimIndent()

  @Before
  fun setup() {
    apiService = mockk()
    imageToDataSource = ImageToDataSource(apiService)
  }

  @Test
  fun `analyzeImage should return parsed data on success`() {

    // Mock API response
    val mockResponse =
        """
        {
          "title": "Example Title",
          "author": "Example Author",
          "description": "An example description of the book.",
          "language": "EN",
          "isbn": "1234567890"
        }
    """
            .trimIndent()

    // Mock successful response
    every {
      apiService.sendChatRequest(
          listOf(expectedPrompt), // This should now match the exact prompt
          captureLambda(),
          any())
    } answers { lambda<(String) -> Unit>().invoke(mockResponse) }

    // Test analyzeImage
    imageToDataSource.analyzeImage(
        imageUrl,
        onSuccess = { result ->
          assertEquals("Example Title", result["title"])
          assertEquals("Example Author", result["author"])
          assertEquals("An example description of the book.", result["description"])
          assertEquals("EN", result["language"])
          assertEquals("1234567890", result["isbn"])
        },
        onError = { fail("Expected success callback but got error: $it") })

    // Verify that sendChatRequest was called with the exact prompt
    verify { apiService.sendChatRequest(listOf(expectedPrompt), any(), any()) }
  }

  @Test
  fun `analyzeImage should call onError when API fails`() {
    val errorMessage = "Network error"
    every { apiService.sendChatRequest(any(), any(), captureLambda()) } answers
        {
          lambda<(String) -> Unit>().invoke(errorMessage)
        }

    val imageUrl = "https://example.com/book-cover.jpg"

    // Test analyzeImage
    imageToDataSource.analyzeImage(
        imageUrl,
        onSuccess = { fail("Expected error callback but got success") },
        onError = { error -> assertEquals("API Request Error: $errorMessage", error) })

    // Verify that sendChatRequest was called
    verify { apiService.sendChatRequest(any(), any(), any()) }
  }

  @Test
  fun `analyzeImage should handle partial data in response`() {
    val mockPartialResponse =
        """
        {
          "title": "Partial Title",
          "author": "Partial Author",
          "language": "EN"
        }
    """
            .trimIndent()

    every { apiService.sendChatRequest(listOf(expectedPrompt), captureLambda(), any()) } answers
        {
          lambda<(String) -> Unit>().invoke(mockPartialResponse)
        }

    imageToDataSource.analyzeImage(
        imageUrl,
        onSuccess = { result ->
          assertEquals("Partial Title", result["title"])
          assertEquals("Partial Author", result["author"])
          assertEquals("N/A", result["description"]) // Missing field should default to "N/A"
          assertEquals("EN", result["language"])
          assertEquals("N/A", result["isbn"]) // Missing field should default to "N/A"
        },
        onError = { fail("Expected success callback but got error: $it") })
  }

  @Test
  fun `analyzeImage should handle non-JSON response from API`() {
    val mockNonJsonResponse = "This is not JSON"

    every { apiService.sendChatRequest(listOf(expectedPrompt), captureLambda(), any()) } answers
        {
          lambda<(String) -> Unit>().invoke(mockNonJsonResponse)
        }

    imageToDataSource.analyzeImage(
        imageUrl,
        onSuccess = { fail("Expected error callback but got success with data: $it") },
        onError = { error -> assertTrue(error.contains("Parsing error")) })
  }
}
