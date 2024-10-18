package com.android.bookswap.model.chat

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class TestApplicationChat : Application() {
  override fun onCreate() {
    super.onCreate()
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
  }
}
