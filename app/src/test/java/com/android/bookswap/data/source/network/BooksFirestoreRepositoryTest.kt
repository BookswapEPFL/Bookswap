package com.android.bookswap.data.source.network

import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.data.BookLanguages
import com.android.bookswap.data.DataBook
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BookFirestoreSourceTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private lateinit var bookFirestoreSource: BookFirestoreSource

  private val testBook =
      DataBook(
          uuid = UUID.randomUUID(),
          title = "Test Book",
          author = "Test Author",
          description = "Test Description",
          rating = 5,
          photo = "http://example.com/photo.jpg",
          language = BookLanguages.ENGLISH,
          isbn = "1234567890")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    bookFirestoreSource = BookFirestoreSource(mockFirestore)

    `when`(mockFirestore.collection(ArgumentMatchers.any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(ArgumentMatchers.any()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @Test
  fun getbook_callsFirestoreGet() {
    // Arrange
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.title)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.language.name)
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.isbn)

    // Act
    bookFirestoreSource.getBook { result ->
      assertTrue(result.isFailure)
      assertTrue(result.getOrThrow().isNotEmpty())
      assertEquals(testBook.title, result.getOrNull()?.first()?.title)
    }

    // Verify that Firestore collection was called
    verify(mockCollectionReference).get()
  }

  @Test
  fun deletebooks_shouldCallFirestoreDelete() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).delete()

    // Act
    bookFirestoreSource.deleteBook(testBook.uuid) {}

    // Assert
    verify(mockDocumentReference).delete()
  }

  @Test
  fun addBooks_callsFirestoreSet_andOnSuccess() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testBook)

    // Act
    bookFirestoreSource.addBooks(testBook) { assertTrue(it.isSuccess) }

    // Verify Firestore set operation
    verify(mockDocumentReference).set(testBook)
  }

  @Test
  fun updatebook_callsFirestoreSet_andOnSuccess() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testBook)

    // Act
    bookFirestoreSource.updateBook(testBook) { assert(it.isSuccess) }

    // Verify Firestore update operation
    verify(mockDocumentReference).set(testBook)
  }

  @Test
  fun documentToBooks_returnsDataBook_whenDocumentIsValid() {
    // Arrange
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.title)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.language.name)
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.isbn)

    // Act
    val result = bookFirestoreSource.documentToBooks(mockDocumentSnapshot)

    // Assert
    assertNotNull(result)
    assertEquals(testBook.title, result.getOrNull()?.title)
    assertEquals(testBook.author, result.getOrNull()?.author)
  }

  @Test
  fun documentToBooks_returnsNull_whenRequiredFieldIsMissing() {
    // Arrange - Missing "Title"
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.language.name)
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.isbn)

    // Act
    val result = bookFirestoreSource.documentToBooks(mockDocumentSnapshot)

    // Assert
    assertTrue(result.isFailure)
  }

  @Test
  fun documentToBooks_returnsNull_whenLanguageIsInvalid() {
    // Arrange - Invalid language value
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.title)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn("INVALID_LANGUAGE")
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.isbn)

    // Act
    val result = bookFirestoreSource.documentToBooks(mockDocumentSnapshot)

    // Assert
    assertTrue(result.isFailure) // Should return null due to invalid language
  }

  @Test
  fun getNewUid_returnsUniqueDocumentId() {
    // Arrange
    val collectionBooks = "Books"
    val mockDocumentReference = mock(DocumentReference::class.java)
    val expectedUid = "unique-document-id"

    // Mock Firestore to return a document with the desired ID
    `when`(mockFirestore.collection(collectionBooks).document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn(expectedUid)

    // Act
    val uid = bookFirestoreSource.getNewUid()

    // Assert
    assert(uid == expectedUid) // Ensure the ID matches the expected value
  }
}
