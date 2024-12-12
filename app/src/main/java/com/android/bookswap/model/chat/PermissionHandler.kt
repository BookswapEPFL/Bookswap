package com.android.bookswap.model.chat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
   * This launcher is registered to handle the result of the permission request. If the permission
   * is granted, notifications are enabled. If the permission is denied, the user is informed and a
   * rationale dialog is shown.
   */
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  val requestPermissionLauncher: ActivityResultLauncher<String> =
      activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
          isGranted: Boolean ->
        Log.d("PermissionHandler", "Permission result: $isGranted")
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
          // Directly request the notification permission
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    }
  }
  /** Enables notifications by initializing the FCM SDK and showing a confirmation message. */
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
    // Code to inform the user that notifications are disabled
    // For example, you might show a Toast or a Snackbar
    // This is a placeholder implementation
    Toast.makeText(
            activity,
            "Notifications are disabled. You will not receive updates.",
            Toast.LENGTH_LONG)
        .show()
  }
  /**
   * Shows a rationale dialog to the user explaining why notification permission is required.
   *
   * This dialog is displayed when the user denies the notification permission request. It provides
   * an explanation and prompts the user to grant the permission.
   */
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private fun showRationaleDialog() {
    AlertDialog.Builder(activity)
        .setTitle("Notification Permission Required")
        .setMessage(
            "To keep you informed about important updates, please allow notification permissions.")
        .setPositiveButton("OK") { _, _ ->
          // Request the permission when the user agrees
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        .setNegativeButton("No thanks") { dialog, _ ->
          // Dismiss the dialog and continue without asking for permission
          dialog.dismiss()
        }
        .create()
        .show()
  }
}
