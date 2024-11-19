package com.android.bookswap

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.android.bookswap.model.isNetworkAvailable
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkUtilsTest {

  private lateinit var context: Context
  private lateinit var connectivityManager: ConnectivityManager
  private lateinit var network: Network
  private lateinit var networkCapabilities: NetworkCapabilities

  @Before
  fun setUp() {
    // Mock the required objects
    context = mockk()
    connectivityManager = mockk()
    network = mockk()
    networkCapabilities = mockk()

    // Mock the behavior for `getSystemService`
    every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
  }

  @After
  fun tearDown() {
    // Clear all mocks to avoid side effects
    unmockkAll()
  }

  @Test
  fun `returns true when internet is available`() {
    // Mock active network and capabilities
    every { connectivityManager.activeNetwork } returns network
    every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
    every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns
        true

    // Test the function
    val result = isNetworkAvailable(context)
    assertTrue(result)
  }

  @Test
  fun `returns false when no active network`() {
    // Mock no active network
    every { connectivityManager.activeNetwork } returns null

    // Test the function
    val result = isNetworkAvailable(context)
    assertFalse(result)
  }

  @Test
  fun `returns false when no network capabilities`() {
    // Mock active network but no capabilities
    every { connectivityManager.activeNetwork } returns network
    every { connectivityManager.getNetworkCapabilities(network) } returns null

    // Test the function
    val result = isNetworkAvailable(context)
    assertFalse(result)
  }

  @Test
  fun `returns false when no internet capability`() {
    // Mock active network and capabilities, but no internet capability
    every { connectivityManager.activeNetwork } returns network
    every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
    every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns
        false

    // Test the function
    val result = isNetworkAvailable(context)
    assertFalse(result)
  }
}
