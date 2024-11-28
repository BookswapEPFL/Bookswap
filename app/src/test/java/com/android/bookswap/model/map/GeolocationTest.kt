package com.android.bookswap.model.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
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
import org.robolectric.annotation.Config

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

    mockkStatic(ActivityCompat::class)
    every {
      ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_FINE_LOCATION)
    } returns PackageManager.PERMISSION_GRANTED
    every {
      ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
    } returns PackageManager.PERMISSION_GRANTED

    geolocation = Geolocation(mockActivity)
  }

  @Test
  fun `startLocationUpdates should request location updates if permissions are granted`() {
    // Mock the permission check to return true
    every {
      mockActivity.checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, any(), any())
    } returns PackageManager.PERMISSION_GRANTED

    every {
      mockFusedLocationClient.requestLocationUpdates(
          any(), any<LocationCallback>(), Looper.getMainLooper())
    } returns Tasks.forResult(null)
    geolocation.startLocationUpdates()

    verify {
      mockFusedLocationClient.requestLocationUpdates(
          any(), any<LocationCallback>(), Looper.getMainLooper())
    }
    assertEquals(true, geolocation.isRunning.value)
  }

  @Config(sdk = [30])
  @Test
  fun `startLocationUpdates should request permissions if not granted`() {
    every {
      ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_FINE_LOCATION)
    } returns PackageManager.PERMISSION_DENIED
    every {
      ActivityCompat.checkSelfPermission(
          mockActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } returns PackageManager.PERMISSION_DENIED

    // Mock ActivityCompat.requestPermissions (static method)
    mockkStatic(ActivityCompat::class)

    every { ActivityCompat.requestPermissions(any(), any(), any()) } just Runs

    geolocation.startLocationUpdates()

    // Define the expected permissions array
    val expectedLocationPermissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    // Verify that ActivityCompat.requestPermissions is called with the correct arguments
    verify {
      ActivityCompat.requestPermissions(eq(mockActivity), eq(expectedLocationPermissions), eq(1))
    }
    assertEquals(false, geolocation.isRunning.value)
  }

  @Config(sdk = [30])
  @Test
  fun `startLocationUpdates should request background permissions if not granted`() {
    every {
      ActivityCompat.checkSelfPermission(
          mockActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } returns PackageManager.PERMISSION_DENIED

    // Mock ActivityCompat.requestPermissions (static method)
    mockkStatic(ActivityCompat::class)

    every { ActivityCompat.requestPermissions(any(), any(), any()) } just Runs
    every {
      mockFusedLocationClient.requestLocationUpdates(
          any(), any<LocationCallback>(), Looper.getMainLooper())
    } returns Tasks.forResult(null)

    geolocation.startLocationUpdates()

    val expectedBackgroundPermissions = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    // Verify that ActivityCompat.requestPermissions is called with the correct arguments
    verify {
      ActivityCompat.requestPermissions(eq(mockActivity), eq(expectedBackgroundPermissions), eq(2))
    }
    assertEquals(true, geolocation.isRunning.value)
  }

  @Config(sdk = [28])
  @Test
  fun `startLocationUpdates should request permissions If not granted API 28`() {
    every {
      ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_FINE_LOCATION)
    } returns PackageManager.PERMISSION_DENIED
    every {
      ActivityCompat.checkSelfPermission(
          mockActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } returns PackageManager.PERMISSION_DENIED

    // Mock ActivityCompat.requestPermissions (static method)
    mockkStatic(ActivityCompat::class)

    every { ActivityCompat.requestPermissions(any(), any(), any()) } just Runs

    geolocation.startLocationUpdates()

    // Define the expected permissions array
    val expectedLocationPermissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    val expectedBackgroundPermissions = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    // Verify that ActivityCompat.requestPermissions is called with the correct arguments
    verify {
      ActivityCompat.requestPermissions(eq(mockActivity), eq(expectedLocationPermissions), eq(1))
    }
    verify(exactly = 0) {
      ActivityCompat.requestPermissions(any(), eq(expectedBackgroundPermissions), any())
    }
    assertEquals(false, geolocation.isRunning.value)
  }

  @Test
  fun `stopLocationUpdates should remove location updates`() {
    every {
      mockFusedLocationClient.requestLocationUpdates(
          any(), any<LocationCallback>(), Looper.getMainLooper())
    } returns Tasks.forResult(null)
    every { mockFusedLocationClient.removeLocationUpdates(any<LocationCallback>()) } returns
        Tasks.forResult(null)
    every {
      mockActivity.checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, any(), any())
    } returns PackageManager.PERMISSION_GRANTED

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
    every {
      mockActivity.checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, any(), any())
    } returns PackageManager.PERMISSION_GRANTED

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

    geolocation.startLocationUpdates()

    locationCallbackSlot.captured.onLocationResult(mockLocationResult)

    // Verify that the latitude and longitude states are updated correctly
    assertEquals(37.7749, geolocation.latitude.value, 0.0)
    assertEquals(-122.4194, geolocation.longitude.value, 0.0)
    assertEquals(true, geolocation.isRunning.value)
  }
}
