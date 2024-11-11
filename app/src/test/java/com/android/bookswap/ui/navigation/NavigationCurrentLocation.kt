package com.android.bookswap.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import io.mockk.*
import org.junit.Before
import org.junit.Test

class NavigationCurrentLocation {

    private lateinit var navController: NavHostController
    private lateinit var navigationActions: NavigationActions

    @Before
    fun setup() {
        navController = mockk(relaxed = true)
        navigationActions = NavigationActions(navController)

        val currentBackStackEntry = mockk<NavBackStackEntry>()
        every { navController.currentBackStackEntry } returns currentBackStackEntry

        val destination = mockk<NavDestination>()
        every { currentBackStackEntry.destination } returns destination
    }

    @Test
    fun `navigateTo does not navigate when already on destination`() {
        // Arrange: Set up the current route to match the target destination
        every { navController.currentBackStackEntry?.destination?.route } returns TopLevelDestinations.MAP.route

        // Act: Try to navigate to the MAP screen
        navigationActions.navigateTo(TopLevelDestinations.MAP)

        // Assert: Verify that navigate is not called since we are already on MAP
        verify(exactly = 0) { navController.navigate(any<String>()) }
    }

    @Test
    fun `navigateTo navigates when not on destination`() {
        // Arrange: Set up the current route to be different from the target destination
        every { navController.currentBackStackEntry?.destination?.route } returns TopLevelDestinations.CHAT.route

        // Act: Try to navigate to the MAP screen
        navigationActions.navigateTo(TopLevelDestinations.MAP)

        // Assert: Verify that navigate is called since we are not on MAP
        verify(exactly = 1) { navController.navigate(eq(TopLevelDestinations.MAP.route), any<NavOptionsBuilder.() -> Unit>()) }
    }
}