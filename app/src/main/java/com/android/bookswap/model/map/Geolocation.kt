package com.android.bookswap.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
  private val _userLoc = MutableStateFlow<LatLng?>(null)

  override val userLocation = _userLoc.asStateFlow()

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
          p0.lastLocation?.let {
            // Handle the updated location here
            _userLoc.value = LatLng(it.latitude, it.longitude)
          }
        }
      }

  /** Request location permissions */
  private fun requestLocationPermissions() {
    val permissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCATION_PERMISSION)
  }

  /** Check if permissions are granted */
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

  /** Start location updates */
  @SuppressLint("MissingPermission")
  override fun startLocationUpdates() {
    if (!isRunning.value) {
      if (hasLocationPermissions()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBackgroundPermissions()) {
          requestBackgroundPermissions()
        }
        // can run without ACCESS_BACKGROUND_LOCATION but it is better if we have the permission
        fusedLocationClient.lastLocation.addOnSuccessListener {
          _userLoc.compareAndSet(null, LatLng(it.latitude, it.longitude))
        }
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

  /** Stop location updates */
  override fun stopLocationUpdates() {
    fusedLocationClient.removeLocationUpdates(locationCallback)
    _userLoc.value = null
    isRunning.value = false
  }
}

object GeoLocViewModel : ViewModel() {
  lateinit var address: Address
  private val _addressStr = MutableStateFlow("")
  val addressStr = _addressStr.asStateFlow()

  fun getPlace(latitude: Double, longitude: Double, context: Context) {

    val handleAddresses: (MutableList<Address>?) -> Unit = {
      if (!it.isNullOrEmpty()) {
        address = it.first()
        _addressStr.value =
            address.let {
              var s = ""
              for (i in 0..it.maxAddressLineIndex) {
                s += (it.getAddressLine(i))
              }
              s
            }
      } else {
        // android.util.Log.wtf("TAG_GEOLOCATION", "Address list empty !")
      }
    }

    viewModelScope.launch {
      val geocoder = Geocoder(context)
      val geocodeListener = Geocoder.GeocodeListener(handleAddresses)
      withContext(Dispatchers.IO) {
        // Perform geocoding on a background thread
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          geocoder.getFromLocation(latitude, longitude, 1, geocodeListener)
        } else {
          handleAddresses(geocoder.getFromLocation(latitude, longitude, 1))
        }
      }
    }
  }
}
