package com.android.bookswap.data

import android.location.Address
import io.mockk.InternalPlatformDsl.toStr
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
    assertEquals(u1.address.toStr(), Address(Locale.getDefault()).toString())
  }

  @Test
  fun checkAssign() {
    val userVM = com.android.bookswap.model.UserViewModel("")
    var address = Address(Locale.getDefault())
    address.countryCode = "CH"
    address.locality = "Lausanne"
    address.postalCode = "1000"
    address.countryName = "Switzerland"
    address.setAddressLine(0, "Rue de la Gare 1")
    userVM.updateUser(
        User(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
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
    assertEquals(u1.address.toString(), address.toString())
    userVM.updateUser(greeting = "Mr.")
    var address2 = Address(Locale.getDefault())
    address2.countryCode = "CH"
    address2.locality = "Lausanne"
    address2.postalCode = "1001"
    address2.countryName = "Switzerland"
    address2.setAddressLine(0, "Rue de la Gare 1")
    userVM.updateAddress(address2, {})
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
}
