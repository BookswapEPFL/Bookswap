package com.android.bookswap.model

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.android.bookswap.BookSwapApplication
import com.android.bookswap.R
import kotlin.random.Random

/**
 * Service to send notification to users
 *
 * @param context the activity context
 */
class NotificationService(private val context: Context) {
  private val notificationManager = context.getSystemService(NotificationManager::class.java)

  fun sendNotification(title: String, messageBody: String) {
    val notification =
        NotificationCompat.Builder(context, BookSwapApplication.NOTIFICATION_CHANNEL)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.logo5)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

    notificationManager.notify(Random.nextInt(), notification)
  }
}
