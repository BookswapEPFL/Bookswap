package com.android.bookswap.data

import com.android.bookswap.data.repository.UsersRepository
import io.mockk.InternalPlatformDsl.toStr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DataUserTest {
  @Test
  fun checkCreate() {
    val u1 = DataUser()
    assertEquals(u1.greeting, "")
    assertEquals(u1.firstName, "")
    assertEquals(u1.lastName, "")
    assertEquals(u1.email, "")
    assertEquals(u1.phoneNumber, "")
    assertEquals(u1.profilePictureUrl, "")
    assertEquals(u1.userId, "")
    assertEquals(u1.latitude.toStr(), 0.0.toString())
    assertEquals(u1.longitude.toStr(), 0.0.toString())
  }

  @Test
  fun checkAssign() {

    val userVM = com.android.bookswap.model.UserViewModel("", MockUserRepo())


    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "dummyUUID0000"))
    var u1 = userVM.getUser()
    assertEquals(u1.greeting, "M.")
    assertEquals(u1.firstName, "John")
    assertEquals(u1.lastName, "Doe")
    assertEquals(u1.email, "John.Doe@example.com")
    assertEquals(u1.phoneNumber, "+41223456789")
    assertEquals(u1.profilePictureUrl, "dummyPic.png")
    assertEquals(u1.userId, "dummyUUID0000")
    assertEquals(u1.latitude.toStr(), 1.0.toString())
    assertEquals(u1.longitude.toStr(), 7.0.toString())
    userVM.updateUser(greeting = "Mr.")
    u1 = userVM.getUser()
    assertNotEquals(u1.greeting, "M.")
    assertEquals(u1.greeting, "Mr.")
    assertEquals(u1.firstName, "John")
    assertEquals(u1.lastName, "Doe")
    assertEquals(u1.email, "John.Doe@example.com")
    assertEquals(u1.phoneNumber, "+41223456789")
    assertEquals(u1.profilePictureUrl, "dummyPic.png")
    assertEquals(u1.userId, "dummyUUID0000")
  }

  @Test
  fun checkString() {
    val userVM = com.android.bookswap.model.UserViewModel("", MockUserRepo())

    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            0.0,
            0.0,
            "dummyPic.png",
            "dummyUUID0000"))
    val u1 = userVM.getUser()
    assertEquals("M. John Doe", u1.printFullname())
  }
}

class MockUserRepo : UsersRepository {
  val mockUserList =
      mutableMapOf(
          "usr_01_jd" to
              DataUser(
                  "M.",
                  "John",
                  "Doe",
                  "john.doe@example.com",
                  "+41223456789",
                  0.0,
                  0.0,
                  "",
                  "usr_01_jd"),
          "usr_02_jd" to
              DataUser(
                  "Mr.",
                  "Jones",
                  "Douse",
                  "jon.doe@example.com",
                  "+41234567890",
                  0.0,
                  0.0,
                  "",
                  "usr_02_jd"),
          "usr_03_jd" to
              DataUser(
                  "Ms.",
                  "Jo",
                  "Doe",
                  "jo.doe@example.com",
                  "+41765432198",
                  0.0,
                  0.0,
                  "",
                  "usr_03_jd"),
      )

  override fun init(callback: (Result<Unit>) -> Unit) {
    TODO("Not yet implemented")
  }

  override fun getUsers(callback: (Result<List<DataUser>>) -> Unit) {
    callback(Result.success(mockUserList.values.toList()))
  }

  override fun getUser(uuid: String, callback: (Result<DataUser>) -> Unit) {
    val usr = mockUserList.get(uuid)
    if (usr != null) {
      callback(Result.success(usr))
    } else {
      callback(Result.failure(Throwable("User not found")))
    }
  }

  override fun addUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    mockUserList.put(dataUser.userId, dataUser)
  }

  override fun updateUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    mockUserList.put(dataUser.userId, dataUser)
  }

  override fun deleteUser(uuid: String, callback: (Result<Unit>) -> Unit) {
    mockUserList.remove(uuid)
  }
}
