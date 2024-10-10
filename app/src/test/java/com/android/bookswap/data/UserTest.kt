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
    assertEquals(u1, User("", "", "", "", "", Address(Locale.getDefault()), "", ""))
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
    assertEquals(
        u1,
        User(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
            "dummyPic.png",
            "dummyUUID0000"))
    u1.greeting = "Mr."
    assertNotEquals(
        u1,
        User(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
            "dummyPic.png",
            "dummyUUID0000"))
    assertEquals(
        u1,
        User(
            "Mr.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
            "dummyPic.png",
            "dummyUUID0000"))
  }
}
