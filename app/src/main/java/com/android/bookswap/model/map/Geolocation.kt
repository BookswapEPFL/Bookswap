package com.android.bookswap.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

const val REQUEST_LOCATION_PERMISSION = 1
const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2

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
class Geolocation(private val activity: Activity) {
  private val fusedLocationClient: FusedLocationProviderClient =
      LocationServices.getFusedLocationProviderClient(activity)
  val isRunning = mutableStateOf(false)
  val latitude = mutableDoubleStateOf(Double.NaN)
  val longitude = mutableDoubleStateOf(Double.NaN)

  // Location request settings
  private val locationRequest: LocationRequest =
      LocationRequest.create().apply {
        interval = 10000 // Update interval in milliseconds
        fastestInterval = 5000 // Fastest update interval in milliseconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
      }

  private val locationCallback =
      object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
          p0.lastLocation.let { location ->
            // Handle the updated location here
            latitude.doubleValue = location.latitude
            longitude.doubleValue = location.longitude
            // You can save this location or notify other parts of your app
          }
        }
      }

  // Request location permissions
  private fun requestLocationPermissions() {
    val permissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCATION_PERMISSION)
  }

  // Check if permissions are granted
  private fun hasLocationPermissions(): Boolean {
    return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
  }

  @RequiresApi(Build.VERSION_CODES.Q)
  private fun requestBackgroundPermissions() {
    val permissions = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    ActivityCompat.requestPermissions(
        activity, permissions, BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE)
  }

  @RequiresApi(Build.VERSION_CODES.Q)
  private fun hasBackgroundPermissions(): Boolean {
    return ActivityCompat.checkSelfPermission(
        activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
  }

  // Start location updates
  @SuppressLint("MissingPermission")
  fun startLocationUpdates() {
    if (!isRunning.value) {
      if (hasLocationPermissions()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBackgroundPermissions()) {
          requestBackgroundPermissions()
        }
        // can run without ACCESS_BACKGROUND_LOCATION but it is better if we have the permission
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper())
        isRunning.value = true
      } else {
        requestLocationPermissions()
        // need to check here for the permission, as otherwise the startLocationUpdates would just
        // loop indefinitely if the user refuse to give the permission
        if (hasLocationPermissions()) {
          startLocationUpdates()
        }
      }
    }
  }

  // Stop location updates
  fun stopLocationUpdates() {
    fusedLocationClient.removeLocationUpdates(locationCallback)
    isRunning.value = false
  }
}
