package com.android.bookswap

import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.model.UserViewModel
import com.google.android.gms.maps.model.LatLng
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object DefaultMockKs {
  private val mockKUserLoc = LatLng(0.0, 0.0)
  private val mockKUserUID = UUID.fromString("00000000-0000-4000-C000-000000000000")
  private val mockKBookUID = UUID.fromString("00000000-0000-4000-C000-100000000000")
  val mockKUserRepository: UsersRepository = mockk()
  val mockKStandardUser =
      DataUser(
          mockKUserUID,
          "M.",
          "John",
          "Doe",
          "John.Doe@example.com",
          "+41223456789",
          mockKUserLoc.latitude,
          mockKUserLoc.longitude,
          "dummyPic.png",
          listOf(mockKBookUID),
          "googleUID",
          listOf("contact1", "contact2"))
  val mockKUserViewModel: UserViewModel =
      spyk<UserViewModel>(
          UserViewModel(mockKUserUID, mockKUserRepository),
          "mockKUserViewModel",
          recordPrivateCalls = true) {
            initUVM(this)
          }

  private fun initUVM(uvm: UserViewModel) {
    every { uvm.latlng } returns mockKUserLoc
    every { uvm setProperty "lat" value any<Double>() } just runs
    every { uvm setProperty "lon" value any<Double>() } just runs
    every { uvm.getUser(any()) } returns mockKStandardUser
    every { uvm.uuid } returns mockKStandardUser.userUUID
    every { uvm.getLocationPlace(any()) } returns MutableStateFlow("address").asStateFlow()
    every { uvm.updateUser(any<DataUser>()) } just runs
    every {
      uvm.updateUser(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } just runs
  }
}
