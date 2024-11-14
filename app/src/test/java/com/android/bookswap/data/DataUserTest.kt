package com.android.bookswap.data

import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.model.UserViewModel
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataUserTest {

  private val mockUsersRepo: UsersRepository = mockk()

  private val standardUser =
      DataUser(
          userUUID = UUID.randomUUID(),
          greeting = "M.",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          phoneNumber = "+41223456789",
          latitude = 1.0,
          longitude = 7.0,
          profilePictureUrl = "dummyPic.png",
          bookList = listOf(UUID(1000, 2000)),
          googleUid = "googleUid")

  @Test
  fun checkAssign() {
    assertNotNull(standardUser.userUUID)
    assertEquals("M.", standardUser.greeting)
    assertEquals("John", standardUser.firstName)
    assertEquals("Doe", standardUser.lastName)
    assertEquals("john.doe@example.com", standardUser.email)
    assertEquals("+41223456789", standardUser.phoneNumber)
    assertEquals(1.0, standardUser.latitude, 0.000001)
    assertEquals(7.0, standardUser.longitude, 0.000001)
    assertEquals("dummyPic.png", standardUser.profilePictureUrl)
    assertEquals(listOf(UUID(1000, 2000)), standardUser.bookList)
    assertEquals("googleUid", standardUser.googleUid)
  }
  /**
   * @Test fun viewModelFetch() { val userVM = UserViewModel(standardUser.userUUID, mockUsersRepo)
   *   assertTrue(!userVM.isStored.value!!)
   *
   * every { mockUsersRepo.getUser(standardUser.userUUID, any()) } answers {
   * secondArg<(Result<DataUser>) -> Unit>()(Result.success(standardUser)) } andThenJust Runs
   *
   * val result = userVM.getUser()
   *
   * assertEquals(standardUser, result) assertTrue(userVM.isStored.value!!)
   *
   * // Verify that second calls does not fetch again userVM.getUser() verify(exactly = 1) {
   * mockUsersRepo.getUser(uuid = any(), any()) } }
   */
  @Test
  fun viewModelUpdateCorrectly() {
    val updatedUser =
        standardUser.copy(
            greeting = "Mme.",
            firstName = "Alice",
            email = "alice.doe@example.com",
            longitude = 5.0,
            latitude = 3.2,
            phoneNumber = "+4122346666",
            profilePictureUrl = "zzz",
            bookList = listOf(UUID(10, 5)),
            googleUid = "googleUid")

    every { mockUsersRepo.updateUser(any(), any()) } answers
        {
          secondArg<(Result<Unit>) -> Unit>()(Result.success(Unit))
        }

    val userVM = UserViewModel(standardUser.userUUID, mockUsersRepo)
    userVM.updateUser(
        updatedUser.greeting,
        updatedUser.firstName,
        updatedUser.lastName,
        updatedUser.email,
        updatedUser.phoneNumber,
        updatedUser.latitude,
        updatedUser.longitude,
        updatedUser.profilePictureUrl,
        updatedUser.bookList,
        updatedUser.googleUid)

    assertTrue(userVM.isStored.value!!)

    // Verify it fails correctly
    every { mockUsersRepo.updateUser(any(), any()) } answers
        {
          secondArg<(Result<Unit>) -> Unit>()(Result.failure(Exception()))
        }

    userVM.updateUser(
        updatedUser.greeting,
        updatedUser.firstName,
        updatedUser.lastName,
        updatedUser.email,
        updatedUser.phoneNumber,
        updatedUser.latitude,
        updatedUser.longitude,
        updatedUser.profilePictureUrl,
        updatedUser.bookList,
        updatedUser.googleUid)

    assertTrue(!userVM.isStored.value!!)
  }
}
