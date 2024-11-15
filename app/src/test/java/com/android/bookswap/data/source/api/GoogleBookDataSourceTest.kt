package com.android.bookswap.data.source.api

import android.content.Context
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.utils.assertBookEquals
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleBookDataSourceTest {
  private lateinit var mockGoogleBookDataSource: GoogleBookDataSource

  @Before
  fun setup() {
    val mockContext: Context = mockk()
    val mockQueue: RequestQueue = mockk()
    mockkStatic(Volley::class)
    every { Volley.newRequestQueue(any()) } returns mockQueue

    mockGoogleBookDataSource = spyk(GoogleBookDataSource(mockContext))
  }

  @Test
  fun `ISBN input validation`() {

    val callback: (Result<DataBook>) -> Unit = mockk()
    every { callback(any()) } answers { assertTrue(firstArg<Result<DataBook>>().isFailure) }

    mockGoogleBookDataSource.getBookFromISBN("01a3456789", callback)
    mockGoogleBookDataSource.getBookFromISBN("01234567890", callback)

    verify(exactly = 2) { callback(any()) }
  }

  @Test
  fun `parseISBNResponse correctly parse`() {
    val jsonBook =
        """
      {
        "kind": "books#volumes",
        "totalItems": 1,
        "items": [
          {
            "id": "JLunPwAACAAJ",
            "volumeInfo": {
              "title": "Flowers for Algernon",
              "authors": [
                "Daniel Keyes"
              ],
              "description": "Example desc",
              "industryIdentifiers": [
                {
                  "type": "ISBN_10",
                  "identifier": "0435123432"
                },
                {
                  "type": "ISBN_13",
                  "identifier": "9780435123437"
                }
              ],
              "imageLinks": {
                "smallThumbnail": "image1",
                "thumbnail": "image2"
              },
              "language": "en"
            }
          }
        ]
      }
    """
            .trimIndent()
    val dataBook =
        DataBook(
            uuid = UUID.randomUUID(),
            title = "Flowers for Algernon",
            author = "Daniel Keyes",
            description = "Example desc",
            rating = null,
            photo = "image2",
            language = BookLanguages.ENGLISH,
            isbn = "9780435123437")

    assertBookEquals(dataBook, mockGoogleBookDataSource.parseISBNResponse(jsonBook).getOrNull())
  }

  @Test
  fun `parseISBNResponse fail when json is wrong`() {
    val brokenJSON = "BROKEN JSON"

    assertTrue(mockGoogleBookDataSource.parseISBNResponse(brokenJSON).isFailure)
  }

  @Test
  fun `parseISBNResponse fail when title is not available`() {
    val missingTitleJson =
        """
      {
        "kind": "books#volumes",
        "totalItems": 1,
        "items": [
          {
            "id": "JLunPwAACAAJ",
            "volumeInfo": {
              "authors": [
                "Daniel Keyes"
              ],
              "description": "Example desc",
              "industryIdentifiers": [
                {
                  "type": "ISBN_10",
                  "identifier": "0435123432"
                },
                {
                  "type": "ISBN_13",
                  "identifier": "9780435123437"
                }
              ],
              "imageLinks": {
                "smallThumbnail": "image1",
                "thumbnail": "image2"
              },
              "language": "en"
            }
          }
        ]
      }
    """
            .trimIndent()

    assertTrue(mockGoogleBookDataSource.parseISBNResponse(missingTitleJson).isFailure)
  }

  @Test
  fun `parseISBNResponse valid when partially empty`() {
    val fieldsEmpty =
        """
      {
        "kind": "books#volumes",
        "totalItems": 1,
        "items": [
          {
            "id": "JLunPwAACAAJ",
            "volumeInfo": {
              "title": "Flowers for Algernon",
              "industryIdentifiers": [
                {
                  "type": "ISBN_10",
                  "identifier": "0435123432"
                },
                {
                  "type": "ISBN_13",
                  "identifier": "9780435123437"
                }
              ]
            }
          }
        ]
      }
    """
            .trimIndent()
    val listEmpty =
        """
      {
        "kind": "books#volumes",
        "totalItems": 1,
        "items": [
          {
            "id": "JLunPwAACAAJ",
            "volumeInfo": {
              "title": "Flowers for Algernon",
              "authors": [],
              "industryIdentifiers": [
                {
                  "type": "ISBN_10",
                  "identifier": "0435123432"
                },
                {
                  "type": "ISBN_13",
                  "identifier": "9780435123437"
                }
              ],
              "imageLinks": {}
            }
          }
        ]
      }
    """
            .trimIndent()
    val dataBook =
        DataBook(
            uuid = UUID.randomUUID(),
            title = "Flowers for Algernon",
            author = null,
            description = null,
            rating = null,
            photo = null,
            language = BookLanguages.OTHER,
            isbn = "9780435123437")

    assertBookEquals(dataBook, mockGoogleBookDataSource.parseISBNResponse(fieldsEmpty).getOrNull())
    assertBookEquals(dataBook, mockGoogleBookDataSource.parseISBNResponse(listEmpty).getOrNull())
  }
}
