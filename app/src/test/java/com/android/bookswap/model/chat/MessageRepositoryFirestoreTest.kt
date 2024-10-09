package com.android.bookswap.model.chat

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.util.Assert.fail
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
class MessageRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private val testMessage =
      Message(
          id = "message-id",
          text = "Hello, World!",
          senderId = "user-id",
          timestamp = System.currentTimeMillis())

  private lateinit var messageRepository: MessageRepositoryFirestore

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    messageRepository = MessageRepositoryFirestore(mockFirestore)

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
    val onSuccess = mock<() -> Unit>()
    messageRepository.init(onSuccess)
    verify(onSuccess).invoke()
  }

  @Test
  fun getMessages_callsFirestoreGet() {
    // Arrange
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("id")).thenReturn(testMessage.id)
    `when`(mockDocumentSnapshot.getString("text")).thenReturn(testMessage.text)
    `when`(mockDocumentSnapshot.getString("senderId")).thenReturn(testMessage.senderId)
    `when`(mockDocumentSnapshot.getLong("timestamp")).thenReturn(testMessage.timestamp)

    // Act
    messageRepository.getMessages(
        onSuccess = { messages ->
          // Assert
          assert(messages.isNotEmpty())
          assert(messages.first().id == testMessage.id)
        },
        onFailure = { fail("Should not fail") })

    // Verify Firestore collection was called
    verify(mockCollectionReference).get()
  }

  @Test
  fun sendMessage_callsFirestoreSet_andOnSuccess() {
    // Arrange
    val messageMap =
        mapOf(
            "id" to testMessage.id,
            "text" to testMessage.text,
            "senderId" to testMessage.senderId,
            "timestamp" to testMessage.timestamp)

    doAnswer { Tasks.forResult(null) }.`when`(mockDocumentReference).set(messageMap)

    // Act
    messageRepository.sendMessage(
        testMessage,
        onSuccess = {
          // Assert success callback
          assert(true)
        },
        onFailure = { fail("Should not fail") })

    // Verify Firestore set operation
    verify(mockDocumentReference).set(messageMap)
  }
}
