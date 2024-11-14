package com.android.bookswap.model.map

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Mock implementation of [IGeolocation] for testing purposes.
 *
 * This class provides hardcoded latitude and longitude values and does not interact with real
 * location services. It is useful for testing UI components that rely on geolocation data without
 * requiring actual GPS hardware or permissions.
 *
 * Usage:
 * - This can be passed as a dependency to classes or Composables expecting an [IGeolocation]
 *   implementation, allowing the testing of location-dependent functionality in a controlled
 *   environment.
 */
class DefaultGeolocation : IGeolocation {
  override val latitude = MutableStateFlow(0.0)
  override val longitude = MutableStateFlow(0.0)
  private val isRunning = mutableStateOf(false)
  /**
   * Starts location updates by setting the isRunning flag to true. Logs a message indicating that
   * the default geolocation has started.
   */
  override fun startLocationUpdates() {
    isRunning.value = true
    Log.d("DefaultGeolocation", "Using default geolocation start")
  }
  /**
   * Stops location updates by setting the isRunning flag to false. Logs a message indicating that
   * the default geolocation has stopped.
   */
  override fun stopLocationUpdates() {
    isRunning.value = false
    Log.d("DefaultGeolocation", "Using default geolocation stop")
  }
  /**
   * Moves the location to the specified latitude and longitude. Updates the latitude and longitude
   * values if location updates are running. Logs a message if location updates are not running.
   *
   * @param latitude The new latitude value.
   * @param longitude The new longitude value.
   */
  fun moveLocation(latitude: Double, longitude: Double) {
    if (isRunning.value) {
      this.latitude.value = latitude
      this.longitude.value = longitude
    } else {
      Log.d("DefaultGeolocation", "Location updates are not running")
    }
  }
}
