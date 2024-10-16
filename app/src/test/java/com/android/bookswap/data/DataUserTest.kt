package com.android.bookswap.data

import android.location.Address
import io.mockk.InternalPlatformDsl.toStr
import java.util.Locale
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
    assertEquals(u1.address.toStr(), Address(Locale.getDefault()).toString())
  }

  @Test
  fun checkAssign() {
    val userVM = com.android.bookswap.model.UserViewModel("")
    var address = Address(Locale.getDefault())
    address.setCountryCode("CH")
    address.setLocality("Lausanne")
    address.setPostalCode("1000")
    address.setCountryName("Switzerland")
    address.setAddressLine(0, "Rue de la Gare 1")
    userVM.updateUser(
        DataUser(
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

  @Test
  fun checkString() {

    fun createAddress(
        locale: Locale = Locale.getDefault(),
        featureName: String = "",
        addressLines: HashMap<Int, String> = HashMap<Int, String>(),
        adminArea: String = "",
        subAdminArea: String = "",
        locality: String = "",
        subLocality: String = "",
        thoroughfare: String = "",
        subThoroughfare: String = "",
        premises: String = "",
        postalCode: String = "",
        countryCode: String = "",
        countryName: String = "",
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        hasLat: Boolean = false,
        hasLon: Boolean = false,
        phone: String = "",
        url: String = ""
    ): Address {
      var addr = Address(locale)
      addr.setFeatureName(featureName)
      addr.adminArea = adminArea
      addr.subAdminArea = subAdminArea
      addr.setLocality(locality)
      addr.subLocality = subLocality
      addr.thoroughfare = thoroughfare
      addr.subThoroughfare = subThoroughfare
      addr.premises = premises
      addr.setPostalCode(postalCode)
      addr.setCountryCode(countryCode)
      addr.setCountryName(countryName)
      if (hasLat) {
        addr.latitude = latitude
      } else {
        addr.latitude = 0.0
      }
      if (hasLon) {
        addr.longitude = longitude
      } else {
        addr.longitude = 0.0
      }
      addr.phone = phone
      addr.url = url
      if (addressLines.isNotEmpty()) {
        for (line in addressLines) addr.setAddressLine(line.key, line.value)
      }
      return addr
    }
    val userVM = com.android.bookswap.model.UserViewModel("")
    var address =
        createAddress(
            Locale.ENGLISH,
            locality = "Lausanne",
            postalCode = "1000",
            countryCode = "CH",
            countryName = "Switzerland")
    address.setAddressLine(0, "Rue de la Gare 1")
    val usr =
        DataUser(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
            "dummyPic.png",
            "dummyUUID0000")
    userVM.updateUser(
        DataUser(
            "M.",
            "John",
            "Doe",
            "John.Doe@example.com",
            "+41223456789",
            address,
            "dummyPic.png",
            "dummyUUID0000"))
    var u1 = userVM.getUser()
    assertEquals("M. John Doe", u1.toString())
    assertEquals(
        "M. John Doe:¦M.|John|Doe¦John.Doe@example.com|+41223456789¦null¦dummyPic.png¦dummyUUID0000",
        u1.printFull1Line())
    assertEquals(
        "M. John Doe:\n  M.|John|Doe\n  John.Doe@example.com|+41223456789\n  null\n  dummyPic.png\n  dummyUUID0000",
        u1.printFullMultiLine())
  }
}
