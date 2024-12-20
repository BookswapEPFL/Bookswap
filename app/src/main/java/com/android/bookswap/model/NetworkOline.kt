package com.android.bookswap.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

/**
 * Checks if the network is available.
 *
 * @param context The context to access system services.
 * @return True if the network is available, false otherwise.
 */
fun isNetworkAvailable(context: Context): Boolean {
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val network = connectivityManager.activeNetwork ?: return false
  val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
  return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
      capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

fun isNetworkAvailableForBook(context: Context): Boolean {
  Log.i("NetworkInfo", "Checking network availability")
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val networkInfo = connectivityManager.activeNetworkInfo
  return networkInfo != null && networkInfo.isConnected
}
