package com.android.sample.model

import androidx.test.core.app.ApplicationProvider
import com.example.boooooo.DataBook
import com.example.boooooo.Languages
import com.google.android.gms.tasks.Tasks
import com.google.common.base.CharMatcher.any
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BooksFirestorerRepositoryTest {

  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockCollectionReference: CollectionReference
  private lateinit var mockDocumentReference: DocumentReference
  private lateinit var mockDocumentSnapshot: DocumentSnapshot
  private lateinit var mockQuerySnapshot: QuerySnapshot

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

    whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @Test
  fun getNewUid_returnsUniqueId() {
    // Arrange
    whenever(mockDocumentReference.id).thenReturn("unique-id-1")

    // Act
    val uid = booksFirestorerRepository.getNewUid()

    // Assert
    assert(uid == "unique-id-1")
  }

  @Test
  fun getbook_callsFirestoreGet() {
    // Arrange
    whenever(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.getString("Title")).thenReturn(testBook.Title)
    whenever(mockDocumentSnapshot.getString("Author")).thenReturn(testBook.Author)
    whenever(mockDocumentSnapshot.getString("Description")).thenReturn(testBook.Description)
    whenever(mockDocumentSnapshot.getString("Rating")).thenReturn(testBook.Rating.toString())
    whenever(mockDocumentSnapshot.getString("Photo")).thenReturn(testBook.photo)
    whenever(mockDocumentSnapshot.getString("Language")).thenReturn(testBook.Language.name)
    whenever(mockDocumentSnapshot.getString("ISBN")).thenReturn(testBook.ISBN)

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
  fun addBooks_shouldCallFirestoreSet() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(any())

    // Act
    booksFirestorerRepository.addBooks(testBook, {}, {})

    // Assert
    verify(mockDocumentReference).set(argThat { this.Title == testBook.Title })
  }

  @Test
  fun updatebook_shouldCallFirestoreSet() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(any())

    // Act
    booksFirestorerRepository.updatebook(testBook, {}, {})

    // Assert
    verify(mockDocumentReference).set(argThat { this.Title == testBook.Title })
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
}

