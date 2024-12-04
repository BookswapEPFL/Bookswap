package com.android.bookswap.model.chat

import android.os.Looper
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.source.network.DataConverter
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.documentToMessage
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

const val COLLECTION_PATH = "chats"

@RunWith(RobolectricTestRunner::class)
class DataMessageFirestoreSourceTest {

  private val mockFirestore: FirebaseFirestore = mockk()
  private val mockCollectionReference: CollectionReference = mockk()
  private val mockDocumentReference: DocumentReference = mockk()
  private val mockDocumentSnapshot: DocumentSnapshot = mockk()
  private val mockQuerySnapshot: QuerySnapshot = mockk()

  private lateinit var messageRepository: MessageFirestoreSource
  private lateinit var messageFirestoreSource: MessageFirestoreSource

  private lateinit var chatPath: String

  private val testMessage =
      DataMessage(
          messageType = MessageType.TEXT,
          uuid = UUID.randomUUID(),
          text = "Test message",
          senderUUID = UUID.randomUUID(),
          receiverUUID = UUID.randomUUID(),
          timestamp = System.currentTimeMillis())

  @Before
  fun setup() {
    // Initialize MessageFirestoreSource
    messageFirestoreSource = MessageFirestoreSource(mockFirestore)
    messageRepository = MessageFirestoreSource(mockFirestore)

    // Mock Firestore interactions
    every { mockFirestore.collection(any()) } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference
    every { mockCollectionReference.get() } returns Tasks.forResult(mockQuerySnapshot)

    // Generate consistent chat path using the test helper
    chatPath = mergeUUIDsForTest(testMessage.senderUUID, testMessage.receiverUUID)

    // Mock Firestore collections and documents for the generated path
    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages")
    } returns mockCollectionReference
    every { mockCollectionReference.document(testMessage.uuid.toString()) } returns
        mockDocumentReference

    // Mock document snapshot to match the test data
    every { mockDocumentSnapshot.get("uuid") } returns testMessage.uuid.toString()
    every { mockDocumentSnapshot.getString("text") } returns testMessage.text
    every { mockDocumentSnapshot.get("senderUUID") } returns testMessage.senderUUID.toString()
    every { mockDocumentSnapshot.get("receiverUUID") } returns testMessage.receiverUUID.toString()
    every { mockDocumentSnapshot.get("timestamp") } returns testMessage.timestamp.toString()
    every { mockDocumentSnapshot.getString("messageType") } returns testMessage.messageType.name
    every { mockDocumentSnapshot.exists() } returns true

    // Mock query snapshot for empty and non-empty results
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)
    every { mockQuerySnapshot.isEmpty } returns false
  }

  // Init Tests
  @Test
  fun `init invokes callback onSuccess`() {
    messageFirestoreSource.init { result -> assert(result.isSuccess) }
  }

  // GetMessages Tests
  @Test
  fun `getMessages returns messages onSuccess`() {
    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages")
    } returns mockCollectionReference
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)

    messageFirestoreSource.getMessages(testMessage.senderUUID, testMessage.receiverUUID) { result ->
      assertTrue(result.isSuccess)
      val messages = result.getOrNull()
      assertEquals(1, messages?.size)
      assertEquals(testMessage, messages?.first())
    }
  }

  @Test
  fun `getMessages returns Failure on fail`() {
    val exception = RuntimeException("Firestore error")
    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages")
    } returns mockCollectionReference
    every { mockCollectionReference.get() } returns Tasks.forException(exception)

    messageFirestoreSource.getMessages(testMessage.senderUUID, testMessage.receiverUUID) { result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  @Test
  fun `getMessages returns emptyList on empty result`() {
    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages")
    } returns mockCollectionReference
    every { mockQuerySnapshot.documents } returns emptyList()

    messageFirestoreSource.getMessages(testMessage.senderUUID, testMessage.receiverUUID) { result ->
      assertTrue(result.isSuccess)
      val messages = result.getOrNull()
      assertTrue(messages?.isEmpty() == true)
    }
  }

  // SendMessage Tests
  @Test
  fun `sendMessage calls firestore set on success`() {
    val messageMap =
        mapOf(
            "uuid" to DataConverter.convert_UUID(testMessage.uuid),
            "text" to testMessage.text,
            "senderUUID" to DataConverter.convert_UUID(testMessage.senderUUID),
            "receiverUUID" to DataConverter.convert_UUID(testMessage.receiverUUID),
            "timestamp" to DataConverter.convert_Long(testMessage.timestamp),
            "messageType" to testMessage.messageType.name)
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .set(messageMap)
    } returns Tasks.forResult(null)

    messageFirestoreSource.sendMessage(testMessage) { result -> assert(result.isSuccess) }

    verify {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .set(messageMap)
    }
  }

  @Test
  fun `sendMessage calls firestore set on failure`() {
    val exception = RuntimeException("Firestore error")
    val messageMap =
        mapOf(
            "uuid" to DataConverter.convert_UUID(testMessage.uuid),
            "text" to testMessage.text,
            "senderUUID" to DataConverter.convert_UUID(testMessage.senderUUID),
            "receiverUUID" to DataConverter.convert_UUID(testMessage.receiverUUID),
            "timestamp" to DataConverter.convert_Long(testMessage.timestamp),
            "messageType" to testMessage.messageType.name)

    // Mock Firestore behavior for the failure case
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .set(messageMap)
    } returns Tasks.forException(exception)

    // Execute sendMessage and verify the result
    messageFirestoreSource.sendMessage(testMessage) { result ->
      assert(result.isFailure)
      assertEquals(exception, result.exceptionOrNull())
    }

    // Verify Firestore interactions
    verify {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .set(messageMap)
    }
  }

  // DeleteMessage Tests
  @Test
  fun `deleteMessage deletes message on success`() {
    val fifteenMinutesInMillis = 15 * 60 * 1000
    val timestamp = System.currentTimeMillis() - (fifteenMinutesInMillis - 1000)

    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.get("timestamp") } returns timestamp.toString()
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .delete()
    } returns Tasks.forResult(null)

    messageFirestoreSource.deleteMessage(
        testMessage.uuid, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isSuccess)
        }

    shadowOf(Looper.getMainLooper()).idle()
    verify {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .delete()
    }
  }

  @Test
  fun `deleteMessage fails when message not found`() {
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.exists() } returns false // Message does not exist

    messageFirestoreSource.deleteMessage(
        testMessage.uuid, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        }
  }

  @Test
  fun `deleteMessage fails when message too old`() {
    val oldTimestamp =
        System.currentTimeMillis() - (15 * 60 * 1000 + 1000) // Sent more than 15 minutes ago

    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getLong("timestamp") } returns oldTimestamp

    messageFirestoreSource.deleteMessage(
        testMessage.uuid, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(
              result.exceptionOrNull()?.message ==
                  "Message can only be deleted within 15 minutes of being sent")
        }
  }

  @Test
  fun `deleteMessage fails on firestore retrieval error`() {
    val exception = RuntimeException("Firestore retrieval error")

    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forException(exception)

    messageFirestoreSource.deleteMessage(
        testMessage.uuid, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        }
  }

  @Test
  fun `deleteMessage fails on firestore delete error`() {
    val timestamp = System.currentTimeMillis() - (15 * 60 * 1000 - 1000) // Within 15 minutes
    val exception = RuntimeException("Firestore delete error")

    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getLong("timestamp") } returns timestamp
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .delete()
    } returns Tasks.forException(exception)

    messageFirestoreSource.deleteMessage(
        testMessage.uuid, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        }
  }

  @Test
  fun `deleteMessage fails when conversion fails`() {
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)

    every { mockDocumentSnapshot.getString("uuid") } returns
        null // Missing field causes conversion failure

    messageFirestoreSource.deleteMessage(
        testMessage.uuid, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        }
  }

  // DeleteAllMessages Tests
  @Test
  fun `deleteAllMessages deletes messages successfully`() {
    val documents = listOf(mockDocumentSnapshot)
    val mockBatch: WriteBatch = mockk()

    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages").get()
    } returns Tasks.forResult(mockQuerySnapshot)
    every { mockQuerySnapshot.documents } returns documents
    every { mockFirestore.batch() } returns mockBatch

    documents.forEach { every { mockBatch.delete(it.reference) } returns mockBatch }
    every { mockBatch.commit() } returns Tasks.forResult(null)

    messageFirestoreSource.deleteAllMessages(testMessage.senderUUID, testMessage.receiverUUID) {
        result ->
      assert(result.isSuccess)
    }

    shadowOf(Looper.getMainLooper()).idle()
    verify { mockBatch.commit() }
  }

  @Test
  fun `deleteAllMessages returns success when no messages to delete`() {
    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages").get()
    } returns Tasks.forResult(mockQuerySnapshot)
    every { mockQuerySnapshot.documents } returns emptyList() // No messages found

    messageFirestoreSource.deleteAllMessages(testMessage.senderUUID, testMessage.receiverUUID) {
        result ->
      assert(result.isSuccess)
    }
  }

  @Test
  fun `deleteAllMessages fails when firestore retrieval error`() {
    val exception = RuntimeException("Firestore retrieval error")

    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages").get()
    } returns Tasks.forException(exception)

    messageFirestoreSource.deleteAllMessages(testMessage.senderUUID, testMessage.receiverUUID) {
        result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  @Test
  fun `deleteAllMessages fails when batch commit error`() {
    val documents = listOf(mockDocumentSnapshot)
    val mockBatch: WriteBatch = mockk()
    val exception = RuntimeException("Batch commit error")

    every {
      mockFirestore.collection(COLLECTION_PATH).document(chatPath).collection("messages").get()
    } returns Tasks.forResult(mockQuerySnapshot)
    every { mockQuerySnapshot.documents } returns documents
    every { mockFirestore.batch() } returns mockBatch

    documents.forEach { every { mockBatch.delete(it.reference) } returns mockBatch }
    every { mockBatch.commit() } returns Tasks.forException(exception)

    messageFirestoreSource.deleteAllMessages(testMessage.senderUUID, testMessage.receiverUUID) {
        result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  // UpdateMessage Tests

  @Test
  fun `updateMessage fails when message not found`() {
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.exists() } returns false

    messageFirestoreSource.updateMessage(
        testMessage, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        }
  }

  @Test
  fun `updateMessage fails when message too old`() {
    val oldMessage =
        testMessage.copy(
            timestamp =
                System.currentTimeMillis() - (15 * 60 * 1000 + 1000) // Older than 15 minutes
            )

    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getLong("timestamp") } returns oldMessage.timestamp

    messageFirestoreSource.updateMessage(
        oldMessage, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(
              result.exceptionOrNull()?.message ==
                  "Message can only be updated within 15 minutes of being sent")
        }
  }

  @Test
  fun `updateMessage fails on firestore retrieval error`() {
    val exception = RuntimeException("Firestore retrieval error")

    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forException(exception)

    messageFirestoreSource.updateMessage(
        testMessage, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        }
  }

  @Test
  fun `updateMessage fails on firestore update error`() {
    val recentMessage =
        testMessage.copy(
            timestamp = System.currentTimeMillis() - (15 * 60 * 1000 - 1000) // Within 15 minutes
            )
    val updatedFields =
        mapOf(
            "text" to recentMessage.text,
            "timestamp" to System.currentTimeMillis(),
            "messageType" to recentMessage.messageType.name)
    val exception = RuntimeException("Firestore update error")

    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getLong("timestamp") } returns recentMessage.timestamp
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .update(updatedFields)
    } returns Tasks.forException(exception)

    messageFirestoreSource.updateMessage(
        recentMessage, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        }
  }

  @Test
  fun `updateMessage fails when conversion fails`() {
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .document(testMessage.uuid.toString())
          .get()
    } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getString("uuid") } returns
        null // Missing field causes conversion failure

    messageFirestoreSource.updateMessage(
        testMessage, testMessage.senderUUID, testMessage.receiverUUID) { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        }
  }

  @Test
  fun `documentToMessage returns dataMessage on success`() {

    val result = documentToMessage(mockDocumentSnapshot)

    assert(result.isSuccess)
    val message = result.getOrNull()
    assert(message != null)
    assert(message?.uuid == testMessage.uuid)
    assert(message?.text == testMessage.text)
    assert(message?.senderUUID == testMessage.senderUUID)
    assert(message?.receiverUUID == testMessage.receiverUUID)
    assert(message?.timestamp == testMessage.timestamp)
    assert(message?.messageType == testMessage.messageType)
  }

  @Test
  fun `documentToMessage returns failure when field is missing`() {
    every { mockDocumentSnapshot.getString("text") } returns null // Missing field

    val result = documentToMessage(mockDocumentSnapshot)

    assert(result.isFailure)
    assert(result.exceptionOrNull() is Exception)
  }

  @Test
  fun `addMessagesListener receives new messages on success`() {
    val snapshot: QuerySnapshot = mockk()
    val listenerRegistration: ListenerRegistration = mockk()

    // Mock Firestore behavior
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .addSnapshotListener(any<EventListener<QuerySnapshot>>())
    } answers
        {
          val listener = arg<EventListener<QuerySnapshot>>(0)
          listener.onEvent(snapshot, null) // Simulate successful snapshot
          listenerRegistration
        }

    every { snapshot.documents } returns listOf(mockDocumentSnapshot)
    every { mockDocumentSnapshot.exists() } returns true

    messageFirestoreSource.addMessagesListener(testMessage.receiverUUID, testMessage.senderUUID) {
        result ->
      assertTrue(result.isSuccess)
      val messages = result.getOrNull()
      assertNotNull(messages)
      assertEquals(1, messages?.size)
      assertEquals(testMessage, messages?.first())
    }

    verify {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .addSnapshotListener(any<EventListener<QuerySnapshot>>())
    }
  }

  @Test
  fun `addMessagesListener returns failure on error`() {
    val exception =
        FirebaseFirestoreException("Listener error", FirebaseFirestoreException.Code.ABORTED)
    val listenerRegistration: ListenerRegistration = mockk()

    // Mock Firestore behavior
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .addSnapshotListener(any<EventListener<QuerySnapshot>>())
    } answers
        {
          val listener = arg<EventListener<QuerySnapshot>>(0)
          listener.onEvent(null, exception) // Simulate an error
          listenerRegistration
        }

    messageFirestoreSource.addMessagesListener(testMessage.receiverUUID, testMessage.senderUUID) {
        result ->
      assertTrue(result.isFailure)
      assertEquals(exception, result.exceptionOrNull())
    }

    verify {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .addSnapshotListener(any<EventListener<QuerySnapshot>>())
    }
  }

  @Test
  fun `addMessagesListener returns empty list on empty snapshot`() {
    val snapshot: QuerySnapshot = mockk()
    val listenerRegistration: ListenerRegistration = mockk()

    // Mock Firestore behavior
    every {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .addSnapshotListener(any<EventListener<QuerySnapshot>>())
    } answers
        {
          val listener = arg<EventListener<QuerySnapshot>>(0)
          listener.onEvent(snapshot, null) // Simulate empty snapshot
          listenerRegistration
        }

    every { snapshot.documents } returns emptyList() // Simulate no documents

    messageFirestoreSource.addMessagesListener(testMessage.receiverUUID, testMessage.senderUUID) {
        result ->
      assertTrue(result.isSuccess)
      val messages = result.getOrNull()
      assertNotNull(messages)
      assertTrue(messages?.isEmpty() == true)
    }

    verify {
      mockFirestore
          .collection(COLLECTION_PATH)
          .document(chatPath)
          .collection("messages")
          .addSnapshotListener(any<EventListener<QuerySnapshot>>())
    }
  }
}

private fun mergeUUIDsForTest(uuid1: UUID, uuid2: UUID): String {
  return if (uuid1.toString() < uuid2.toString()) {
    "${uuid1}_$uuid2"
  } else {
    "${uuid2}_$uuid1"
  }
}
