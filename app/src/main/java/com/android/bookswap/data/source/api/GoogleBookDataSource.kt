package com.android.bookswap.data.source.api

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.utils.getJSONArrayOrNull
import com.android.bookswap.utils.getJSONObjectOrNull
import com.android.bookswap.utils.getStringOrNull
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.UUID
import org.json.JSONObject

const val GOOGLE_BOOK_API = "https://www.googleapis.com/books/v1/volumes?q="

/**
 * Source to request data from GoogleBook
 *
 * @param context the context from where the request is made
 */
class GoogleBookDataSource(context: Context) {
  private val queue = Volley.newRequestQueue(context)

  /**
   * Request a book from GoogleBook using an ISBN This function return the data using callback
   *
   * @param isbn a string of 10 or 13 digits representing an ISBN
   * @param callback callback with a Result of a DataBook
   * @throws IllegalArgumentException if isbn is not in a valid format
   * @author EdenKahane
   */
  fun getBookFromISBN(isbn: String, callback: (Result<DataBook>) -> Unit) {
      try {
          require(isbn.all { it.isDigit() }) { "ISBN should only be composed of digits" }
          require(isbn.length == 10 || isbn.length == 13) { "ISBN should be of length 10 or 13" }
      } catch (exception: Exception) {
          callback(Result.failure(exception))
          return
      }

    val stringRequest =
        StringRequest(
            Request.Method.GET,
            GOOGLE_BOOK_API.plus("isbn:${isbn}"),
            { response -> callback(parseISBNResponse(response)) },
            { error -> Result.failure<VolleyError>(error) })
    queue.add(stringRequest)
  }

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun parseISBNResponse(response: String): Result<DataBook> {
    try {
      val json = JSONObject(response)
      // Since we want the result to fail when specific data are not available (like title),
      // we do not always use get..OrNull()
      val item = json.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo")

      val language =
          when (val languageCode = item.getStringOrNull("language")?.uppercase()) {
            is String -> BookLanguages.values().first { it.languageCode == languageCode }
            else -> null
          }

      // We do not know where the ISBN_13 is, so we need to filter for it
      val industryIdentifiers = item.getJSONArray("industryIdentifiers")
      val identifier =
          (0 until industryIdentifiers.length())
              .map { industryIdentifiers.getJSONObject(it) }
              .first { it.getString("type") == "ISBN_13" }
              .getString("identifier")

      return Result.success(
          DataBook(
              UUID.randomUUID(),
              item.getString("title"),
              item.getJSONArrayOrNull("authors")?.getStringOrNull(0),
              item.getStringOrNull("description"),
              null,
              item.getJSONObjectOrNull("imageLinks")?.getStringOrNull("thumbnail"),
              language ?: BookLanguages.OTHER,
              identifier))
    } catch (exception: Exception) {
      return Result.failure(exception)
    }
  }
}
