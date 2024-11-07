package com.android.bookswap.data

import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.model.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class DataUserTest {

  private lateinit var userVM: UserViewModel

  @Before
  fun setUp() {
    userVM = UserViewModel("usr_01_jd", MockUserRepo())
  }

  @Test
  fun checkCreate() {
    val u1 = DataUser()
    assertEquals("", u1.greeting)
    assertEquals("", u1.firstName)
    assertEquals("", u1.lastName)
    assertEquals("", u1.email)
    assertEquals("", u1.phoneNumber)
    assertEquals(0.0, u1.latitude, 0.000001)
    assertEquals(0.0, u1.longitude, 0.000001)
    assertEquals("", u1.profilePictureUrl)
    assertEquals("", u1.userUUID)
  }

  @Test
  fun checkAssign() {
    val u1 =
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd")

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_ExistingUser() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    val u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    assert(userVM.isStored)
  }

  @Test
  fun checkUpdate_NotExistentUser() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_04_jd"))

    val u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_04_jd", u1.userUUID)

    assert(!userVM.isStored)
  }

  @Test
  fun checkUpdate_greeting() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(greeting = "Mr.")
    u1 = userVM.getUser(true)

    assertNotEquals("M.", u1.greeting)
    assertEquals("Mr.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_firstName() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(firstName = "Joe")
    u1 = userVM.getUser(true)

    assertEquals("M.", u1.greeting)
    assertNotEquals("John", u1.firstName)
    assertEquals("Joe", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_lastName() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(lastName = "Douse")
    u1 = userVM.getUser(true)

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertNotEquals("Doe", u1.lastName)
    assertEquals("Douse", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_email() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(email = "john.doe@example.swiss")
    u1 = userVM.getUser(true)

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertNotEquals("john.doe@example.com", u1.email)
    assertEquals("john.doe@example.swiss", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_phoneNumber() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(phone = "+41234567890")
    u1 = userVM.getUser(true)

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertNotEquals("+41223456789", u1.phoneNumber)
    assertEquals("+41234567890", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_latitude() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(latitude = 2.7)
    u1 = userVM.getUser(true)

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertNotEquals(1.0, u1.latitude)
    assertEquals(2.7, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_longitude() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(longitude = 6.3)
    u1 = userVM.getUser(true)

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertNotEquals(7.0, u1.longitude)
    assertEquals(6.3, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkUpdate_profilePictureURI() {
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "john.doe@example.com",
            "+41223456789",
            1.0,
            7.0,
            "dummyPic.png",
            "usr_01_jd"))

    var u1 = userVM.getUser()

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)

    userVM.updateUser(picURL = "pl4c3h0ld3rP1c.jpg")
    u1 = userVM.getUser(true)

    assertEquals("M.", u1.greeting)
    assertEquals("John", u1.firstName)
    assertEquals("Doe", u1.lastName)
    assertEquals("john.doe@example.com", u1.email)
    assertEquals("+41223456789", u1.phoneNumber)
    assertEquals(1.0, u1.latitude, 0.000001)
    assertEquals(7.0, u1.longitude, 0.000001)
    assertNotEquals("dummyPic.png", u1.profilePictureUrl)
    assertEquals("pl4c3h0ld3rP1c.jpg", u1.profilePictureUrl)
    assertEquals("usr_01_jd", u1.userUUID)
  }

  @Test
  fun checkString() {
    val u1 =
        DataUser(
            "M.", "John", "Doe", "john.doe@example.com", "+41223456789", 0.0, 0.0, "", "usr_01_jd")
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
    mockUserList.put(dataUser.userUUID, dataUser)
  }

  override fun updateUser(dataUser: DataUser, callback: (Result<Unit>) -> Unit) {
    getUser(dataUser.userUUID) { result ->
      result.fold(
          {
            mockUserList.put(dataUser.userUUID, dataUser)
            callback(Result.success(Unit))
          },
          { callback(Result.failure(it)) })
    }
  }

  override fun deleteUser(uuid: String, callback: (Result<Unit>) -> Unit) {
    mockUserList.remove(uuid)
  }
}
