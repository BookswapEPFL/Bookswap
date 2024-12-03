package com.android.bookswap.model

import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.invoke
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class OthersUserViewModelTest {
    @MockK private lateinit var usersRepository: UsersRepository
    @MockK private lateinit var viewModel: OthersUserViewModel
    private val testUUID = UUID.randomUUID()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        usersRepository = mockk(relaxed = true) // Create a relaxed mock
        viewModel = OthersUserViewModel(testUUID, usersRepository)
    }

    @Test
    fun `getUserByUUID fetches user successfully`() {
        // Arrange: Define expected behavior for the repository
        val mockUser = DataUser(
            userUUID = testUUID,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "0796667788",
            latitude = 45.0,
            longitude = 50.0,
            profilePictureUrl = "",
            bookList = listOf(UUID.randomUUID(), UUID.randomUUID())
        )
        //("John Doe", "john@example.com") // Example user data
        every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers {
            lambda<(Result<DataUser>) -> Unit>().invoke(Result.success(mockUser))
        }

        var resultUser: DataUser? = null

        // Act: Call the method to be tested
        viewModel.getUserByUUID(testUUID) { user ->
            resultUser = user
        }

        // Assert: Verify the result and interactions
        assert(resultUser == mockUser)
        verify(exactly = 1) { usersRepository.getUser(testUUID, any()) }
    }

    @Test
    fun `getUserByUUID handles failure gracefully`() {
        // Arrange: Define failure behavior for the repository
        val errorMessage = "User not found"
        every { usersRepository.getUser(eq(testUUID), captureLambda()) } answers {
            lambda<(Result<DataUser>) -> Unit>().invoke(Result.failure(Exception(errorMessage)))
        }

        var resultUser: DataUser? = null

        // Act: Call the method to be tested
        viewModel.getUserByUUID(testUUID) { user ->
            resultUser = user
        }

        // Assert: Verify the result and interactions
        assert(resultUser == null)
        verify(exactly = 1) { usersRepository.getUser(testUUID, any()) }
    }
}