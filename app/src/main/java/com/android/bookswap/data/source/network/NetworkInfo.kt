package com.android.bookswap.data.source.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

private fun isNetworkAvailable(context: Context): Boolean {
  Log.i("NetworkInfo", "Checking network availability")
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val networkInfo = connectivityManager.activeNetworkInfo
  return networkInfo != null && networkInfo.isConnected
}
