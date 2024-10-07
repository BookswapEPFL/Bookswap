package com.android.sample.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class Geolocation(private val activity: Activity) {

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val latitude = mutableDoubleStateOf(Double.NaN)  // Change to MutableState
    val longitude = mutableDoubleStateOf(Double.NaN)

    // Location request settings
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000 // Update interval in milliseconds
        fastestInterval = 5000 // Fastest update interval in milliseconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0.lastLocation.let { location ->
                // Handle the updated location here
                latitude.doubleValue = location.latitude
                longitude.doubleValue = location.longitude
                // You can save this location or notify other parts of your app
            }
        }
    }

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    // Request location permissions
    fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCATION_PERMISSION)
    }

    // Check if permissions are granted
    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Start location updates
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (hasLocationPermissions()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            requestLocationPermissions()
        }
    }

    // Stop location updates
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
