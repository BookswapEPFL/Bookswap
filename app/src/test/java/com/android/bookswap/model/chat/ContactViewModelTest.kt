package com.android.bookswap.model.chat

import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.UserViewModel
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Before
import org.junit.Test

class ContactViewModelTest {

  private lateinit var userVM: UserViewModel
  private lateinit var messageFirestoreSource: MessageFirestoreSource
  private lateinit var userFirestoreSource: UserFirestoreSource

  @Before
  fun setUp() {
    userVM = mockk()
    messageFirestoreSource = mockk()
    userFirestoreSource = mockk()

    // Mock the behavior of getUser() in userVM
    every { userVM.getUser() } returns user

    // Mock the behavior of getMessages in messageFirestoreSource
    every { messageFirestoreSource.getMessages(any(), any(), any()) } answers
        {
          val user1UUID = it.invocation.args[0] as UUID
          val user2UUID = it.invocation.args[1] as UUID
          val callback = it.invocation.args[2] as (Result<List<DataMessage>>) -> Unit

          // Filter messages between the two users
          val filteredMessages =
              messagesDB.filter { message ->
                (message.senderUUID == user1UUID && message.receiverUUID == user2UUID) ||
                    (message.senderUUID == user2UUID && message.receiverUUID == user1UUID)
              }
          callback(Result.success(filteredMessages))
        }

    // Mock the behavior of getUser in userFirestoreSource
    every { userFirestoreSource.getUser(uuid = any(), any()) } answers
        {
          val userUUID = it.invocation.args[0] as UUID
          val callback = it.invocation.args[1] as (Result<DataUser>) -> Unit
          val user = usersDB.find { it.userUUID == userUUID }
          callback(Result.success(user ?: DataUser(UUID.randomUUID())))
        }
  }

  @Test
  fun `updateMessageBoxMap should update messageBoxMap`() {
    // Instantiate the ContactViewModel with mocked dependencies
    val contactViewModel = ContactViewModel(userVM, userFirestoreSource, messageFirestoreSource)

    // Call the method to update the message box map
    contactViewModel.updateMessageBoxMap()

    // Wait for the async operations to complete (or use proper coroutine testing tools if
    // applicable)
    Thread.sleep(1000) // Placeholder: Replace with proper handling for async tests

    // Print the current state of the messageBoxMap for debugging
    println(contactViewModel.messageBoxMap.value)

    // Assertions to verify the message box map is updated with correct messages
    assert(contactViewModel.messageBoxMap.value[listOfUUIDs[1]]?.message == "Namaste")
    assert(contactViewModel.messageBoxMap.value[listOfUUIDs[2]]?.message == "Hola")
  }

  // UUIDs to simulate users and contacts
  val listOfUUIDs: List<UUID> =
      listOf(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
      )

  // Mock user data for testing
  val user = DataUser(userUUID = listOfUUIDs[0], contactList = listOfUUIDs.drop(1))

  // Mock users in the database (this simulates the users repository)
  val usersDB: List<DataUser> =
      listOf(
          DataUser(userUUID = listOfUUIDs[1], firstName = "Alice"),
          DataUser(userUUID = listOfUUIDs[2], firstName = "Bob"),
      )

  // Mock messages in the database (this simulates the message repository)
  val messagesDB: List<DataMessage> =
      listOf(
          DataMessage(
              senderUUID = listOfUUIDs[0],
              receiverUUID = listOfUUIDs[1],
              text = "Hello",
              timestamp = 1L),
          DataMessage(
              senderUUID = listOfUUIDs[1],
              receiverUUID = listOfUUIDs[0],
              text = "Hi",
              timestamp = 2L),
          DataMessage(
              senderUUID = listOfUUIDs[0],
              receiverUUID = listOfUUIDs[2],
              text = "Hey",
              timestamp = 3L),
          DataMessage(
              senderUUID = listOfUUIDs[2],
              receiverUUID = listOfUUIDs[0],
              text = "Hola",
              timestamp = 4L),
          DataMessage(
              senderUUID = listOfUUIDs[0],
              receiverUUID = listOfUUIDs[1],
              text = "Guten Tag",
              timestamp = 7L),
          DataMessage(
              senderUUID = listOfUUIDs[1],
              receiverUUID = listOfUUIDs[0],
              text = "Namaste",
              timestamp = 8L),
      )
}
