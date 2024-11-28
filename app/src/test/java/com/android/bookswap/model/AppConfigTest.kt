package com.android.bookswap.model

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class AppConfigTest {

    @Test
    fun `assert creation of data class`() {
        val mockUserViewModel: UserViewModel = mockk()
        val appConfig = AppConfig(
            userViewModel = mockUserViewModel
        )

        assertEquals(mockUserViewModel, appConfig.userViewModel)
    }
}