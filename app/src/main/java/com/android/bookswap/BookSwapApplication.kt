package com.android.bookswap

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class BookSwapApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationChannel= NotificationChannel(
            NOTIFICATION_CHANNEL,
            APP_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL = "bookswap_notification"
        const val APP_NAME = "BookSwapApp" // Can't be a string resource, since no context exist at time of app creationg

    }
}