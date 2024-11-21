package com.android.bookswap.model.chat

import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.MessageBox
import com.android.bookswap.data.source.network.MessageFirestoreSource
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.android.bookswap.model.UserViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ContactViewModelTest {

    private lateinit var userVM: UserViewModel
    private lateinit var messageFirestoreSource: MessageFirestoreSource
    private lateinit var userFirestoreSource: UserFirestoreSource

    // This method sets up the necessary mocks and prepares the test environment
    @Before
    fun setUp() {
        userVM = mockk()
        messageFirestoreSource = mockk()
        userFirestoreSource = mockk()

        // Mocking the behavior of getUser() method in the userVM
        every { userVM.getUser() } returns user

        // Mocking the repository calls for messages and user data
        every { messageFirestoreSource.getMessages(any()) } answers {
            val callback = it.invocation.args[0] as (Result<List<DataMessage>>) -> Unit
            callback(Result.success(messagesDB))
        }
        every { userFirestoreSource.getUser(uuid = any(), any()) } answers {
            val userUUID = it.invocation.args[0] as UUID
            val callback = it.invocation.args[1] as (Result<DataUser>) -> Unit
            val user = usersDB.find { it.userUUID == userUUID }
            callback(Result.success(user ?: DataUser(UUID.randomUUID())))
        }
    }

    // This test verifies if the messageBoxMap is correctly updated after calling updateMessageBoxMap
    @Test
    fun `updateMessageBoxMap should update messageBoxMap`() {
        // Instantiate the ContactViewModel with mocked dependencies
        val contactViewModel = ContactViewModel(userVM, userFirestoreSource, messageFirestoreSource)

        // Call the method to update the message box map
        contactViewModel.updateMessageBoxMap()

        // Wait for the async operations to complete (not needed if we are using coroutines with proper Dispatchers)
        Thread.sleep(1000) // Placeholder: You should ideally handle async operations better in tests

        // Print the current state of the messageBoxMap for debugging
        println(contactViewModel.messageBoxMap.value)

        // Assertions to check if the message box map is updated with the correct messages
        assert(contactViewModel.messageBoxMap.value[listOfUUIDs[1]]?.message == "Namaste")
        assert(contactViewModel.messageBoxMap.value[listOfUUIDs[2]]?.message == "Hola")
    }

    // UUIDs to simulate users and contacts
    val listOfUUIDs: List<UUID> = listOf(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
    )

    // Mock user data for testing
    val user = DataUser(userUUID = listOfUUIDs[0], contactList = listOfUUIDs.drop(1).map { it.toString() })

    // Mock users in the database (this simulates the users repository)
    val usersDB: List<DataUser> = listOf(
        DataUser(userUUID = listOfUUIDs[1], firstName = "Alice"),
        DataUser(userUUID = listOfUUIDs[2], firstName = "Bob"),
    )

    // Mock messages in the database (this simulates the message repository)
    val messagesDB: List<DataMessage> = listOf(
        DataMessage(senderUUID = listOfUUIDs[0], receiverUUID = listOfUUIDs[1], text = "Hello", timestamp = 1L),
        DataMessage(senderUUID = listOfUUIDs[1], receiverUUID = listOfUUIDs[0], text = "Hi", timestamp = 2L),
        DataMessage(senderUUID = listOfUUIDs[0], receiverUUID = listOfUUIDs[2], text = "Hey", timestamp = 3L),
        DataMessage(senderUUID = listOfUUIDs[2], receiverUUID = listOfUUIDs[0], text = "Hola", timestamp = 4L),
        DataMessage(senderUUID = listOfUUIDs[0], receiverUUID = listOfUUIDs[1], text = "Guten Tag", timestamp = 7L),
        DataMessage(senderUUID = listOfUUIDs[1], receiverUUID = listOfUUIDs[0], text = "Namaste", timestamp = 8L),
    )
}