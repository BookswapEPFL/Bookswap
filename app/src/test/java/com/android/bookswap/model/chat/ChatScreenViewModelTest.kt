package com.android.bookswap.model.chat

import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import org.junit.Before
import org.junit.Test

class ChatScreenViewModelTest {

  private lateinit var chatScreenViewModel: ChatScreenViewModel
  private val currentUser =
      DataUser(
          UUID.randomUUID(),
          "Hello",
          "Jaime",
          "Oliver Pastor",
          "",
          "",
          0.0,
          0.0,
          "",
          emptyList(),
          "")
  private val otherUser =
      DataUser(UUID.randomUUID(), "Hey", "Matias", "Salvade", "", "", 0.0, 0.0, "", emptyList(), "")

  @MockK lateinit var mockUsersRepository: UsersRepository

  @Before
  fun setup() {
    chatScreenViewModel = ChatScreenViewModel()
    MockKAnnotations.init(this)
  }

  @Test
  fun testFormatTimeStamps() {
    val timestamp = System.currentTimeMillis()
    val formattedTimestamp = chatScreenViewModel.formatTimestamp(timestamp)
    val expectedTimestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    assert(formattedTimestamp == expectedTimestamp)
  }

  @Test
  fun `addContacts adds users to each other's contact lists on success`() {
    every { mockUsersRepository.getUser(currentUser.userUUID, any()) } answers
        {
          val callback = secondArg<(Result<DataUser>) -> Unit>()
          callback(Result.success(currentUser))
        }

    every { mockUsersRepository.getUser(otherUser.userUUID, any()) } answers
        {
          val callback = secondArg<(Result<DataUser>) -> Unit>()
          callback(Result.success(otherUser))
        }

    every { mockUsersRepository.addContact(any(), any(), any()) } answers
        {
          val callback = thirdArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }

    chatScreenViewModel.addContacts(mockUsersRepository, currentUser, otherUser)

    verify { mockUsersRepository.addContact(currentUser.userUUID, otherUser.userUUID, any()) }
    verify { mockUsersRepository.addContact(otherUser.userUUID, currentUser.userUUID, any()) }
  }

  @Test
  fun `addContacts fails when adding to other user's contacts`() {
    every { mockUsersRepository.getUser(currentUser.userUUID, any()) } answers
        {
          val callback = secondArg<(Result<DataUser>) -> Unit>()
          callback(Result.success(currentUser))
        }

    every { mockUsersRepository.getUser(otherUser.userUUID, any()) } answers
        {
          val callback = secondArg<(Result<DataUser>) -> Unit>()
          callback(Result.success(otherUser))
        }

    every {
      mockUsersRepository.addContact(currentUser.userUUID, otherUser.userUUID, any())
    } answers
        {
          val callback = thirdArg<(Result<Unit>) -> Unit>()
          callback(Result.success(Unit))
        }

    every {
      mockUsersRepository.addContact(otherUser.userUUID, currentUser.userUUID, any())
    } answers
        {
          val callback = thirdArg<(Result<Unit>) -> Unit>()
          callback(Result.failure(Exception("Add contact failed for other user")))
        }

    chatScreenViewModel.addContacts(mockUsersRepository, currentUser, otherUser)

    verify { mockUsersRepository.addContact(currentUser.userUUID, otherUser.userUUID, any()) }
    verify { mockUsersRepository.addContact(otherUser.userUUID, currentUser.userUUID, any()) }
  }
}
