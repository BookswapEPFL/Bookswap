package com.android.bookswap.model.chat

import android.content.Context
import android.os.Looper
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.documentToMessage
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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

@RunWith(RobolectricTestRunner::class)
class DataMessageFirestoreSourceTest {

  private val mockFirestore: FirebaseFirestore = mockk()
  private val mockCollectionReference: CollectionReference = mockk()
  private val mockDocumentReference: DocumentReference = mockk()
  private val mockDocumentSnapshot: DocumentSnapshot = mockk()
  private val mockQuerySnapshot: QuerySnapshot = mockk()
  private val mockContext: Context = mockk()

  private lateinit var messageRepository: MessageFirestoreSource
  private lateinit var messageFirestoreSource: MessageFirestoreSource

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
    messageFirestoreSource = MessageFirestoreSource(mockFirestore)
    messageRepository = MessageFirestoreSource(mockFirestore)
    every { mockFirestore.collection(any()) } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference
    every { mockCollectionReference.get() } returns Tasks.forResult(mockQuerySnapshot)

    // Arrange snapshot
    every { mockDocumentSnapshot.getString("uuid") } returns testMessage.uuid.toString()
    every { mockDocumentSnapshot.getString("text") } returns testMessage.text
    every { mockDocumentSnapshot.getString("senderUUID") } returns testMessage.senderUUID.toString()
    every { mockDocumentSnapshot.getString("receiverUUID") } returns
        testMessage.receiverUUID.toString()
    every { mockDocumentSnapshot.getLong("timestamp") } returns testMessage.timestamp
    every { mockDocumentSnapshot.getString("messageType") } returns testMessage.messageType.name
    every { mockDocumentSnapshot.exists() } returns true
  }

  // Init Tests
  @Test
  fun `init invokes callback onSuccess`() {
    messageFirestoreSource.init { result -> assert(result.isSuccess) }
  }

  // GetMessages Tests
  @Test
  fun `getMessages returns messages onSuccess`() {
    // Act
    messageFirestoreSource.getMessages { result ->
      // Assert
      assertTrue(result.isSuccess)
      val messages = result.getOrNull()
      assertEquals(1, messages?.size)
      assertEquals(testMessage, messages?.first())
    }
  }

  @Test
  fun `getMessages returns Failure on fail`() {
    val exception = RuntimeException("Firestore error")
    every { mockCollectionReference.get() } returns Tasks.forException(exception)

    messageFirestoreSource.getMessages { result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  @Test
  fun `getMessages returns emptyList on empty result`() {
    every { mockQuerySnapshot.documents } returns emptyList()

    messageFirestoreSource.getMessages { result ->
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
            "uuid" to testMessage.uuid.toString(),
            "text" to testMessage.text,
            "senderUUID" to testMessage.senderUUID.toString(),
            "receiverUUID" to testMessage.receiverUUID.toString(),
            "timestamp" to testMessage.timestamp,
            "messageType" to testMessage.messageType.name)
    every { mockDocumentReference.set(messageMap) } returns Tasks.forResult(null)

    messageFirestoreSource.sendMessage(testMessage) { result -> assert(result.isSuccess) }

    verify { mockDocumentReference.set(messageMap) }
  }

  @Test
  fun `sendMessage calls firestore set on failure`() {
    val exception = RuntimeException("Firestore error")
    val messageMap =
        mapOf(
            "uuid" to testMessage.uuid.toString(),
            "text" to testMessage.text,
            "senderUUID" to testMessage.senderUUID.toString(),
            "receiverUUID" to testMessage.receiverUUID.toString(),
            "timestamp" to testMessage.timestamp,
            "messageType" to testMessage.messageType.name)
    every { mockDocumentReference.set(messageMap) } returns Tasks.forException(exception)

    messageFirestoreSource.sendMessage(testMessage) { result ->
      assert(result.isFailure)
      assertEquals(exception, result.exceptionOrNull())
    }
  }

  // DeleteMessage Tests
  @Test
  fun `deleteMessage deletesMessage on success`() {
    val fifteenMinutesInMillis = 15 * 60 * 1000
    val timestamp = System.currentTimeMillis() - (fifteenMinutesInMillis - 1000)

    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.exists() } returns true
    every { mockDocumentSnapshot.getLong("timestamp") } returns timestamp
    every { mockDocumentReference.delete() } returns Tasks.forResult(null)

    messageFirestoreSource.deleteMessage(
        testMessage.uuid, { result -> assert(result.isSuccess) }, mockContext)

    shadowOf(Looper.getMainLooper()).idle()

    verify { mockDocumentReference.delete() }
  }

  @Test
  fun `deleteMessage fails when message not found`() {
    every { mockDocumentSnapshot.exists() } returns false // Message does not exist
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    messageFirestoreSource.deleteMessage(
        testMessage.uuid,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
  }

  @Test
  fun `deleteMessage fails when message too old`() {
    val oldMessage =
        testMessage.copy(
            timestamp =
                System.currentTimeMillis() -
                    (15 * 60 * 1000 + 1000)) // Sent more than 15 minutes ago

    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getLong("timestamp") } returns oldMessage.timestamp

    messageFirestoreSource.deleteMessage(
        oldMessage.uuid,
        { result ->
          assert(result.isFailure)
          assert(
              result.exceptionOrNull()?.message ==
                  "Message can only be deleted within 15 minutes of being sent")
        },
        mockContext)
  }

  @Test
  fun `deleteMessage fails on firestore retrival error`() {
    val exception = RuntimeException("Firestore retrieval error")
    every { mockDocumentReference.get() } returns Tasks.forException(exception)

    messageFirestoreSource.deleteMessage(
        testMessage.uuid,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun `deleteMessage fails on firestore delete error`() {
    val exception = RuntimeException("Firestore delete error")
    val recentMessage =
        testMessage.copy(
            timestamp =
                System.currentTimeMillis() - (15 * 60 * 1000 - 1000)) // Sent within 15 minutes

    every { mockDocumentSnapshot.getLong("timestamp") } returns recentMessage.timestamp
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentReference.delete() } returns Tasks.forException(exception)

    messageFirestoreSource.deleteMessage(
        recentMessage.uuid,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun `deleteMessage fails when conversion fails`() {
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)

    every { mockDocumentSnapshot.getString("uuid") } returns
        null // Missing field causes conversion failure

    messageFirestoreSource.deleteMessage(
        testMessage.uuid,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
  }

  // DeleteAllMessages Tests
  @Test
  fun `delete all messages deletes messages successfully`() {
    val documents = listOf(mockDocumentSnapshot)
    val mockBatch: WriteBatch = mockk()

    every {
      mockCollectionReference.whereIn(
          "senderUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every {
      mockCollectionReference.whereIn(
          "receiverUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every { mockCollectionReference.whereNotEqualTo("senderUUID", "receiverUUID") } returns
        mockCollectionReference
    every { mockCollectionReference.get() } returns Tasks.forResult(mockQuerySnapshot)
    every { mockQuerySnapshot.documents } returns documents
    every { mockQuerySnapshot.isEmpty } returns false
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
  fun `delete allMessages returns success when no messages to delete`() {
    every {
      mockCollectionReference.whereIn(
          "senderUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every {
      mockCollectionReference.whereIn(
          "receiverUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every { mockCollectionReference.whereNotEqualTo("senderUUID", "receiverUUID") } returns
        mockCollectionReference

    every { mockCollectionReference.get() } returns Tasks.forResult(mockQuerySnapshot)
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
      mockCollectionReference.whereIn(
          "senderUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every {
      mockCollectionReference.whereIn(
          "receiverUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every { mockCollectionReference.whereNotEqualTo("senderUUID", "receiverUUID") } returns
        mockCollectionReference

    every { mockCollectionReference.get() } returns Tasks.forException(exception)

    messageFirestoreSource.deleteAllMessages(testMessage.senderUUID, testMessage.receiverUUID) {
        result ->
      assert(result.isFailure)
      assert(result.exceptionOrNull() == exception)
    }
  }

  @Test
  fun `deleteAllMessages fails when batch commit error`() {
    val documents = listOf(mockDocumentSnapshot)
    val exception = RuntimeException("Batch commit error")
    val mockBatch: WriteBatch = mockk()

    every {
      mockCollectionReference.whereIn(
          "senderUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every {
      mockCollectionReference.whereIn(
          "receiverUUID", listOf(testMessage.senderUUID, testMessage.receiverUUID))
    } returns mockCollectionReference

    every { mockCollectionReference.whereNotEqualTo("senderUUID", "receiverUUID") } returns
        mockCollectionReference
    every { mockCollectionReference.get() } returns Tasks.forResult(mockQuerySnapshot)
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
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)

    every { mockDocumentSnapshot.exists() } returns false

    messageFirestoreSource.updateMessage(
        testMessage,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
  }

  @Test
  fun `updateMessage fails when message too old`() {
    val oldMessage =
        testMessage.copy(
            timestamp =
                System.currentTimeMillis() -
                    (15 * 60 * 1000 + 1000)) // Sent more than 15 minutes ago
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.getLong("timestamp") } returns oldMessage.timestamp

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
  fun `updateMessage fails on firestore retrieval error`() {
    val exception = RuntimeException("Firestore retrieval error")
    every { mockDocumentReference.get() } returns Tasks.forException(exception)

    messageFirestoreSource.updateMessage(
        testMessage,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun `updateMessage fails on firestore update error`() {
    val exception = RuntimeException("Firestore update error")
    val recentMessage =
        testMessage.copy(timestamp = System.currentTimeMillis() - (15 * 60 * 1000 - 1000))
    val updatedText = "Updated text"
    val messageMap = mapOf("text" to updatedText, "timestamp" to System.currentTimeMillis())
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)

    every { mockDocumentReference.update(messageMap) } returns Tasks.forException(exception)

    messageFirestoreSource.updateMessage(
        recentMessage.copy(text = updatedText),
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull() == exception)
        },
        mockContext)
  }

  @Test
  fun `updateMessage fails when conversion fails`() {
    every { mockDocumentSnapshot.getString("uuid") } returns null
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)

    messageFirestoreSource.updateMessage(
        testMessage,
        { result ->
          assert(result.isFailure)
          assert(result.exceptionOrNull()?.message == "Message not found")
        },
        mockContext)
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
}
