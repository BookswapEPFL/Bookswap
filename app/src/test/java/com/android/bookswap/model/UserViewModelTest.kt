package com.android.bookswap.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.android.bookswap.data.repository.UsersRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

  // Test Dispatcher for controlling coroutine execution
  private val testDispatcher = StandardTestDispatcher()

  // Mock dependencies
  private lateinit var mockContext: Context
  private lateinit var mockGeocoder: Geocoder
  private lateinit var mockUserRepository: UsersRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var mockAddress: Address

  // Test data
  private val userUUID = UUID.randomUUID()
  private val addressComponents =
      listOf("1600 Amphitheatre Parkway", "Mountain View", "CA", "94043", "USA")
  private val latitude = 37.4221
  private val longitude = -122.0841

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)

    mockContext = mockk()
    mockGeocoder = mockk()
    mockUserRepository = mockk(relaxed = true)

    // Initialize the ViewModel
    userViewModel = UserViewModel(userUUID, mockUserRepository)

    // Mock the Address object
    mockAddress = mockk()
    every { mockAddress.latitude } returns latitude
    every { mockAddress.longitude } returns longitude

    // Mock the Geocoder behavior
    mockkConstructor(Geocoder::class)
    every { anyConstructed<Geocoder>().getFromLocationName(any(), any()) } returns
        mutableListOf(mockAddress)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    clearAllMocks()
  }

  @Test
  fun `updateCoordinates updates location`() = runTest {
    // Arrange
    val mockAddress = mockk<Address>()
    every { mockAddress.latitude } returns latitude
    every { mockAddress.longitude } returns longitude
    every { mockAddress.maxAddressLineIndex } returns 1

    // Simulate Geocoder returning a list of addresses
    every { mockGeocoder.getFromLocationName(any(), any()) } returns mutableListOf(mockAddress)

    // Mock updateLocation to return success
    coEvery { mockUserRepository.updateLocation(userUUID, latitude, longitude, any()) } answers
        {
          val callback = arg<(Result<Unit>) -> Unit>(3)
          callback(Result.success(Unit))
        }

    // Act
    userViewModel.updateCoordinates(addressComponents, mockContext, userUUID)
    advanceUntilIdle() // Ensure all coroutines complete

    // Assert
    verify { mockUserRepository.updateLocation(userUUID, latitude, longitude, any()) }
  }
}
