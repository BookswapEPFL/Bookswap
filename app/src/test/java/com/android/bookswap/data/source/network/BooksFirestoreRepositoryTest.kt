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
import com.google.firebase.firestore.util.Assert.fail
import java.util.UUID
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
class BooksFirestoreRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private lateinit var booksFirestorerRepository: BooksFirestoreRepository

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

    booksFirestorerRepository = BooksFirestoreRepository(mockFirestore)

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
    booksFirestorerRepository.getBook(
        OnSucess = { books ->
          // Assert that the fetched books match the expected values
          assert(books.isNotEmpty())
          assert(books.first().title == testBook.title)
        },
        onFailure = { fail("Should not fail") })

    // Verify that Firestore collection was called
    verify(mockCollectionReference).get()
  }

  @Test
  fun deletebooks_shouldCallFirestoreDelete() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).delete()

    // Act
    booksFirestorerRepository.deleteBooks(testBook.isbn!!, testBook, {}, {})

    // Assert
    verify(mockDocumentReference).delete()
  }

  @Test
  fun addBooks_callsFirestoreSet_andOnSuccess() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testBook)

    // Act
    booksFirestorerRepository.addBook(
        testBook,
        {
          // Assert success callback
          assert(true)
        },
        { fail("Should not fail") })

    // Verify Firestore set operation
    verify(mockDocumentReference).set(testBook)
  }

  @Test
  fun updatebook_callsFirestoreSet_andOnSuccess() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testBook)

    // Act
    booksFirestorerRepository.updateBook(
        testBook,
        {
          // Assert success callback
          assert(true)
        },
        { fail("Should not fail") })

    // Verify Firestore update operation
    verify(mockDocumentReference).set(testBook)
  }

  @Test
  fun documenttoBooks_returnsDataBook_whenDocumentIsValid() {
    // Arrange
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.title)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.language.name)
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.isbn)

    // Act
    val result = booksFirestorerRepository.documentToBooks(mockDocumentSnapshot)

    // Assert
    assert(result != null)
    assert(result?.title == testBook.title)
    assert(result?.author == testBook.author)
  }

  @Test
  fun documenttoBooks_returnsNull_whenRequiredFieldIsMissing() {
    // Arrange - Missing "Title"
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.language.name)
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.isbn)

    // Act
    val result = booksFirestorerRepository.documentToBooks(mockDocumentSnapshot)

    // Assert
    assert(result == null)
  }

  @Test
  fun documenttoBooks_returnsNull_whenLanguageIsInvalid() {
    // Arrange - Invalid language value
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.title)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn("INVALID_LANGUAGE")
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.isbn)

    // Act
    val result = booksFirestorerRepository.documentToBooks(mockDocumentSnapshot)

    // Assert
    assert(result == null) // Should return null due to invalid language
  }

  /**@Test
  fun getNewUid_returnsUniqueDocumentId() {
    // Arrange
    val collectionBooks = "Books"
    val mockDocumentReference = mock(DocumentReference::class.java)
    val expectedUid = UUID.randomUUID().toString()

    // Mock Firestore to return a document with the desired ID
    `when`(mockFirestore.collection(collectionBooks).document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn(expectedUid)

    // Act
    val uid = booksFirestorerRepository.getNewUid()

    // Assert
    assert(uid.toString() == expectedUid) // Ensure the ID matches the expected value
  }
  */
}
