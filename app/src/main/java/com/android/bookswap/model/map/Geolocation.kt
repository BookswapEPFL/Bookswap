package com.android.bookswap.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow

const val REQUEST_LOCATION_PERMISSION = 1
const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2
const val LOCATION_REFRESH_DELAY = 500L
const val MIN_UPDATE_DISTANCE_METERS = 20F

/**
 * Geolocation class manages the geolocation functionality and handles the required permissions for
 * accessing location data.
 * - To start receiving location updates, call startLocationUpdates().
 * - To stop receiving updates, call stopLocationUpdates().
 * - The current user's location can be checked using the latitude and longitude variables.
 * - The current state of location updates can be checked using the isRunning variable.
 *
 * This class requires appropriate location permissions to function, including both foreground and
 * optionally background location access.
 */
class Geolocation(private val activity: Activity) : IGeolocation {
  private val fusedLocationClient: FusedLocationProviderClient =
      LocationServices.getFusedLocationProviderClient(activity)
  val isRunning = mutableStateOf(false)
  override val latitude = MutableStateFlow(Double.NaN)
  override val longitude = MutableStateFlow(Double.NaN)

  /** Location request settings */
  private val locationRequest: LocationRequest =
      LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_REFRESH_DELAY)
          .setMinUpdateDistanceMeters(MIN_UPDATE_DISTANCE_METERS)
          .build()
  /**
   * Callback for location updates.
   *
   * This callback is triggered whenever the location is updated. It updates the `latitude` and
   * `longitude` state flows with the new location data.
   */
  private val locationCallback =
      object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
          p0.lastLocation?.let { location ->
            // Handle the updated location here
            latitude.value = location.latitude
            longitude.value = location.longitude
            // You can save this location or notify other parts of your app
          }
        }
      }

  /** Check if permissions are granted */
  private fun hasLocationPermissions(): Boolean {
    val fine =
        activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    val coarse =
        activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    return fine || coarse
  }

  /** Start location updates */
  @SuppressLint("MissingPermission")
  override fun startLocationUpdates() {
    if (!isRunning.value) {
      if (hasLocationPermissions()) {
        fusedLocationClient.lastLocation.addOnSuccessListener {
          latitude.compareAndSet(Double.NaN, it.latitude)
          longitude.compareAndSet(Double.NaN, it.longitude)
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper())
        isRunning.value = true
      }
    }
  }

  /** Stop location updates */
  override fun stopLocationUpdates() {
    fusedLocationClient.removeLocationUpdates(locationCallback)
    isRunning.value = false
  }
}
