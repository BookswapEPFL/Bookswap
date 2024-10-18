package com.android.bookswap.model.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.android.bookswap.MainActivity
import com.android.bookswap.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/** Service to handle Firebase Cloud Messaging notifications. */
const val DEFAULT_TITLE = "Hello There!"
const val DEFAULT_BODY = "You have a new notification"

open class MyFirebaseMessagingService : FirebaseMessagingService() {
  /**
   * Called when a message is received.
   *
   * @param remoteMessage the message received from Firebase Cloud Messaging.
   */
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    val notification = remoteMessage.notification
    val title = notification?.title ?: DEFAULT_TITLE
    val body = notification?.body ?: DEFAULT_BODY
    sendNotification(title, body)
  }

  /**
   * Sends a notification to the user.
   *
   * @param title the title of the notification.
   * @param messageBody the body of the notification.
   */
  fun sendNotification(title: String, messageBody: String) {
    // Create an intent to open the MainActivity when the notification is clicked
    val intent =
        Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
    val pendingIntent =
        PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
    // Create intent and PendingIntent for the "Accept" action
    val acceptIntent =
        Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_ACCEPT" }
    val acceptPendingIntent =
        PendingIntent.getBroadcast(
            this,
            0,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    // Create intent and PendingIntent for the "Decline" action
    val declineIntent =
        Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_DECLINE" }
    val declinePendingIntent =
        PendingIntent.getBroadcast(
            this,
            1,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    // Define a meaningful channel ID and channel name
    val channelId = "bookswap_notifications_channel"
    val channelName = "BookSwap Notifications"

    // Build the notification
    val notificationBuilder =
        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_launcher_foreground, "Accept", acceptPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Decline", declinePendingIntent)

    // Get the NotificationManager
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    // Create the notification channel
    createNotificationChannel(notificationManager, channelId, channelName)

    // Use a unique ID for each notification to avoid overwriting
    val notificationId = System.currentTimeMillis().toInt()
    notificationManager.notify(notificationId, notificationBuilder.build())
  }

  /**
   * Creates a notification channel for Android O and above.
   *
   * @param notificationManager the NotificationManager to create the channel.
   * @param channelId the ID of the notification channel.
   * @param channelName the name of the notification channel.
   */
  fun createNotificationChannel(
      notificationManager: NotificationManager,
      channelId: String,
      channelName: String
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel =
          NotificationChannel(channelId, channelName, importance).apply {
            description = "Channel for BookSwap notifications"
            enableLights(true)

            lightColor = android.graphics.Color.RED
            enableVibration(true)
          }
      notificationManager.createNotificationChannel(channel)
    }
  }
}
