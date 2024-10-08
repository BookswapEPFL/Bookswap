package com.android.sample.model

import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.model.BooksFirestorerRepository
import com.android.bookswap.model.DataBook
import com.android.bookswap.model.Languages
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.util.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BooksFirestorerRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private lateinit var booksFirestorerRepository: BooksFirestorerRepository

  private val testBook =
      DataBook(
          Title = "Test Book",
          Author = "Test Author",
          Description = "Test Description",
          Rating = 5,
          photo = "http://example.com/photo.jpg",
          Language = Languages.ENGLISH,
          ISBN = "1234567890")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    booksFirestorerRepository = BooksFirestorerRepository(mockFirestore)

    `when`(mockFirestore.collection(ArgumentMatchers.any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(ArgumentMatchers.any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }



  @Test
  fun getbook_callsFirestoreGet() {
    // Arrange
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.Title)
    `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.Author)
    `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.Description)
    `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.Rating.toString())
    `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.Language.name)
    `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.ISBN)

    // Act
    booksFirestorerRepository.getbook(
        OnSucess = { books ->
          // Assert that the fetched books match the expected values
          assert(books.isNotEmpty())
          assert(books.first().Title == testBook.Title)
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
    booksFirestorerRepository.deletebooks(testBook.ISBN, testBook, {}, {})

    // Assert
    verify(mockDocumentReference).delete()
  }

    @Test
    fun addBooks_callsFirestoreSet_andOnSuccess() {
        // Arrange
        doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testBook)

        // Act
        booksFirestorerRepository.addBooks(testBook, {
            // Assert success callback
            assert(true)
        }, {
            fail("Should not fail")
        })

        // Verify Firestore set operation
        verify(mockDocumentReference).set(testBook)
    }
    @Test
    fun updatebook_callsFirestoreSet_andOnSuccess() {
        // Arrange
        doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testBook)

        // Act
        booksFirestorerRepository.updatebook(testBook, {
            // Assert success callback
            assert(true)
        }, {
            fail("Should not fail")
        })

        // Verify Firestore update operation
        verify(mockDocumentReference).set(testBook)
    }
    @Test
    fun documenttoBooks_returnsDataBook_whenDocumentIsValid() {
        // Arrange
        `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.Title)
        `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.Author)
        `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.Description)
        `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.Rating.toString())
        `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
        `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.Language.name)
        `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.ISBN)

        // Act
        val result = booksFirestorerRepository.documenttoBooks(mockDocumentSnapshot)

        // Assert
        assert(result != null)
        assert(result?.Title == testBook.Title)
        assert(result?.Author == testBook.Author)
    }

    @Test
    fun documenttoBooks_returnsNull_whenRequiredFieldIsMissing() {
        // Arrange - Missing "Title"
        `when`(mockDocumentSnapshot.getString("Title")).thenReturn(null)
        `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.Author)
        `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.Description)
        `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.Rating.toString())
        `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
        `when`(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.Language.name)
        `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.ISBN)

        // Act
        val result = booksFirestorerRepository.documenttoBooks(mockDocumentSnapshot)

        // Assert
        assert(result == null)
    }

    @Test
    fun documenttoBooks_returnsNull_whenLanguageIsInvalid() {
        // Arrange - Invalid language value
        `when`(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.Title)
        `when`(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.Author)
        `when`(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.Description)
        `when`(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.Rating.toString())
        `when`(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
        `when`(mockDocumentSnapshot.getString("Language")).thenReturn("INVALID_LANGUAGE")
        `when`(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.ISBN)

        // Act
        val result = booksFirestorerRepository.documenttoBooks(mockDocumentSnapshot)

        // Assert
        assert(result == null) // Should return null due to invalid language
    }



}
