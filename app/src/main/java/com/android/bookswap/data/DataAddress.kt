package com.android.bookswap.data

import android.location.Address
import java.util.Locale

class DataAddress() {
  fun toAddress(value: Any): Address {
    return Address(Locale.getDefault())
  }
}
