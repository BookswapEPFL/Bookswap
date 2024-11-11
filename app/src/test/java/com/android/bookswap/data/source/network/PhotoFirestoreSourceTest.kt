package com.android.bookswap.data.source.network

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.R
import com.android.bookswap.data.DataPhoto
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.UUID
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhotoFirestoreSourceTest {
  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private lateinit var PhotoFirestoreSource: PhotoFirestoreSource
  private lateinit var testPhoto: DataPhoto

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    PhotoFirestoreSource = PhotoFirestoreSource(mockFirestore)

    `when`(mockFirestore.collection(ArgumentMatchers.anyString()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(ArgumentMatchers.anyString()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val context = ApplicationProvider.getApplicationContext<Context>()
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.etranger_test)

    testPhoto =
        DataPhoto(
            uuid = UUID.randomUUID(),
            url = "", // Optional url field (don't know if it is really useful)
            timestamp = System.currentTimeMillis(),
            base64 = PhotoFirestoreSource.bitmapToBase64(bitmap))
  }

  @Test
  fun getPhoto_callsFirestoreGet() {
    // Arrange
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    `when`(mockDocumentSnapshot.getString("uuid")).thenReturn(testPhoto.uuid.toString())
    `when`(mockDocumentSnapshot.getString("url")).thenReturn(testPhoto.url)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testPhoto.timestamp)
    `when`(mockDocumentSnapshot.getString("base64")).thenReturn(testPhoto.base64)

    // Act
    PhotoFirestoreSource.getPhoto(
        testPhoto.uuid,
        onSuccess = { photo ->
          // Assert that the fetched photo matches the expected values
          assert(photo.uuid == testPhoto.uuid)
          assert(photo.url == testPhoto.url)
          assert(photo.timestamp == testPhoto.timestamp)
          assert(photo.base64 == testPhoto.base64)
        },
        onFailure = { throw AssertionError("Should not fail") })

    // Verify Firestore collection was called
    verify(mockCollectionReference).document(testPhoto.uuid.toString())
  }

  @Test
  fun addPhoto_callsFirestoreSet_andOnSuccess() {
    // Arrange
    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(testPhoto)

    // Act
    PhotoFirestoreSource.addPhoto(
        testPhoto,
        onSuccess = {
          // Assert success callback
          assert(true)
        },
        onFailure = { throw AssertionError("Should not fail") })

    // Verify Firestore set operation
    verify(mockDocumentReference).set(testPhoto)
  }

  @Test
  fun documentToPhoto_returnsDataPhoto_whenDocumentIsValid() {
    // Arrange
    `when`(mockDocumentSnapshot.getString("uuid")).thenReturn(testPhoto.uuid.toString())
    `when`(mockDocumentSnapshot.getString("url")).thenReturn(testPhoto.url)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testPhoto.timestamp)
    `when`(mockDocumentSnapshot.getString("base64")).thenReturn(testPhoto.base64)

    // Act
    val result = PhotoFirestoreSource.documentToPhoto(mockDocumentSnapshot)

    // Assert
    assert(result != null)
    assert(result?.uuid == testPhoto.uuid)
    assert(result?.url == testPhoto.url)
    assert(result?.timestamp == testPhoto.timestamp)
    assert(result?.base64 == testPhoto.base64)
  }

  @Test
  fun documentToPhoto_returnsNull_whenRequiredFieldIsMissing() {
    // Arrange - Missing "base64" field
    `when`(mockDocumentSnapshot.getString("uuid")).thenReturn(testPhoto.uuid.toString())
    `when`(mockDocumentSnapshot.getString("url")).thenReturn(testPhoto.url)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testPhoto.timestamp)
    `when`(mockDocumentSnapshot.getString("base64")).thenReturn(null)

    // Act
    val result = PhotoFirestoreSource.documentToPhoto(mockDocumentSnapshot)

    // Assert
    assert(result == null) // Should return null due to missing "base64"
  }
}
