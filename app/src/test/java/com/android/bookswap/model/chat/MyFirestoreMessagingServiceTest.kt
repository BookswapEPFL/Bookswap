package com.android.bookswap.model.chat

import android.app.NotificationManager
import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class MyFirebaseMessagingServiceTest {

  private lateinit var service: MyFirebaseMessagingService

  @Before
  fun setup() {
    // Initialize the service with Robolectric
    service = Robolectric.setupService(MyFirebaseMessagingService::class.java)
  }

  @Test
  fun testSendNotification_createsNotification() {
    // Mock RemoteMessage to pass to the service
    val remoteMessage = RemoteMessage.Builder("testSender").setMessageId("testMessageId").build()

    // Trigger the notification
    service.onMessageReceived(remoteMessage)

    // Use ShadowNotificationManager to get the notification manager
    val notificationManager =
        Shadows.shadowOf(
            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

    // Assert that a notification was posted
    val postedNotifications = notificationManager.allNotifications
    assertNotNull("Notification should have been posted", postedNotifications)
    assertTrue("Notification list should not be empty", postedNotifications.isNotEmpty())
  }

  @Test
  fun testOnMessageReceived_withNotificationPayload() {
    // Mock RemoteMessage with notification payload
    val remoteMessage = RemoteMessage.Builder("testSender").setMessageId("testMessageId").build()

    // Trigger the notification
    service.onMessageReceived(remoteMessage)

    // Use ShadowNotificationManager to get the notification manager
    val notificationManager =
        Shadows.shadowOf(
            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
  }
}
