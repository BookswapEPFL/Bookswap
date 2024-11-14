package com.android.bookswap.model.chat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

class PermissionHandler(private val activity: ComponentActivity) {
  /**
   * Launcher for requesting notification permission.
   *
   * @RequiresApi(Build.VERSION_CODES.TIRAMISU)
   */
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  val requestPermissionLauncher: ActivityResultLauncher<String> =
      activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
          isGranted: Boolean ->
        if (isGranted) {
          enableNotifications()
        } else {
          informUserNotificationsDisabled()
          showRationaleDialog()
        }
      }
  /**
   * Requests notification permission from the user.
   *
   * This function checks if the API level is 33 or higher (Android 13+). If the permission is
   * already granted, it enables notifications. Otherwise, it directly requests the notification
   * permission.
   */
  fun askNotificationPermission() {
    // Check if the API level is 33 or higher (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      when {
        // Case 1: Permission already granted
        ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED -> {
          // FCM SDK (and your app) can post notifications.
          enableNotifications()
        }
        // Case 2: No need to show rationale, directly request permission
        else -> {
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    }
  }
  /**
   * Enables notifications by initializing the FCM SDK.
   *
   * This function sets the `isAutoInitEnabled` property of the FirebaseMessaging instance to true,
   * which allows the app to automatically initialize Firebase Cloud Messaging (FCM) and receive
   * notifications. It also displays a Toast message to inform the user that notifications have been
   * enabled.
   */
  fun enableNotifications() {
    // Initialize FCM SDK
    FirebaseMessaging.getInstance().isAutoInitEnabled = true
    // Additional setup if needed
    Toast.makeText(activity, "Notifications have been enabled.", Toast.LENGTH_LONG).show()
  }
  /**
   * Informs the user that notifications are disabled.
   *
   * This function displays a Toast message to inform the user that notifications are disabled and
   * they will not receive updates.
   */
  fun informUserNotificationsDisabled() {
    Toast.makeText(
            activity,
            "Notifications are disabled. You will not receive updates.",
            Toast.LENGTH_LONG)
        .show()
  }
  /**
   * Shows a rationale dialog to the user explaining why notification permission is required.
   *
   * This function displays an AlertDialog with a message explaining the importance of notification
   * permissions. If the user agrees, the permission request is launched again. If the user
   * declines, the dialog is dismissed.
   */
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private fun showRationaleDialog() {
    AlertDialog.Builder(activity)
        .setTitle("Notification Permission Required")
        .setMessage(
            "To keep you informed about important updates, please allow notification permissions.")
        .setPositiveButton("OK") { _, _ ->
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        .setNegativeButton("No thanks") { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
  }
}
