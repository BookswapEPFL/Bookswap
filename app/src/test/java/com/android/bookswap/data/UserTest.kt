package com.android.bookswap.data

import android.location.Address
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UserTest {
  @Test
  fun checkCreate() {
    val u1 = User()
    assertEquals(u1.greeting, "")
    assertEquals(u1.firstName, "")
    assertEquals(u1.lastName, "")
    assertEquals(u1.email, "")
    assertEquals(u1.phoneNumber, "")
    assertEquals(u1.profilePictureUrl, "")
    assertEquals(u1.userId, "")
    assertEquals(u1.address, Address(Locale.getDefault()))
  }

  @Test
  fun checkAssign() {
    val address = Address(Locale.getDefault())
    address.countryCode = "CH"
    address.locality = "Lausanne"
    address.postalCode = "1000"
    address.countryName = "Switzerland"
    address.setAddressLine(0, "Rue de la Gare 1")
    val u1 =
        User(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
            "dummyPic.png",
            "dummyUUID0000")
    assertEquals(u1.greeting, "M.")
    assertEquals(u1.firstName, "John")
    assertEquals(u1.lastName, "Doe")
    assertEquals(u1.email, "Hohn.Doe@example.com")
    assertEquals(u1.phoneNumber, "+41223456789")
    assertEquals(u1.profilePictureUrl, "dummyPic.png")
    assertEquals(u1.userId, "dummyUUID0000")
    assertEquals(u1.address, address)
    u1.greeting = "Mr."
    assertNotEquals(u1.greeting, "M.")
    assertEquals(u1.greeting, "Mr.")
    assertEquals(u1.firstName, "John")
    assertEquals(u1.lastName, "Doe")
    assertEquals(u1.email, "Hohn.Doe@example.com")
    assertEquals(u1.phoneNumber, "+41223456789")
    assertEquals(u1.profilePictureUrl, "dummyPic.png")
    assertEquals(u1.userId, "dummyUUID0000")
    assertEquals(u1.address, address)
  }
}
