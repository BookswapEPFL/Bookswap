package com.android.bookswap.data.source.network

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.data.DataUser
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Assert.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserFirestoreSourceTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockQuery: Query

  private lateinit var userFirestoreSource: UserFirestoreSource

  private val testUser =
      DataUser(
          "M.",
          "John",
          "Doe",
          "John.Doe@example.com",
          "+41223456789",
          0.0,
          0.0,
          "dummyPic.png",
          "dummyUUID0000")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    userFirestoreSource = UserFirestoreSource(mockFirestore)

    `when`(mockFirestore.collection(ArgumentMatchers.any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(ArgumentMatchers.any()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @After fun tearDown() {}

  @Test fun init() {}

  @Test
  fun getUsers() {
    // Arrange
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("UUID")).thenReturn(testUser.userId)
    `when`(mockDocumentSnapshot.getString("Greeting")).thenReturn(testUser.greeting)
    `when`(mockDocumentSnapshot.getString("Firstname")).thenReturn(testUser.firstName)
    `when`(mockDocumentSnapshot.getString("Lastname")).thenReturn(testUser.lastName)
    `when`(mockDocumentSnapshot.getString("Email")).thenReturn(testUser.email)
    `when`(mockDocumentSnapshot.getString("Phone")).thenReturn(testUser.phoneNumber)
    `when`(mockDocumentSnapshot.getDouble("Latitude")).thenReturn(testUser.latitude)
    `when`(mockDocumentSnapshot.getDouble("Longitude")).thenReturn(testUser.longitude)
    `when`(mockDocumentSnapshot.getString("Picture")).thenReturn(testUser.profilePictureUrl)

    // Act
    userFirestoreSource.getUsers { result ->
      result.fold(
          {
            // Assert proper data transfert
            assert(it.isNotEmpty())
            assert(it.first().printFull1Line() == testUser.printFull1Line())
          },
          { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockCollectionReference).get()
  }

  @Test
  fun getUser() {
    // Arrange
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockCollectionReference.whereEqualTo(eq("UUID"), ArgumentMatchers.anyString()))
        .thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockDocumentSnapshot.getString("UUID")).thenReturn(testUser.userId)
    `when`(mockDocumentSnapshot.getString("Greeting")).thenReturn(testUser.greeting)
    `when`(mockDocumentSnapshot.getString("Firstname")).thenReturn(testUser.firstName)
    `when`(mockDocumentSnapshot.getString("Lastname")).thenReturn(testUser.lastName)
    `when`(mockDocumentSnapshot.getString("Email")).thenReturn(testUser.email)
    `when`(mockDocumentSnapshot.getString("Phone")).thenReturn(testUser.phoneNumber)
    `when`(mockDocumentSnapshot.getDouble("Latitude")).thenReturn(testUser.latitude)
    `when`(mockDocumentSnapshot.getDouble("Longitude")).thenReturn(testUser.longitude)
    `when`(mockDocumentSnapshot.getString("Picture")).thenReturn(testUser.profilePictureUrl)

    // Act
    userFirestoreSource.getUser("dummyUUID0000") { result ->
      result.fold(
          {
            // Assert proper data transfert
            assert(it.printFull1Line() == testUser.printFull1Line())
          },
          { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockCollectionReference).whereEqualTo(eq("UUID"), ArgumentMatchers.anyString())
  }

  @Test
  fun addUser() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testUser)

    // Act
    userFirestoreSource.addUser(testUser) { result ->
      result.fold({ assert(true) }, { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockDocumentReference).set(testUser)
  }

  @Test
  fun updateUser() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testUser)

    // Act
    userFirestoreSource.updateUser(testUser) { result ->
      result.fold({ assert(true) }, { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockDocumentReference).set(testUser)
  }

  @Test
  fun deleteUser() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).delete()

    // Act
    userFirestoreSource.deleteUser(testUser.userId) { result ->
      result.fold({ assert(true) }, { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockDocumentReference).delete()
  }

  @Test
  fun documentToUser_validDoc() {
    // Arrange
    `when`(mockDocumentSnapshot.getString("UUID")).thenReturn(testUser.userId)
    `when`(mockDocumentSnapshot.getString("Greeting")).thenReturn(testUser.greeting)
    `when`(mockDocumentSnapshot.getString("Firstname")).thenReturn(testUser.firstName)
    `when`(mockDocumentSnapshot.getString("Lastname")).thenReturn(testUser.lastName)
    `when`(mockDocumentSnapshot.getString("Email")).thenReturn(testUser.email)
    `when`(mockDocumentSnapshot.getString("Phone")).thenReturn(testUser.phoneNumber)
    `when`(mockDocumentSnapshot.getDouble("Latitude")).thenReturn(testUser.latitude)
    `when`(mockDocumentSnapshot.getDouble("Longitude")).thenReturn(testUser.longitude)
    `when`(mockDocumentSnapshot.getString("Picture")).thenReturn(testUser.profilePictureUrl)

    // Act
    val result = userFirestoreSource.documentToUser(mockDocumentSnapshot)

    // Assert
    assert(result.getOrNull() != null)
    result.onSuccess { assert(it.printFull1Line() == testUser.printFull1Line()) }
  }

  @Test
  fun documentToUser_invalidDoc() {
    // Arrange
    `when`(mockDocumentSnapshot.getString("UUID")).thenReturn(testUser.userId)
    `when`(mockDocumentSnapshot.getString("Greeting")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("Firstname")).thenReturn(testUser.firstName)
    `when`(mockDocumentSnapshot.getString("Lastname")).thenReturn(testUser.lastName)
    `when`(mockDocumentSnapshot.getString("Email")).thenReturn(testUser.email)
    `when`(mockDocumentSnapshot.getString("Phone")).thenReturn(testUser.phoneNumber)
    `when`(mockDocumentSnapshot.getDouble("Latitude")).thenReturn(testUser.latitude)
    `when`(mockDocumentSnapshot.getDouble("Longitude")).thenReturn(testUser.longitude)
    `when`(mockDocumentSnapshot.getString("Picture")).thenReturn(testUser.profilePictureUrl)

    // Act
    val result = userFirestoreSource.documentToUser(mockDocumentSnapshot)

    // Assert
    assert(result.getOrNull() == null)
    result.onFailure { Log.d("UserFirestoreSourceTest", "failure with message: ${it.message}") }
  }
}
