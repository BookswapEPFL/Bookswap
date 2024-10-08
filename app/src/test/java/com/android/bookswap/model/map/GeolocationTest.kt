/*package com.android.sample.model.map

import android.app.Activity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.mockito.Mockito.*

class GeolocationTest {

    private lateinit var mockActivity: Activity
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient
    private lateinit var geolocation: Geolocation

    @Before
    fun setup() {
        mockFusedLocationClient = mockk()
        mockActivity = mock(Activity::class.java)

        mockkStatic(LocationServices::class)
        every { LocationServices.getFusedLocationProviderClient(mockActivity) } returns mockFusedLocationClient


        // Create an instance of the class under test
        geolocation = Geolocation(mockActivity)
    }
}*/
package com.android.bookswap.model.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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
import org.mockito.Mockito.*

class GeolocationTest {

  private lateinit var mockActivity: Activity
  private lateinit var mockFusedLocationClient: FusedLocationProviderClient
  private lateinit var geolocation: Geolocation

  @Before
  fun setup() {
    mockFusedLocationClient = mock(FusedLocationProviderClient::class.java)
    mockActivity = mock(Activity::class.java)

    // Mock static method for FusedLocationProviderClient
    mockkStatic(LocationServices::class)
    every { LocationServices.getFusedLocationProviderClient(mockActivity) } returns
        mockFusedLocationClient

    geolocation = Geolocation(mockActivity)
  }

  @Test
  fun `startLocationUpdates should request location updates if permissions are granted`() {
    // Mock the permission check to return true
    `when`(mockActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    geolocation.startLocationUpdates()

    verify(mockFusedLocationClient)
        .requestLocationUpdates(
            any(LocationRequest::class.java),
            any(LocationCallback::class.java),
            eq(Looper.getMainLooper()))
  }

  @Test
  fun `startLocationUpdates should request permissions if not granted`() {
    `when`(
            ActivityCompat.checkSelfPermission(
                mockActivity, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    // Mock ActivityCompat.requestPermissions (static method)
    mockkStatic(ActivityCompat::class)

    every { ActivityCompat.requestPermissions(any(), any(), any()) } just Runs

    geolocation.startLocationUpdates()

    // Define the expected permissions array
    val expectedPermissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    // Verify that ActivityCompat.requestPermissions is called with the correct arguments
    verify { ActivityCompat.requestPermissions(eq(mockActivity), eq(expectedPermissions), eq(1)) }
  }

  @Test
  fun `stopLocationUpdates should remove location updates`() {
    geolocation.stopLocationUpdates()

    // Verify that removeLocationUpdates was called with the correct callback
    verify(mockFusedLocationClient).removeLocationUpdates(any(LocationCallback::class.java))
  }

  @Test
  fun `latitude and longitude should initially be NaN`() {
    assertEquals(Double.NaN, geolocation.latitude.doubleValue, 0.0)
    assertEquals(Double.NaN, geolocation.longitude.doubleValue, 0.0)
  }

  @Test
  fun `latitude and longitude should be updated in location callback`() {
    val mockLocation = mock(android.location.Location::class.java)
    `when`(mockLocation.latitude).thenReturn(37.7749)
    `when`(mockLocation.longitude).thenReturn(-122.4194)

    // Mock LocationResult to return the mock location
    val mockLocationResult = mock(com.google.android.gms.location.LocationResult::class.java)
    `when`(mockLocationResult.lastLocation).thenReturn(mockLocation)

    val locationCallbackSlot = slot<LocationCallback>()
    mockkObject(mockFusedLocationClient)
    every {
      mockFusedLocationClient.requestLocationUpdates(
          any<LocationRequest>(), capture(locationCallbackSlot), any<Looper>())
    } returns mockk()

    geolocation.startLocationUpdates()

    locationCallbackSlot.captured.onLocationResult(mockLocationResult)

    // Verify that the latitude and longitude states are updated correctly
    assertEquals(37.7749, geolocation.latitude.doubleValue, 0.0)
    assertEquals(-122.4194, geolocation.longitude.doubleValue, 0.0)
  }
}