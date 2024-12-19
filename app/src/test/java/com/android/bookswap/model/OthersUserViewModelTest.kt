package com.android.bookswap.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.test.core.app.ApplicationProvider
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import io.mockk.*
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.invoke
import io.mockk.mockk
import io.mockk.verify
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OthersUserViewModelTest {
  @MockK private lateinit var usersRepository: UsersRepository
  @MockK private lateinit var viewModel: OthersUserViewModel
  private val testUUID = UUID.randomUUID()
  private lateinit var mockContext: Context

  private val mockUser = DataUser(userUUID = testUUID, latitude = 46.5293, longitude = 6.6233)

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    mockContext = ApplicationProvider.getApplicationContext()
    usersRepository = mockk(relaxed = true) // Create a relaxed mock
    viewModel = spyk(OthersUserViewModel(mockContext, testUUID, usersRepository))

    mockkConstructor(Geocoder::class)
  }

  @Test
  fun `getUserByUUID fetches user successfully`() {
    // Arrange: Define expected behavior for the repository
    val mockUser =
        DataUser(
            userUUID = testUUID,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "0796667788",
            latitude = 45.0,
            longitude = 50.0,
            profilePictureUrl = "",
            bookList = listOf(UUID.randomUUID(), UUID.randomUUID()))
    // ("John Doe", "john@example.com") // Example user data
    every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers
        {
          lambda<(Result<DataUser>) -> Unit>().invoke(Result.success(mockUser))
        }

    var resultUser: DataUser? = null

    // Act: Call the method to be tested
    viewModel.getUserByUUID(testUUID) { user -> resultUser = user }

    // Assert: Verify the result and interactions
    assert(resultUser == mockUser)
    verify(exactly = 2) { usersRepository.getUser(testUUID, any()) }
  }

  @Test
  fun `getUserByUUID handles failure gracefully`() {
    // Arrange: Define failure behavior for the repository
    val errorMessage = "User not found"
    every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers
        {
          lambda<(Result<DataUser>) -> Unit>().invoke(Result.failure(Exception(errorMessage)))
        }

    var resultUser: DataUser? = null

    // Act: Call the method to be tested
    viewModel.getUserByUUID(testUUID) { user -> resultUser = user }

    // Assert: Verify the result and interactions
    assert(resultUser == null)
    verify(exactly = 2) { usersRepository.getUser(testUUID, any()) }
  }

  @Test
  fun `fetchUserAndInitializeAddress fetches user and calls getCityAndCountryFromCoordinates`() =
      runTest {
        every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers
            {
              lambda<(Result<DataUser>) -> Unit>().invoke(Result.success(mockUser))
            }

        // Mock Geocoder response using Robolectric's shadow system
        val address =
            Address(Locale.getDefault()).apply {
              locality = "Lausanne"
              countryName = "Switzerland"
            }

        // ShadowGeocoder will use this address as the result
        mockkConstructor(Geocoder::class)
        every {
          anyConstructed<Geocoder>().getFromLocation(mockUser.latitude, mockUser.longitude, 1)
        } returns listOf(address)

        // Act
        viewModel.fetchUserAndInitializeAddress(mockContext)

        // Verify that the addressStr was updated correctly
        assertEquals("Lausanne, Switzerland", viewModel.addressStr.value)
      }

  @Test
  fun `fetchUserAndInitializeAddress handles user fetch failure gracefully`() = runTest {
    // Arrange
    val errorMessage = "User not found"

    every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers
        {
          lambda<(Result<DataUser>) -> Unit>().invoke(Result.failure(Exception(errorMessage)))
        }

    // Act
    viewModel.fetchUserAndInitializeAddress(mockContext)

    // Assert
    assertTrue(viewModel.addressStr.value.isEmpty())
    verify { usersRepository.getUser(testUUID, any()) }
  }

  @Test
  fun `fetchUserAndInitializeAddress handles geocoding failure gracefully`() = runTest {
    every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers
        {
          lambda<(Result<DataUser>) -> Unit>().invoke(Result.success(mockUser))
        }

    every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns null

    // Act
    viewModel.fetchUserAndInitializeAddress(mockContext)

    // Assert
    assertTrue(viewModel.addressStr.value.isEmpty())
    verify { usersRepository.getUser(testUUID, any()) }
  }

  @Test
  fun `fetchUserAndInitializeAddress handles empty geocoding result gracefully`() = runTest {
    every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers
        {
          lambda<(Result<DataUser>) -> Unit>().invoke(Result.success(mockUser))
        }

    every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns emptyList()

    // Act
    viewModel.fetchUserAndInitializeAddress(mockContext)

    // Assert
    assertTrue(viewModel.addressStr.value.isEmpty())
    verify { usersRepository.getUser(testUUID, any()) }
  }

  @Test
  fun `getCityAndCountryFromCoordinates updates addressStr successfully`() = runTest {
    val mockAddress = mockk<Address>()
    every { mockAddress.locality } returns "Lausanne"
    every { mockAddress.countryName } returns "Switzerland"

    every {
      anyConstructed<Geocoder>().getFromLocation(mockUser.latitude, mockUser.longitude, 1)
    } returns listOf(mockAddress)

    // Act
    viewModel.getCityAndCountryFromCoordinates(mockContext, mockUser.latitude, mockUser.longitude)

    // Assert
    assertEquals("Lausanne, Switzerland", viewModel.addressStr.value)
  }

  @Test
  fun `getCityAndCountryFromCoordinates handles no results gracefully`() = runTest {
    every {
      anyConstructed<Geocoder>().getFromLocation(mockUser.latitude, mockUser.longitude, 1)
    } returns emptyList()

    // Act
    viewModel.getCityAndCountryFromCoordinates(mockContext, mockUser.latitude, mockUser.longitude)

    // Assert
    assertTrue(viewModel.addressStr.value.isEmpty())
  }

  @Test
  fun `getCityAndCountryFromCoordinates handles geocoding exception gracefully`() = runTest {
    every {
      anyConstructed<Geocoder>().getFromLocation(mockUser.latitude, mockUser.longitude, 1)
    } throws Exception("Geocoding failed")

    // Act
    viewModel.getCityAndCountryFromCoordinates(mockContext, mockUser.latitude, mockUser.longitude)

    // Assert
    assertTrue(viewModel.addressStr.value.isEmpty())
  }
}
