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
import java.util.UUID
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
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
          UUID.randomUUID(),
          "M.",
          "John",
          "Doe",
          "John.Doe@example.com",
          "+41223456789",
          0.0,
          0.0,
          "dummyPic.png")

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

    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getLong("userUUID.mostSignificantBits"))
        .thenReturn(testUser.userUUID.mostSignificantBits)!!
    `when`(mockDocumentSnapshot.getLong("userUUID.leastSignificantBits"))
        .thenReturn(testUser.userUUID.leastSignificantBits)!!
    `when`(mockDocumentSnapshot.getString("greeting")).thenReturn(testUser.greeting)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(testUser.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(testUser.lastName)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(testUser.email)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(testUser.phoneNumber)
    `when`(mockDocumentSnapshot.getDouble("latitude")).thenReturn(testUser.latitude)
    `when`(mockDocumentSnapshot.getDouble("longitude")).thenReturn(testUser.longitude)
    `when`(mockDocumentSnapshot.getString("profilePictureUrl"))
        .thenReturn(testUser.profilePictureUrl)
    `when`(mockDocumentSnapshot.get("bookList")).thenReturn(testUser.bookList)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(testUser.googleUid)
  }

  @After fun tearDown() {}

  @Test fun init() {}

  @Test
  fun getUsers() {
    // Act
    userFirestoreSource.getUsers { result ->
      result.fold(
          {
            // Assert proper data transfert
            assert(it.isNotEmpty())
            assert(it.first().printFullname() == testUser.printFullname())
          },
          { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockCollectionReference).get()
  }

  @Test
  fun getUser() {
    // Arrange
    `when`(mockCollectionReference.whereEqualTo(eq("UUID"), ArgumentMatchers.any()))
        .thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Act
    userFirestoreSource.getUser(testUser.userUUID) { result ->
      result.fold(
          {
            // Assert proper data transfer
            assert(it.printFullname() == testUser.printFullname())
          },
          { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockCollectionReference).whereEqualTo(eq("UUID"), any())
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
    userFirestoreSource.deleteUser(testUser.userUUID) { result ->
      result.fold({ assert(true) }, { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify(mockDocumentReference).delete()
  }

  @Test
  fun documentToUser_validDoc() {
    // Act
    val result = userFirestoreSource.documentToUser(mockDocumentSnapshot)

    // Assert
    assert(result.getOrNull() != null)
    result.onSuccess { assert(it.printFullname() == testUser.printFullname()) }
  }

  @Test
  fun documentToUser_invalidDoc() {
    `when`(mockDocumentSnapshot.getString("greeting")).thenReturn(null)

    // Act
    val result = userFirestoreSource.documentToUser(mockDocumentSnapshot)

    // Assert
    assert(result.getOrNull() == null)
    result.onFailure { Log.d("UserFirestoreSourceTest", "failure with message: ${it.message}") }
  }
}
