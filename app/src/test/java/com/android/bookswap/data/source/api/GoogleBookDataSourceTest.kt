package com.android.bookswap.data.source.api

import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleBookDataSourceTest {
  @Test
  fun `ISBN input validation`() {
    val mockGoogleBookDataSource = mock(GoogleBookDataSource::class.java)

    val callback: (Result<DataBook>) -> Unit = {}
    `when`(
            mockGoogleBookDataSource.getBookFromISBN(
                anyString(), ArgumentMatchers.any(callback::class.java) ?: callback))
        .thenCallRealMethod()

    assertThrows(IllegalArgumentException::class.java) {
      mockGoogleBookDataSource.getBookFromISBN("01a3456789", callback)
    }

    assertThrows(IllegalArgumentException::class.java) {
      mockGoogleBookDataSource.getBookFromISBN("01234567890", callback)
    }
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
            rating = 0,
            photo = "image2",
            language = BookLanguages.ENGLISH,
            isbn = "0435123432")

    val mockGoogleBookDataSource = mock(GoogleBookDataSource::class.java)
    `when`(mockGoogleBookDataSource.parseISBNResponse(jsonBook)).thenCallRealMethod()

    assertDataBookEqual(dataBook, mockGoogleBookDataSource.parseISBNResponse(jsonBook).getOrNull())
  }

  @Test
  fun `parseISBNResponse fail when json is wrong`() {
    val brokenJSON = "BROKEN JSON"

    val mockGoogleBookDataSource = mock(GoogleBookDataSource::class.java)
    `when`(mockGoogleBookDataSource.parseISBNResponse(brokenJSON)).thenCallRealMethod()

    assertTrue(mockGoogleBookDataSource.parseISBNResponse(brokenJSON).isFailure)
  }

  @Test
  fun `parseISBNResponse fail when title is not available`() {
    val missingTitleJson =
        """
      {
        "kind": assertEquals"books#volumes",
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

    val mockGoogleBookDataSource = mock(GoogleBookDataSource::class.java)
    `when`(mockGoogleBookDataSource.parseISBNResponse(missingTitleJson)).thenCallRealMethod()

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
            author = "unknown",
            description = "unknown",
            rating = 0,
            photo = "",
            language = BookLanguages.OTHER,
            isbn = "0435123432")

    val mockGoogleBookDataSource = mock(GoogleBookDataSource::class.java)
    `when`(mockGoogleBookDataSource.parseISBNResponse(anyString())).thenCallRealMethod()

    assertDataBookEqual(
        dataBook, mockGoogleBookDataSource.parseISBNResponse(fieldsEmpty).getOrNull())
    assertDataBookEqual(dataBook, mockGoogleBookDataSource.parseISBNResponse(listEmpty).getOrNull())
  }

  /**
   * Assert that two books are equal without comparing the uuid.
   *
   * @param expected the expected book
   * @param result the received book, with it's uuid replaced to be the same as expected uuid
   */
  private fun assertDataBookEqual(expected: DataBook, result: DataBook?) {
    assertEquals(expected, result?.copy(uuid = expected.uuid))
  }
}
