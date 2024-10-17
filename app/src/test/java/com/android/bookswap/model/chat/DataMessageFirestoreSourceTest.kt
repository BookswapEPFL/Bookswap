package com.android.bookswap.model.chat

import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Assert.assertEquals
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
class DataMessageFirestoreSourceTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private val testMessage =
      DataMessage(
          id = "message-id",
          text = "Hello, World!",
          senderId = "user-id",
          receiverId = "receiver-id",
          timestamp = System.currentTimeMillis())

  private lateinit var messageRepository: MessageFirestoreSource

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    messageRepository = MessageFirestoreSource(mockFirestore)

    `when`(mockFirestore.collection(ArgumentMatchers.anyString()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(ArgumentMatchers.anyString()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @Test
  fun `test getNewUid returns valid document id`() {
    val expectedUid = "randomGeneratedUid"
    `when`(mockFirestore.collection(ArgumentMatchers.anyString()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn(expectedUid)

    val newUid = messageRepository.getNewUid()

    assert(newUid == expectedUid)
  }

  @Test
  fun `test init calls onSuccess`() {
    val callback = mock<(Result<Unit>) -> Unit>()
    messageRepository.init(callback)
    verify(callback).invoke(Result.success(Unit))
  }

  @Test
  fun `get messages calls firestore get`() {
    // Arrange
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(testMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(testMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(testMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(testMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testMessage.timestamp)

    // Act
    messageRepository.getMessages() {
      assertTrue(it.isSuccess)
      assertTrue(it.getOrNull()!!.isNotEmpty())
      assertEquals(testMessage.id, it.getOrNull()?.first()?.id)
    }

    // Verify Firestore collection was called
    verify(mockCollectionReference).get()
  }

  @Test
  fun `send message calls firestore set and calls OnSuccess`() {
    // Arrange
    val messageMap =
        mapOf(
            "id" to testMessage.id,
            "text" to testMessage.text,
            "senderId" to testMessage.senderId,
            "receiverId" to testMessage.receiverId,
            "timestamp" to testMessage.timestamp)

    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(messageMap)

    // Act
    messageRepository.sendMessage(testMessage) { assertTrue(it.isSuccess) }

    // Verify Firestore set operation
    verify(mockDocumentReference).set(messageMap)
  }
}
