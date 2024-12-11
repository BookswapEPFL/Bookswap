package com.android.bookswap.model.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GeolocationTest {

  private val mockActivity: Activity = mockk()
  private val mockFusedLocationClient: FusedLocationProviderClient = mockk()
  private lateinit var geolocation: Geolocation

  @Before
  fun setup() {
    // Mock static method for FusedLocationProviderClient
    mockkStatic(LocationServices::class)
    every { LocationServices.getFusedLocationProviderClient(mockActivity) } returns
        mockFusedLocationClient

    every { mockActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) } returns
        PackageManager.PERMISSION_GRANTED
    every { mockActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) } returns
        PackageManager.PERMISSION_GRANTED

    geolocation = Geolocation(mockActivity)
  }

  @Test
  fun `startLocationUpdates should request location updates`() {
    every {
      mockFusedLocationClient.requestLocationUpdates(
          any(), any<LocationCallback>(), Looper.getMainLooper())
    } returns Tasks.forResult(null)
    every { mockFusedLocationClient.lastLocation } returns Tasks.forResult(null)

    geolocation.startLocationUpdates()

    verify {
      mockFusedLocationClient.requestLocationUpdates(
          any(), any<LocationCallback>(), Looper.getMainLooper())
    }
    assertEquals(true, geolocation.isRunning.value)
  }

  @Test
  fun `stopLocationUpdates should remove location updates`() {
    every {
      mockFusedLocationClient.requestLocationUpdates(
          any(), any<LocationCallback>(), Looper.getMainLooper())
    } returns Tasks.forResult(null)
    every { mockFusedLocationClient.removeLocationUpdates(any<LocationCallback>()) } returns
        Tasks.forResult(null)
    every { mockFusedLocationClient.lastLocation } returns Tasks.forResult(null)

    geolocation.startLocationUpdates()
    geolocation.stopLocationUpdates()

    // Verify that removeLocationUpdates was called with the correct callback
    verify { mockFusedLocationClient.removeLocationUpdates(any<LocationCallback>()) }
    assertEquals(false, geolocation.isRunning.value)
  }

  @Test
  fun `latitude and longitude should initially be NaN`() {
    assertEquals(Double.NaN, geolocation.latitude.value, 0.0)
    assertEquals(Double.NaN, geolocation.longitude.value, 0.0)
  }

  @Test
  fun `latitude and longitude should be updated in location callback`() {

    val mockLocation: Location = mockk()
    every { mockLocation.latitude } returns 37.7749
    every { mockLocation.longitude } returns -122.4194

    // Mock LocationResult to return the mock location
    val mockLocationResult: LocationResult = mockk()
    every { mockLocationResult.lastLocation } returns mockLocation

    val locationCallbackSlot = slot<LocationCallback>()
    mockkObject(mockFusedLocationClient)
    every {
      mockFusedLocationClient.requestLocationUpdates(
          any<LocationRequest>(), capture(locationCallbackSlot), any<Looper>())
    } returns mockk()
    every { mockFusedLocationClient.lastLocation } returns Tasks.forResult(null)

    geolocation.startLocationUpdates()

    locationCallbackSlot.captured.onLocationResult(mockLocationResult)

    // Verify that the latitude and longitude states are updated correctly
    assertEquals(37.7749, geolocation.latitude.value, 0.0)
    assertEquals(-122.4194, geolocation.longitude.value, 0.0)
    assertEquals(true, geolocation.isRunning.value)
  }
}
