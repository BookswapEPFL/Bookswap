package com.android.bookswap.model.chat

import android.content.Context
import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.crypto.SecretKey
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class OfflineMessageStorageTest {
  private val mockFirestore: FirebaseFirestore = mockk()
  private val mockQuerySnapshot: QuerySnapshot = mockk()
  private lateinit var offlineMessageStorage: OfflineMessageStorage
  private lateinit var context: Context
  private lateinit var tempDir: File

  private val testMessages =
      List(12) {
        DataMessage(
            messageType = if (it % 2 == 0) MessageType.TEXT else MessageType.IMAGE,
            uuid = UUID.randomUUID(),
            text = "Test message $it",
            senderUUID = UUID.randomUUID(),
            receiverUUID = UUID.randomUUID(),
            timestamp = System.currentTimeMillis() - it * 1000L)
      }

  @Before
  fun setup() {
    // Create a temporary directory for tests
    tempDir = createTempDir()
    context = mockk()

    // Mock context to return the temporary directory
    every { context.filesDir } returns tempDir

    offlineMessageStorage = OfflineMessageStorage(context)

    val mockCollectionReference = mockk<CollectionReference>()
    val mockQuery = mockk<Query>()

    every { mockFirestore.collection(any()) } returns mockCollectionReference
    every { mockCollectionReference.orderBy(any<String>(), any<Query.Direction>()) } returns
        mockQuery
    every { mockQuery.whereEqualTo(any<String>(), any()) } returns mockQuery
    every { mockQuery.limit(any()) } returns mockQuery
    every { mockQuery.get() } returns Tasks.forResult(mockQuerySnapshot)
    every { mockQuerySnapshot.documents } returns
        testMessages.map { mockk { every { toObject(DataMessage::class.java) } returns it } }
  }

  @Test
  fun `generateKey generates valid AES key`() {
    val key: SecretKey = offlineMessageStorage.generateKey()
    assertNotNull(key)
    assertEquals("AES", key.algorithm)
    assertEquals(256 / 8, key.encoded.size) // AES-256 key size in bytes
  }

  @Test
  fun `encrypt encrypts data correctly`() {
    val plainText = "Test encryption data"
    val encryptedData = offlineMessageStorage.encrypt(plainText)

    // Ensure encrypted data is not empty and different from the plain text
    assertTrue(encryptedData.isNotEmpty())
    assertNotEquals(plainText, String(encryptedData, Charsets.UTF_8))

    // Ensure the encrypted data is not directly readable as plain text
    val encryptedString = String(encryptedData, Charsets.UTF_8)
    assertFalse(encryptedString.contains(plainText))
  }

  @Test
  fun `decrypt decrypts data correctly`() {
    val plainText = "Test decryption data"
    val encryptedData = offlineMessageStorage.encrypt(plainText)
    val decryptedText = offlineMessageStorage.decrypt(encryptedData)

    // Ensure the decrypted text matches the original plain text
    assertEquals(plainText, decryptedText)
  }

  @Test
  fun `encrypt and decrypt together maintain data integrity`() {
    val plainText = "Full cycle encryption and decryption test"
    val encryptedData = offlineMessageStorage.encrypt(plainText)
    val decryptedText = offlineMessageStorage.decrypt(encryptedData)

    // Ensure the decrypted text matches the original plain text
    assertEquals(plainText, decryptedText)

    // Ensure the encrypted data is different from the original plain text
    assertNotEquals(plainText, String(encryptedData, Charsets.UTF_8))
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
  fun `getMessagesFromText returns empty list if message file does not exist`() {
    // Ensure the message file does not exist
    val messagesFile = File(tempDir, "Messages.txt")
    if (messagesFile.exists()) {
      messagesFile.delete()
    }

    // Call getMessagesFromText and verify it returns an empty list
    val messages = offlineMessageStorage.getMessagesFromText()
    assertTrue(messages.isEmpty())
  }

  @Test
  fun `setMessages writes encrypted messages to file`() {
    testMessages.forEach { offlineMessageStorage.addMessage(it) }
    offlineMessageStorage.setMessages()

    val messagesFile = File(tempDir, "Messages.txt")
    assertTrue(messagesFile.exists())
    val encryptedData = messagesFile.readBytes()
    assertTrue(encryptedData.isNotEmpty())
    assertFalse(String(encryptedData).contains("Test message"))
  }

  @Test
  fun `setMessages handles failure gracefully`() {
    testMessages.forEach { offlineMessageStorage.addMessage(it) }

    // Simulate a failure by making the file read-only
    val messagesFile = File(tempDir, "Messages.txt")
    messagesFile.createNewFile()
    messagesFile.setReadOnly()

    try {
      offlineMessageStorage.setMessages()
      fail("Expected IOException to be thrown")
    } catch (e: IOException) {
      assertTrue(e.message?.contains("Failed to write messages to file") == true)
    }
  }

  @Test
  fun `getMessagesFromText decrypts messages from file`() {
    testMessages.forEach { offlineMessageStorage.addMessage(it) }
    offlineMessageStorage.setMessages()
    val messagesFromFile = offlineMessageStorage.getMessagesFromText()
    assertEquals(testMessages.size, messagesFromFile.size)
    assertTrue(messagesFromFile.containsAll(testMessages))
  }

  @Test
  fun `clearMessages clears internal list`() {
    testMessages.forEach { offlineMessageStorage.addMessage(it) }
    offlineMessageStorage.clearMessages()
    assertTrue(offlineMessageStorage.getMessages().isEmpty())
  }

  @Test
  fun `extractMessages returns last 10 TEXT messages and IMAGE messages in between`() {
    val limit = 10
    val shuffledMessages = testMessages.shuffled()

    val extractedMessages =
        offlineMessageStorage.extractMessages(shuffledMessages.toMutableList(), limit)

    val sortedMessages = shuffledMessages.sortedByDescending { it.timestamp }
    val expectedMessages = mutableListOf<DataMessage>()
    var textCount = 0

    for (message in sortedMessages) {
      if (message.messageType == MessageType.TEXT && textCount < limit) {
        expectedMessages.add(message)
        textCount++
      } else if (message.messageType == MessageType.IMAGE) {
        expectedMessages.add(message)
      }
      if (textCount == limit) break
    }

    assertEquals(expectedMessages.size, extractedMessages.size)
    assertTrue(expectedMessages.containsAll(extractedMessages))
  }
}
