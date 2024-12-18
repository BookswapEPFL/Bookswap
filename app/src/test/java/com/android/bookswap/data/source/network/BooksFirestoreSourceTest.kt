package com.android.bookswap.data.source.network

import com.android.bookswap.data.BookGenres
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.android.bookswap.utils.assertBookEquals
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BooksFirestoreSourceTest {

  private val mockFirestore: FirebaseFirestore = mockk()
  private val mockCollectionReference: CollectionReference = mockk()
  private val mockDocumentReference: DocumentReference = mockk()
  private val mockDocumentSnapshot: DocumentSnapshot = mockk()
  private val mockQuerySnapshot: QuerySnapshot = mockk()

  private val testBook =
      DataBook(
          uuid = UUID.randomUUID(),
          title = "Test Book",
          author = "Test Author",
          description = "Test Description",
          rating = 5,
          photo = "http://example.com/photo.jpg",
          language = BookLanguages.ENGLISH,
          isbn = "1234567890",
          userId = UUID.randomUUID(),
          archived = false,
          exchange = false)

  @Before
  fun setUp() {
    every { mockFirestore.collection(any()) }.returns(mockCollectionReference)
    every { mockCollectionReference.document(any()) }.returns(mockDocumentReference)
    every { mockCollectionReference.get() }.returns(Tasks.forResult(mockQuerySnapshot))

    // Mock of snapshot
    every { mockDocumentSnapshot.getString("title") }.returns(testBook.title)
    every { mockDocumentSnapshot.getString("author") }.returns(testBook.author)
    every { mockDocumentSnapshot.getString("description") }.returns(testBook.description)
    every { mockDocumentSnapshot.get("rating") }.returns(testBook.rating?.toLong())
    every { mockDocumentSnapshot.getString("photo") }.returns(testBook.photo)
    every { mockDocumentSnapshot.getString("language") }.returns(testBook.language.name)
    every { mockDocumentSnapshot.getString("isbn") }.returns(testBook.isbn)
    every { mockDocumentSnapshot.get("genres") }.returns(emptyList<String>())
    every { mockDocumentSnapshot.get("uuid") }.returns(testBook.uuid.toString())
    every { mockDocumentSnapshot.get("userid") }.returns(testBook.userId.toString())

    every { mockDocumentReference.set(any<Map<String, Any>>()) }.returns(Tasks.forResult(null))
    every { mockDocumentReference.delete() }.returns(Tasks.forResult(null))
    every { mockDocumentReference.get() }.returns(Tasks.forResult(mockDocumentSnapshot))
  }

  @Test
  fun `book get returns correct result`() {
    val bookSource = BooksFirestoreSource(mockFirestore)
    bookSource.getBook(
        callback = { result ->
          assertTrue(result.isSuccess)
          val books = result.getOrThrow()
          // Assert that the fetched books match the expected values
          assertTrue(books.isNotEmpty())
          assertBookEquals(books.first(), testBook, true)
        })

    // Verify that Firestore collection was called
    verify { mockCollectionReference.get() }
  }

  @Test
  fun `delete call firestore delete`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Arrange
    every { mockDocumentReference.delete() }.returns(Tasks.forResult(null))

    // Act
    bookSource.deleteBook(testBook.uuid) {}

    // Assert
    verify { mockDocumentReference.delete() }
  }

  @Test
  fun `book set is success`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Arrange
    every { mockDocumentReference.set(testBook) }.returns(Tasks.forResult(null))

    // Act
    bookSource.addBook(testBook) { result -> assertTrue(result.isSuccess) }

    // Verify Firestore set operation
    verify { mockDocumentReference.set(any<Map<String, Any>>()) }
    // verify { mockDocumentReference.set(testBook) }
  }

  @Test
  fun `book update success`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Arrange
    val testBook =
        DataBook(
            uuid = UUID.fromString("f9fb0016-1714-46bb-823b-24dd32e09e19"),
            title = "Test Book",
            author = "Test Author",
            description = "Test Description",
            rating = 5,
            photo = "http://example.com/photo.jpg",
            language = BookLanguages.ENGLISH,
            isbn = "1234567890",
            genres = emptyList(),
            userId = UUID.fromString("142e9f53-697c-4bfd-b4cf-af6a0761d67a"),
            archived = false,
            exchange = false)

    val bookDocument = bookSource.bookToDocument(testBook)

    every { mockDocumentReference.set(bookDocument) } returns Tasks.forResult(null)

    // Act
    bookSource.updateBook(testBook) { result -> assertTrue(result.isSuccess) }

    // Verify Firestore update operation
    verify { mockDocumentReference.set(bookDocument) }
  }

  @Test
  fun `documentToBooks is valid`() {
    // Arrange
    val mockDocumentSnapshot = mockk<DocumentSnapshot>()

    val testBook =
        DataBook(
            uuid = UUID.randomUUID(),
            title = "Test Book",
            author = "Test Author",
            description = "Test Description",
            rating = 5,
            photo = "test_photo_url",
            language = BookLanguages.ENGLISH,
            isbn = "1234567890",
            genres = listOf(BookGenres.FICTION, BookGenres.FANTASY),
            userId = UUID.randomUUID(),
            archived = false,
            exchange = true)

    // Mocking the fields
    every { mockDocumentSnapshot.get("uuid") } returns testBook.uuid.toString()
    every { mockDocumentSnapshot.getString("title") } returns testBook.title
    every { mockDocumentSnapshot.getString("author") } returns testBook.author
    every { mockDocumentSnapshot.getString("description") } returns testBook.description
    every { mockDocumentSnapshot.getLong("rating") } returns testBook.rating?.toLong()
    every { mockDocumentSnapshot.getString("photo") } returns testBook.photo
    every { mockDocumentSnapshot.get("language") } returns testBook.language.name
    every { mockDocumentSnapshot.getString("isbn") } returns testBook.isbn
    every { mockDocumentSnapshot.get("genres") } returns testBook.genres.map { it.name }
    every { mockDocumentSnapshot.get("userId") } returns testBook.userId.toString()
    every { mockDocumentSnapshot.getBoolean("archived") } returns testBook.archived
    every { mockDocumentSnapshot.getBoolean("exchange") } returns testBook.exchange

    val bookSource = BooksFirestoreSource(mockk())

    // Act
    val result = bookSource.documentToBooks(mockDocumentSnapshot)

    // Assert
    assertNotNull("Expected non-null result from documentToBooks", result)
    assertEquals("Book UUID mismatch", testBook.uuid, result?.uuid)
    assertEquals("Book title mismatch", testBook.title, result?.title)
    assertEquals("Book author mismatch", testBook.author, result?.author)
    assertEquals("Book description mismatch", testBook.description, result?.description)
    assertEquals("Book rating mismatch", testBook.rating, result?.rating)
    assertEquals("Book photo mismatch", testBook.photo, result?.photo)
    assertEquals("Book language mismatch", testBook.language, result?.language)
    assertEquals("Book ISBN mismatch", testBook.isbn, result?.isbn)
    assertEquals("Book genres mismatch", testBook.genres, result?.genres)
    assertEquals("Book userId mismatch", testBook.userId, result?.userId)
    assertEquals("Book archived mismatch", testBook.archived, result?.archived)
    assertEquals("Book exchange mismatch", testBook.exchange, result?.exchange)
  }

  @Test
  fun `documentToBooks null when missing value`() {
    // Arrange - Missing "Title"
    val bookSource = BooksFirestoreSource(mockFirestore)

    every { mockDocumentSnapshot.getString("title") }.returns(null)

    // Act
    val result = bookSource.documentToBooks(mockDocumentSnapshot)

    // Assert
    assertNull(result)
  }

  @Test
  fun `documentToBooks null when invalid value`() {
    // Arrange - Invalid language value
    val bookSource = BooksFirestoreSource(mockFirestore)

    every { mockDocumentSnapshot.getString("language") }.returns("INVALID_LANGUAGE")

    // Act
    val result = bookSource.documentToBooks(mockDocumentSnapshot)

    // Assert
    assertNull(result) // Should return null due to invalid language
  }

  @Test
  fun `getFromArchivedBooks retrieves book from archive`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Act
    bookSource.getFromArchivedBooks(
        testBook.uuid,
        { retrievedBook -> assertEquals(testBook.title, retrievedBook.title) },
        { exception -> fail("Failed to retrieve book: ${exception.message}") })

    // Assert
    verify { mockDocumentReference.get() }
  }

  @Test
  fun `getFromArchivedBooks handles missing document`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Arrange
    val uuid = UUID.randomUUID()
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.exists() } returns false

    // Act
    bookSource.getFromArchivedBooks(
        uuid,
        { fail("Expected failure due to missing document") },
        { exception ->
          assertTrue(exception is IllegalArgumentException)
          assertEquals("Book not found", exception.message)
        })

    // Assert
    verify { mockDocumentReference.get() }
  }

  @Test
  fun `getFromArchivedBooks handles Firestore failure`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Arrange
    val uuid = UUID.randomUUID()
    every { mockDocumentReference.get() } returns Tasks.forException(Exception("Firestore error"))

    // Act
    bookSource.getFromArchivedBooks(
        uuid,
        { fail("Expected failure due to Firestore error") },
        { exception ->
          assertTrue(exception is Exception)
          assertEquals("Firestore error", exception.message)
        })

    // Assert
    verify { mockDocumentReference.get() }
  }

  @Test
  fun `getFromArchivedBooks handles invalid genre`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Arrange
    val uuid = UUID.randomUUID()
    val mockDocumentSnapshot = mockk<DocumentSnapshot>()
    every { mockDocumentSnapshot.exists() } returns true
    every { mockDocumentSnapshot.getString("uuid") } returns uuid.toString()
    every { mockDocumentSnapshot.getString("title") } returns "Test Book"
    every { mockDocumentSnapshot.getString("author") } returns "Test Author"
    every { mockDocumentSnapshot.getString("description") } returns "Test Description"
    every { mockDocumentSnapshot.getLong("rating") } returns 5L
    every { mockDocumentSnapshot.getString("photo") } returns "http://example.com/photo.jpg"
    every { mockDocumentSnapshot.getString("language") } returns BookLanguages.ENGLISH.name
    every { mockDocumentSnapshot.getString("isbn") } returns "1234567890"
    every { mockDocumentSnapshot.get("genres") } returns listOf("INVALID_GENRE")
    every { mockDocumentSnapshot.getString("userId") } returns UUID.randomUUID().toString()
    every { mockDocumentSnapshot.getBoolean("archived") } returns false
    every { mockDocumentSnapshot.getBoolean("exchange") } returns false
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)

    // Act
    bookSource.getFromArchivedBooks(
        uuid,
        { retrievedBook ->
          assertNotNull(retrievedBook)
          assertTrue(retrievedBook.genres.isEmpty()) // Invalid genre should be skipped
        },
        { exception -> fail("Failed to retrieve book: ${exception.message}") })

    // Assert
    verify { mockDocumentReference.get() }
  }

  @Test
  fun `deleteFromArchivedBooks deletes book from archive`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Act
    bookSource.deleteFromArchivedBooks(testBook) { result -> assertTrue(result.isSuccess) }

    // Assert
    verify { mockDocumentReference.delete() }
  }

  @Test
  fun `TakeBackFromArchives takes book back from archive`() {
    val bookSource = BooksFirestoreSource(mockFirestore)

    // Arrange
    val userUUID = UUID.randomUUID()
    every { mockDocumentReference.get() }.returns(Tasks.forResult(mockDocumentSnapshot))

    // Act
    bookSource.TakeBackFromArchives(testBook.uuid, userUUID) { result ->
      assertTrue(result.isSuccess)
    }

    // Assert
    verify { mockDocumentReference.get() }
    verify { mockDocumentReference.delete() }
    verify { mockDocumentReference.set(any<Map<String, Any>>()) }
  }
}
