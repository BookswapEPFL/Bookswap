package com.android.bookswap.model

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.bookswap.data.DataUser
import com.android.bookswap.data.repository.UsersRepository
import com.android.bookswap.data.source.network.UserFirestoreSource
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * ViewModel for the user data of the application.
 *
 * @param uuid UUID of the user
 * @param repository Repository to fetch user data
 */
open class OthersUserViewModel(
    context: Context,
    var uuid: UUID,
    private val usersRepository: UsersRepository =
        UserFirestoreSource(FirebaseFirestore.getInstance())
) : ViewModel() {

  // Address string initialized as a MutableStateFlow
  val addressStr = MutableStateFlow("")

  init {
    // Fetch the user and initialize the address when the ViewModel is created
    fetchUserAndInitializeAddress(context)
  }

  /**
   * Fetches user data by user ID using a single callback.
   *
   * @param userId The UUID of the user to fetch.
   * @param callback Callback invoked with the user data if found, or null if not.
   */
  fun getUserByUUID(userId: UUID, callback: (DataUser?) -> Unit) {
    usersRepository.getUser(userId) { result ->
      result
          .onSuccess { user ->
            Log.i("OthersUserViewModel", "User fetched: $user")
            callback(user) // Pass the fetched user to the callback.
          }
          .onFailure { error ->
            Log.e("OthersUserViewModel", "Failed to fetch user: ${error.message}")
            callback(null) // Pass null to the callback in case of failure.
          }
    }
  }

  /**
   * Fetches user data and initializes the addressStr by converting latitude and longitude to a city
   * and country.
   *
   * @param context The application context for Geocoder.
   */
  fun fetchUserAndInitializeAddress(context: Context) {
    usersRepository.getUser(uuid) { result ->
      result
          .onSuccess { user ->
            val latitude = user.latitude
            val longitude = user.longitude

            // Convert latitude and longitude to city and country
            getCityAndCountryFromCoordinates(context, latitude, longitude)
            Log.i("OthersUserViewModel", "Address initialized: ${addressStr.value}")
          }
          .onFailure { error ->
            Log.e("OthersUserViewModel", "Failed to fetch user for address: ${error.message}")
          }
    }
  }

  /**
   * Converts latitude and longitude to a city and country using Geocoder.
   *
   * @param context The application context for Geocoder.
   * @param latitude The latitude of the user.
   * @param longitude The longitude of the user.
   */
  fun getCityAndCountryFromCoordinates(context: Context, latitude: Double, longitude: Double) {
    try {
      val geocoder = Geocoder(context, Locale.getDefault())
      val addresses = geocoder.getFromLocation(latitude, longitude, 1)
      if (!addresses.isNullOrEmpty()) {
        val city = addresses[0].locality ?: "Unknown City"
        val country = addresses[0].countryName ?: "Unknown Country"
        val address = "$city, $country"
        addressStr.value = address
        Log.i("OthersUserViewModel", "Address initialized: $address")
      } else {
        Log.e("OthersUserViewModel", "Failed to fetch city and country from coordinates.")
      }
    } catch (e: Exception) {
      Log.e("OthersUserViewModel", "Geocoding error: ${e.message}")
    }
  }
}
