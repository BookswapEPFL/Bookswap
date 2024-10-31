package com.android.bookswap.model.chat

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.source.network.COLLECTION_PATH
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.documentToMessage
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

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
  fun `test init calls onFailure when initialization fails`() {
    val callback = mock<(Result<Unit>) -> Unit>()
    val exceptionMessage = "Initialization failed"
    val exception = RuntimeException(exceptionMessage)
    messageRepository.init { callback(Result.failure(exception)) }
    verify(callback).invoke(argThat { isFailure && exceptionOrNull()?.message == exceptionMessage })
  }

  @Test
  fun `getMessages calls onSuccess`() {
    // Arrange
    val callback = mock<(Result<List<DataMessage>>) -> Unit>()
    val messages = listOf(testMessage)

    // Ensure the documents list is not null
    `when`(mockQuerySnapshot.documents).thenReturn(messages.map { mockDocumentSnapshot })

    // Mock the document snapshot fields
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(testMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(testMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(testMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(testMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testMessage.timestamp)

    // Act
    messageRepository.getMessages(callback)
    shadowOf(Looper.getMainLooper()).idle()

    // Assert
    verify(callback).invoke(argThat { isSuccess && getOrNull() == messages })
    verify(mockCollectionReference).get()
  }

  @Test
  fun `getMessages calls onFailure when Firestore query fails`() {
    val callback = mock<(Result<List<DataMessage>>) -> Unit>()
    val exception = RuntimeException("Firestore query failed")
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))
    messageRepository.getMessages(callback)
    shadowOf(Looper.getMainLooper()).idle()
    verify(callback).invoke(argThat { isFailure && exceptionOrNull() == exception })
  }

  @Test
  fun `get messages returns empty list when no messages exist`() {
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList())

    messageRepository.getMessages { result ->
      assertTrue(result.isSuccess)
      assertTrue(result.getOrNull()!!.isEmpty())
    }

    verify(mockCollectionReference).get()
  }

  @Test
  fun `get messages handles firestore failure`() {
    val exception = Exception("Firestore get error")
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

    messageRepository.getMessages { result ->
      assertTrue(result.isFailure)
      assertEquals(exception, result.exceptionOrNull())
    }

    verify(mockCollectionReference).get()
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

  @Test
  fun `send message handles firestore set failure`() {
    val exception = Exception("Firestore set error")
    val messageMap =
        mapOf(
            "id" to testMessage.id,
            "text" to testMessage.text,
            "senderId" to testMessage.senderId,
            "receiverId" to testMessage.receiverId,
            "timestamp" to testMessage.timestamp)

    `when`(mockDocumentReference.set(messageMap)).thenReturn(Tasks.forException(exception))

    messageRepository.sendMessage(testMessage) { result ->
      assertTrue(result.isFailure)
      assertEquals(exception, result.exceptionOrNull())
    }

    verify(mockDocumentReference).set(messageMap)
  }

  @Test
  fun `documentToMessage fails when document is missing fields`() {
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(null) // Simulate missing "id" field
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(testMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(testMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(testMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testMessage.timestamp)

    val result = documentToMessage(mockDocumentSnapshot)

    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is NullPointerException)
  }

  @Test
  fun `documentToMessage converts DocumentSnapshot to DataMessage`() {
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(testMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(testMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(testMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(testMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testMessage.timestamp)

    val result = documentToMessage(mockDocumentSnapshot)

    assertTrue(result.isSuccess)
    assertEquals(testMessage, result.getOrNull())
  }

  @Test
  fun `delete message fails for messages older than 15 minutes`() {
    val context = mock<Context>()
    val oldMessage = testMessage.copy(timestamp = System.currentTimeMillis() - (16 * 60 * 1000))

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(oldMessage.id)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(oldMessage.timestamp)

    // Act
    messageRepository.deleteMessage(
        oldMessage.id,
        { result ->
          assertTrue(result.isFailure)
          assertEquals(
              "Message can only be deleted within 15 minutes of being sent",
              result.exceptionOrNull()?.message)
        },
        context)

    // Verify Firestore delete was not called
    verify(mockDocumentReference, never()).delete()
  }

  @Test
  fun `delete message fails when message does not exist`() {
    val context = mock<Context>()
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false) // Simulate nonexistent document

    messageRepository.deleteMessage(
        testMessage.id,
        { result ->
          assertTrue(result.isFailure)
          assertEquals("Message not found", result.exceptionOrNull()?.message)
        },
        context)

    // Verify delete was not called
    verify(mockDocumentReference, never()).delete()
  }

  @Test
  fun `update message fails for messages older than 15 minutes`() {
    val context = mock<Context>()
    val oldMessage = testMessage.copy(timestamp = System.currentTimeMillis() - (16 * 60 * 1000))

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(oldMessage.id)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(oldMessage.timestamp)

    messageRepository.updateMessage(
        oldMessage,
        { result ->
          assertTrue(result.isFailure)
          assertEquals(
              "Message can only be updated within 15 minutes of being sent",
              result.exceptionOrNull()?.message)
        },
        context)

    // Verify Firestore update was not called
    verify(mockDocumentReference, never()).update(anyMap())
  }

  @Test
  fun `update message fails when message does not exist`() {
    val context = mock<Context>()
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false) // Simulate nonexistent document

    messageRepository.updateMessage(
        testMessage,
        { result ->
          assertTrue(result.isFailure)
          assertEquals("Message not found", result.exceptionOrNull()?.message)
        },
        context)

    // Verify update was not called
    verify(mockDocumentReference, never()).update(anyMap())
  }

  @Test
  fun `delete all messages between two users deletes documents`() {
    // Arrange
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("senderId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("receiverId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereNotEqualTo("senderId", "receiverId"))
        .thenReturn(mockCollectionReference)

    // Mock retrieval of a query snapshot with one document to delete
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.reference).thenReturn(mockDocumentReference)

    // Mock the Firestore batch process
    val mockBatch = mock<WriteBatch>()
    `when`(mockFirestore.batch()).thenReturn(mockBatch)
    `when`(mockBatch.delete(mockDocumentReference)).thenReturn(mockBatch)
    `when`(mockBatch.commit()).thenReturn(Tasks.forResult(null)) // Simulate successful commit

    // Act
    messageRepository.deleteAllMessages("user1", "user2") { result ->
      // Assert that the delete operation was successful
      assertTrue(result.isSuccess)
    }

    // Ensure that any pending tasks are executed
    shadowOf(Looper.getMainLooper()).idle()

    // Verify interactions
    verify(mockCollectionReference).get() // Verify retrieval call
    verify(mockBatch).delete(mockDocumentReference) // Verify deletion call
    verify(mockBatch).commit() // Verify batch commit call
  }
}
