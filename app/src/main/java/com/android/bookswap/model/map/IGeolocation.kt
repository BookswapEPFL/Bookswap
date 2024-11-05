package com.android.bookswap.model.map

import androidx.compose.runtime.MutableState

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
  val latitude: MutableState<Double>
  val longitude: MutableState<Double>

  fun startLocationUpdates()

  fun stopLocationUpdates()
}
