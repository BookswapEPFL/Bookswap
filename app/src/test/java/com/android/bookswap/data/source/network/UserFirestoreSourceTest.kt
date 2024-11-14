package com.android.bookswap.data.source.network

import android.util.Log
import com.android.bookswap.data.DataUser
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Assert.fail
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class UserFirestoreSourceTest {

  private val mockFirestore: FirebaseFirestore = mockk()
  private val mockCollectionReference: CollectionReference = mockk()
  private val mockDocumentReference: DocumentReference = mockk()
  private val mockDocumentSnapshot: DocumentSnapshot = mockk()
  private val mockQuerySnapshot: QuerySnapshot = mockk()
  private val mockQuery: Query = mockk()

  private val userFirestoreSource: UserFirestoreSource = UserFirestoreSource(mockFirestore)

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

    userFirestoreSource

    every { mockFirestore.collection(any()) } returns mockCollectionReference
    every { mockCollectionReference.document() } returns mockDocumentReference
    every { mockCollectionReference.get() } returns Tasks.forResult(mockQuerySnapshot)
    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)


    every { mockDocumentSnapshot.getLong("userUUID.mostSignificantBits") } returns testUser.userUUID.mostSignificantBits
            every { mockDocumentSnapshot.getLong("userUUID.leastSignificantBits") } returns testUser.userUUID.leastSignificantBits
            every { mockDocumentSnapshot.getString("greeting") } returns testUser.greeting
            every { mockDocumentSnapshot.getString("firstName") } returns testUser.firstName

            every { mockDocumentSnapshot.getString("lastName") } returns testUser.lastName

            every { mockDocumentSnapshot.getString("email") } returns testUser.email
            every { mockDocumentSnapshot.getString("phoneNumber") } returns testUser.phoneNumber
            every { mockDocumentSnapshot.getDouble("latitude") } returns testUser.latitude
            every { mockDocumentSnapshot.getDouble("longitude") } returns testUser.longitude
            every { mockDocumentSnapshot.getString("profilePictureUrl") } returns testUser.profilePictureUrl
    every { mockDocumentSnapshot.get("bookList") } returns testUser.bookList
  every { mockDocumentSnapshot.getString("googleUid") } returns testUser.googleUid
  }

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
    verify { mockCollectionReference.get() }
  }

  @Test
  fun getUser() {
    // Arrange
    every { mockCollectionReference.whereEqualTo("UUID", any()) } returns mockQuery
    every { mockQuery.get() } returns Tasks.forResult(mockQuerySnapshot)

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
    verify { mockCollectionReference.whereEqualTo("UUID", any()) }
  }

  @Test
  fun addUser() {
    // Arrange
    every { mockDocumentReference.set(testUser) } returns Tasks.forResult(null)

    // Act
    userFirestoreSource.addUser(testUser) { result ->
      result.fold({ assert(true) }, { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify { mockDocumentReference.set(testUser) }
  }

  @Test
  fun updateUser() {
    // Arrange
    every { mockDocumentReference.set(testUser) } returns Tasks.forResult(null)

    // Act
    userFirestoreSource.updateUser(testUser) { result ->
      result.fold({ assert(true) }, { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify { mockDocumentReference.set(testUser) }

  }

  @Test
  fun deleteUser() {
    // Arrange
    every { mockDocumentReference.delete() } returns Tasks.forResult(null)

    // Act
    userFirestoreSource.deleteUser(testUser.userUUID) { result ->
      result.fold({ assert(true) }, { fail("Should not fail!") })
    }

    // Verify Firestore collection was accessed
    verify { mockDocumentReference.delete() }
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
    every { mockDocumentSnapshot.getString("greeting") } returns null

    // Act
    val result = userFirestoreSource.documentToUser(mockDocumentSnapshot)

    // Assert
    assert(result.getOrNull() == null)
    result.onFailure { Log.d("UserFirestoreSourceTest", "failure with message: ${it.message}") }
  }
}
