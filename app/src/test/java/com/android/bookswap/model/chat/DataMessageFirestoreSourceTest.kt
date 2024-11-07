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
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class DataMessageFirestoreSourceTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockContext: Context

  private lateinit var firestore: FirebaseFirestore
  private lateinit var messageRepository: MessageFirestoreSource
  private lateinit var messageFirestoreSource: MessageFirestoreSource

  private val testMessage =
      DataMessage(
          id = "test_id",
          text = "Test message",
          senderId = "sender_id",
          receiverId = "receiver_id",
          timestamp = System.currentTimeMillis())

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firestore = FirebaseFirestore.getInstance()
    firestore.useEmulator("localhost", 8080)
    firestore.firestoreSettings =
        FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()

    messageFirestoreSource = MessageFirestoreSource(mockFirestore)
    messageRepository = MessageFirestoreSource(firestore)

    `when`(mockFirestore.collection(ArgumentMatchers.anyString()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(ArgumentMatchers.anyString()))
        .thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  // Init Tests
  @Test
  fun init_invokesCallbackOnSuccess() {
    messageFirestoreSource.init { result -> assert(result.isSuccess) }
  }

  // NewUid Tests
  @Test
  fun getNewUid_returnsUniqueDocumentId() {
    val expectedUid = "unique_test_id"
    `when`(mockDocumentReference.id).thenReturn(expectedUid)
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

    val uid = messageFirestoreSource.getNewUid()
    assert(uid == expectedUid) { "Expected UID to be $expectedUid but was $uid" }
  }

  // GetMessages Tests
  @Test
  fun getMessages_returnsMessagesOnSuccess() {
    // Arrange
    val documents = listOf(mockDocumentSnapshot)
    val expectedMessage = testMessage
    `when`(mockQuerySnapshot.documents).thenReturn(documents)
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(expectedMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(expectedMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(expectedMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(expectedMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(expectedMessage.timestamp)

    // Act
    messageFirestoreSource.getMessages { result ->
      // Assert
      assert(result.isSuccess)
      val messages = result.getOrNull()
      assert(messages?.size == 1)
      assert(messages?.first() == expectedMessage)
    }
  }

  @Test
  fun getMessages_returnsFailureOnException() {
    val exception = RuntimeException("Firestore error")
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.getMessages { result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  @Test
  fun getMessages_returnsEmptyListOnEmptyResult() {
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList())
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    messageFirestoreSource.getMessages { result ->
      assert(result.isSuccess)
      val messages = result.getOrNull()
      assert(messages?.isEmpty() == true)
    }
  }

  // SendMessage Tests
  @Test
  fun sendMessage_callsFirestoreSet_onSuccess() {
    val messageMap =
        mapOf(
            "id" to testMessage.id,
            "text" to testMessage.text,
            "senderId" to testMessage.senderId,
            "receiverId" to testMessage.receiverId,
            "timestamp" to testMessage.timestamp)
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.set(messageMap)).thenReturn(Tasks.forResult(null))

    messageFirestoreSource.sendMessage(testMessage) { result -> assert(result.isSuccess) }

    verify(mockDocumentReference).set(messageMap)
  }

  @Test
  fun sendMessage_callsFirestoreSet_onFailure() {
    val exception = RuntimeException("Firestore error")
    val messageMap =
        mapOf(
            "id" to testMessage.id,
            "text" to testMessage.text,
            "senderId" to testMessage.senderId,
            "receiverId" to testMessage.receiverId,
            "timestamp" to testMessage.timestamp)
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.set(messageMap)).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.sendMessage(testMessage) { result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  // DeleteMessage Tests
  @Test
  fun deleteMessage_deletesMessageOnSuccess() {
    val fifteenMinutesInMillis = 15 * 60 * 1000
    val recentMessage =
        testMessage.copy(timestamp = System.currentTimeMillis() - (fifteenMinutesInMillis - 1000))

    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(recentMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true) // Ensure document exists
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(recentMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(recentMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(recentMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(recentMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(recentMessage.timestamp)
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    messageFirestoreSource.deleteMessage(
        recentMessage.id, { result -> assert(result.isSuccess) }, mockContext)

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun deleteMessage_failsWhenMessageNotFound() {
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false) // Message does not exist

    messageFirestoreSource.deleteMessage(
        testMessage.id,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
  }

  @Test
  fun deleteMessage_failsWhenMessageTooOld() {
    val oldMessage =
        testMessage.copy(
            timestamp =
                System.currentTimeMillis() -
                    (15 * 60 * 1000 + 1000)) // Sent more than 15 minutes ago
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(oldMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(oldMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(oldMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(oldMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(oldMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(oldMessage.timestamp)

    messageFirestoreSource.deleteMessage(
        oldMessage.id,
        { result ->
          assert(result.isFailure)
          assert(
              result.exceptionOrNull()?.message ==
                  "Message can only be deleted within 15 minutes of being sent")
        },
        mockContext)
  }

  @Test
  fun deleteMessage_failsOnFirestoreRetrievalError() {
    val exception = RuntimeException("Firestore retrieval error")
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.deleteMessage(
        testMessage.id,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun deleteMessage_failsOnFirestoreDeleteError() {
    val exception = RuntimeException("Firestore delete error")
    val recentMessage =
        testMessage.copy(
            timestamp =
                System.currentTimeMillis() - (15 * 60 * 1000 - 1000)) // Sent within 15 minutes
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(recentMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(recentMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(recentMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(recentMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(recentMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(recentMessage.timestamp)
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.deleteMessage(
        recentMessage.id,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun deleteMessage_failsWhenConversionFails() {
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id"))
        .thenReturn(null) // Missing field causes conversion failure

    messageFirestoreSource.deleteMessage(
        testMessage.id,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
  }

  // DeleteAllMessages Tests
  @Test
  fun deleteAllMessages_deletesMessagesSuccessfully() {
    val documents = listOf(mockDocumentSnapshot)
    val mockBatch = mock(WriteBatch::class.java)

    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("senderId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("receiverId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereNotEqualTo("senderId", "receiverId"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(documents)

    `when`(mockFirestore.batch()).thenReturn(mockBatch)
    documents.forEach { `when`(mockBatch.delete(it.reference)).thenReturn(mockBatch) }
    `when`(mockBatch.commit()).thenReturn(Tasks.forResult(null))

    messageFirestoreSource.deleteAllMessages("user1", "user2") { result ->
      assert(result.isSuccess)
    }

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockBatch).commit()
  }

  @Test
  fun deleteAllMessages_returnsSuccessWhenNoMessagesToDelete() {
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("senderId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("receiverId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereNotEqualTo("senderId", "receiverId"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList()) // No messages found

    messageFirestoreSource.deleteAllMessages("user1", "user2") { result ->
      assert(result.isSuccess)
    }
  }

  @Test
  fun deleteAllMessages_failsWhenFirestoreRetrievalError() {
    val exception = RuntimeException("Firestore retrieval error")
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("senderId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("receiverId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereNotEqualTo("senderId", "receiverId"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.deleteAllMessages("user1", "user2") { result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  @Test
  fun deleteAllMessages_failsWhenBatchCommitError() {
    val documents = listOf(mockDocumentSnapshot)
    val exception = RuntimeException("Batch commit error")
    val mockBatch = mock(WriteBatch::class.java)

    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("senderId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("receiverId", listOf("user1", "user2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereNotEqualTo("senderId", "receiverId"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(documents)

    `when`(mockFirestore.batch()).thenReturn(mockBatch)
    documents.forEach { `when`(mockBatch.delete(it.reference)).thenReturn(mockBatch) }
    `when`(mockBatch.commit()).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.deleteAllMessages("user1", "user2") { result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  // UpdateMessage Tests
  @Test
  fun updateMessage_failsWhenMessageNotFound() {
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)

    messageFirestoreSource.updateMessage(
        testMessage,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
  }

  @Test
  fun updateMessage_failsWhenMessageTooOld() {
    val oldMessage =
        testMessage.copy(
            timestamp =
                System.currentTimeMillis() -
                    (15 * 60 * 1000 + 1000)) // Sent more than 15 minutes ago
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(oldMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(oldMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(oldMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(oldMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(oldMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(oldMessage.timestamp)

    messageFirestoreSource.updateMessage(
        oldMessage,
        { result ->
          assert(result.isFailure)
          assert(
              result.exceptionOrNull()?.message ==
                  "Message can only be updated within 15 minutes of being sent")
        },
        mockContext)
  }

  @Test
  fun updateMessage_failsOnFirestoreRetrievalError() {
    val exception = RuntimeException("Firestore retrieval error")
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.updateMessage(
        testMessage,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun updateMessage_failsOnFirestoreUpdateError() {
    val exception = RuntimeException("Firestore update error")
    val recentMessage =
        testMessage.copy(timestamp = System.currentTimeMillis() - (15 * 60 * 1000 - 1000))
    val updatedText = "Updated text"
    val messageMap = mapOf("text" to updatedText, "timestamp" to System.currentTimeMillis())

    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(recentMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(recentMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(recentMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(recentMessage.senderId)
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn(recentMessage.receiverId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(recentMessage.timestamp)
    `when`(mockDocumentReference.update(messageMap)).thenReturn(Tasks.forException(exception))

    messageFirestoreSource.updateMessage(
        recentMessage.copy(text = updatedText),
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun updateMessage_failsWhenConversionFails() {
    `when`(mockFirestore.collection(COLLECTION_PATH)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(testMessage.id)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(null)

    messageFirestoreSource.updateMessage(
        testMessage,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
  }

  @Test
  fun documentToMessage_returnsDataMessageOnSuccess() {
    `when`(mockDocumentSnapshot.getString("id")).thenReturn("test_id")
    `when`(mockDocumentSnapshot.getString("text")).thenReturn("Test message")
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn("sender1")
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn("receiver1")
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(1634567890L)

    val result = documentToMessage(mockDocumentSnapshot)

    assert(result.isSuccess)
    val message = result.getOrNull()
    assert(message != null)
    assert(message?.id == "test_id")
    assert(message?.text == "Test message")
    assert(message?.senderId == "sender1")
    assert(message?.receiverId == "receiver1")
    assert(message?.timestamp == 1634567890L)
  }

  @Test
  fun documentToMessage_returnsFailureWhenFieldIsMissing() {
    `when`(mockDocumentSnapshot.getString("id")).thenReturn("test_id")
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(null) // Missing field
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn("sender1")
    `when`(mockDocumentSnapshot.getString("receiverId")).thenReturn("receiver1")
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(1634567890L)

    val result = documentToMessage(mockDocumentSnapshot)

    assert(result.isFailure)
    assert(result.exceptionOrNull() is Exception)
  }
}
