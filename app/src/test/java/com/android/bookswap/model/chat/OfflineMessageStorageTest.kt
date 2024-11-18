package com.android.bookswap.model.chat

import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

class OfflineMessageStorageTest {
    private val mockFirestore: FirebaseFirestore = mockk()
    private val mockQuerySnapshot: QuerySnapshot = mockk()
    private lateinit var offlineMessageStorage: OfflineMessageStorage

    private val testMessages = List(12) {
        DataMessage(
            messageType = if (it % 2 == 0) MessageType.TEXT else MessageType.IMAGE,
            uuid = UUID.randomUUID(),
            text = "Test message $it",
            senderUUID = UUID.randomUUID(),
            receiverUUID = UUID.randomUUID(),
            timestamp = System.currentTimeMillis() - it * 1000L
        )
    }

    @Before
    fun setup() {
        val file = File("MessagesTest.txt")
        file.writeText("")

        offlineMessageStorage = OfflineMessageStorage(file.path, mockFirestore)

        val mockCollectionReference = mockk<CollectionReference>()
        val mockQuery = mockk<Query>()

        every { mockFirestore.collection(any()) } returns mockCollectionReference
        every { mockCollectionReference.orderBy(any<String>(), any<Query.Direction>()) } returns mockQuery
        every { mockQuery.whereEqualTo(any<String>(), any()) } returns mockQuery
        every { mockQuery.limit(any()) } returns mockQuery
        every { mockQuery.get() } returns Tasks.forResult(mockQuerySnapshot)
        every { mockQuerySnapshot.documents } returns testMessages.map { mockk {
            every { toObject(DataMessage::class.java) } returns it
        }}
    }

    @Test
    fun `addMessage adds a message to the internal list`() {
        val message = testMessages[0]
        offlineMessageStorage.addMessage(message)
        assertTrue(offlineMessageStorage.getMessages().contains(message))
    }

    @Test
    fun `getMessages returns the correct list of messages`() {
        testMessages.forEach { offlineMessageStorage.addMessage(it) }
        val messages = offlineMessageStorage.getMessages()
        assertEquals(testMessages.size, messages.size)
        assertTrue(messages.containsAll(testMessages))
    }

    @Test
    fun `setMessages writes messages to file`() {
        testMessages.forEach { offlineMessageStorage.addMessage(it) }
        offlineMessageStorage.setMessages()
        val messagesFromFile = offlineMessageStorage.getMessagesFromText()
        assertTrue(messagesFromFile.containsAll(testMessages))
    }

    @Test
    fun `getMessagesFromText reads messages from file`() {
        testMessages.forEach { offlineMessageStorage.addMessage(it) }
        offlineMessageStorage.setMessages()
        val messagesFromFile = offlineMessageStorage.getMessagesFromText()
        assertTrue(messagesFromFile.containsAll(testMessages))
    }

    @Test
    fun `clearMessages clears internal list`() {
        testMessages.forEach { offlineMessageStorage.addMessage(it) }
        offlineMessageStorage.clearMessages()
        assertTrue(offlineMessageStorage.getMessages().isEmpty())
    }

    @Test
    fun `retrieveMessagesFromFirestore retrieves and stores messages`() {
        every { mockQuerySnapshot.documents } returns testMessages.map { mockk {
            every { toObject(DataMessage::class.java) } returns it
        }}

        offlineMessageStorage.retrieveMessagesFromFirestore { result ->
            assertTrue(result.isSuccess)
            val messages = result.getOrNull()
            assertNotNull(messages)
            assertTrue(messages!!.containsAll(testMessages))
        }
    }
}
