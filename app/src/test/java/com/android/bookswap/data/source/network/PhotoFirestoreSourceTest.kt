package com.android.bookswap.data.source.network

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.R
import com.android.bookswap.data.DataPhoto
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhotoFirestoreSourceTest {
  private val mockFirestore: FirebaseFirestore = mockk()
  private val mockCollectionReference: CollectionReference = mockk()
  private val mockDocumentReference: DocumentReference = mockk()
  private val mockDocumentSnapshot: DocumentSnapshot = mockk()
  private val mockQuerySnapshot: QuerySnapshot = mockk()

  private val photoFirestoreSource = PhotoFirestoreSource(mockFirestore)

  private lateinit var testPhoto: DataPhoto

  @Before
  fun setup() {
    every { mockFirestore.collection(any()) } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference
    every { mockCollectionReference.get() } returns Tasks.forResult(mockQuerySnapshot)

    val context = ApplicationProvider.getApplicationContext<Context>()
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.etranger_test)

    testPhoto =
        DataPhoto(
            uuid = UUID.randomUUID(),
            url = "", // Optional url field (don't know if it is really useful)
            timestamp = System.currentTimeMillis(),
            base64 = photoFirestoreSource.bitmapToBase64(bitmap))

    // Arrange snapshot
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getString("uuid") } returns testPhoto.uuid.toString()
    every { mockDocumentSnapshot.getString("url") } returns testPhoto.url
    every { mockDocumentSnapshot.getLong("timestamp") } returns testPhoto.timestamp
    every { mockDocumentSnapshot.getString("base64") } returns testPhoto.base64
  }

  @Test
  fun `getPhoto call firestore get`() {

    // Act
    photoFirestoreSource.getPhoto(
        testPhoto.uuid,
        callback = { result ->
          assertTrue(result.isSuccess)
          val photo = result.getOrThrow()
          // Assert that the fetched photo matches the expected values
          assertEquals(testPhoto.uuid, photo.uuid)
          assertEquals(testPhoto.url, photo.url)
          assertEquals(testPhoto.timestamp, photo.timestamp)
          assertEquals(testPhoto.base64, photo.base64)
        })

    // Verify Firestore collection was called
    verify { mockCollectionReference.document(testPhoto.uuid.toString()) }
    verify { mockDocumentReference.get() }
  }

  @Test
  fun `addPhoto call set and success`() {
    // Arrange
    every { mockDocumentReference.set(photoFirestoreSource.photoToDocument(testPhoto)) } returns
        Tasks.forResult(null)

    // Act
    photoFirestoreSource.addPhoto(
        testPhoto,
        callback = { result ->
          // Assert success callback
          assert(result.isSuccess)
        })
    // Verify Firestore set operation
    verify { mockDocumentReference.set(photoFirestoreSource.photoToDocument(testPhoto)) }
  }

  @Test
  fun `documentToPhoto is valid`() {
    // Act
    val result = photoFirestoreSource.documentToPhoto(mockDocumentSnapshot)

    // Assert
    assertNotNull(result)
    assertEquals(testPhoto.uuid, result!!.uuid)
    assertEquals(testPhoto.url, result.url)
    assertEquals(testPhoto.timestamp, result.timestamp)
    assertEquals(testPhoto.base64, result.base64)
  }

  @Test
  fun `documentToPhoto returns null on error`() {
    // Arrange - Missing "base64" field
    every { mockDocumentSnapshot.getString("base64") } returns null

    // Act
    val result = photoFirestoreSource.documentToPhoto(mockDocumentSnapshot)

    // Assert
    assertNull(result) // Should return null due to missing "base64"
  }
}
