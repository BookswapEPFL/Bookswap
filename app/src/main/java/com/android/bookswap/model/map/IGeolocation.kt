package com.android.bookswap.model.map

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for providing geolocation data, with basic location management functionality.
 *
 * Classes implementing this interface should provide access to the user's current latitude and
 * longitude, as well as functions to start and stop location updates.
 *
 * This interface allows different implementations for geolocation, facilitating testing by enabling
 * the use of mock or fake data sources.
 */
interface IGeolocation {
  val userLocation: StateFlow<LatLng?>

  /**
   * Starts location updates.
   *
   * This function initiates the process of receiving location updates. It checks for the necessary
   * location permissions and requests them if they are not granted. If the permissions are granted,
   * it starts the location updates using the `FusedLocationProviderClient`.
   */
  fun startLocationUpdates()
  /**
   * Stops location updates.
   *
   * This function stops the process of receiving location updates. It removes the location updates
   * using the `FusedLocationProviderClient` and sets the `isRunning` state to false.
   */
  fun stopLocationUpdates()
}
