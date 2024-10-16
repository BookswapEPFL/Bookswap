package com.android.bookswap.data

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
    val userVM = com.android.bookswap.model.UserViewModel("")

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

    val userVM = com.android.bookswap.model.UserViewModel("")
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
    assertEquals("M. John Doe", u1.toString())
    assertEquals(
        "M. John Doe:¦M.|John|Doe¦John.Doe@example.com|+41223456789¦0.0, 0.0¦dummyPic.png¦dummyUUID0000",
        u1.printFull1Line())
    assertEquals(
        "M. John Doe:\n  M.|John|Doe\n  John.Doe@example.com|+41223456789\n  0.0, 0.0\n  dummyPic.png\n  dummyUUID0000",
        u1.printFullMultiLine())
  }
}
