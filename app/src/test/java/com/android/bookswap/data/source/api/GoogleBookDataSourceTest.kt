package com.android.bookswap.data.source.api

import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import java.util.UUID
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Captor
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.capture
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleBookDataSourceTest {

  private lateinit var mockitoClosable: AutoCloseable
  @Captor private lateinit var resultDataBookCaptor: ArgumentCaptor<Result<DataBook>>

  @Before
  fun init() {
    mockitoClosable = MockitoAnnotations.openMocks(this)
  }

  @After
  fun close() {
    mockitoClosable.close()
  }

  @Test
  fun `ISBN input validation`() {
    val userId = UUID.randomUUID()
    val mockGoogleBookDataSource: GoogleBookDataSource = mock()

    val callback: (Result<DataBook>) -> Unit = mock()
    `when`(
            mockGoogleBookDataSource.getBookFromISBN(
                anyString(), eq(userId), ArgumentMatchers.any(callback::class.java) ?: callback))
        .thenCallRealMethod()

    mockGoogleBookDataSource.getBookFromISBN("01a3456789", userId, callback)
    mockGoogleBookDataSource.getBookFromISBN("01234567890", userId, callback)
    verify(callback, times(2)).invoke(capture(resultDataBookCaptor))

    assert(resultDataBookCaptor.value.isFailure)
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
            isbn = "9780435123437",
            userId = UUID.randomUUID()
            )

    val mockGoogleBookDataSource: GoogleBookDataSource = mock()
    `when`(mockGoogleBookDataSource.parseISBNResponse(jsonBook, dataBook.userId)).thenCallRealMethod()

    assertBookEquals(dataBook, mockGoogleBookDataSource.parseISBNResponse(jsonBook, dataBook.userId).getOrNull())
  }

  @Test
  fun `parseISBNResponse fail when json is wrong`() {
    val userID = UUID.randomUUID()
    val brokenJSON = "BROKEN JSON"

    val mockGoogleBookDataSource: GoogleBookDataSource = mock()
    `when`(mockGoogleBookDataSource.parseISBNResponse(brokenJSON, userID)).thenCallRealMethod()

    assertTrue(mockGoogleBookDataSource.parseISBNResponse(brokenJSON, userID).isFailure)
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

    val userId = UUID.randomUUID()
    val mockGoogleBookDataSource: GoogleBookDataSource = mock()
    `when`(mockGoogleBookDataSource.parseISBNResponse(missingTitleJson, userId)).thenCallRealMethod()

    assertTrue(mockGoogleBookDataSource.parseISBNResponse(missingTitleJson, userId).isFailure)
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
            isbn = "9780435123437",
            userId = UUID.randomUUID()
            )

    val mockGoogleBookDataSource: GoogleBookDataSource = mock()
    `when`(mockGoogleBookDataSource.parseISBNResponse(anyString(), eq(dataBook.userId))).thenCallRealMethod()

    assertBookEquals(dataBook, mockGoogleBookDataSource.parseISBNResponse(fieldsEmpty, dataBook.userId).getOrNull())
    assertBookEquals(dataBook, mockGoogleBookDataSource.parseISBNResponse(listEmpty, dataBook.userId).getOrNull())
  }

  /**
   * Assert that two books are identical except for their UUID
   *
   * @param expected the expected result
   * @param result the result with it's UUID modified to match the UUID of expected
   */
  private fun assertBookEquals(expected: DataBook, result: DataBook?) {
    assertEquals(expected, result?.copy(uuid = expected.uuid))
  }
}
