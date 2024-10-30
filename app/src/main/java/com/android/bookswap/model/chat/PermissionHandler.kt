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

  fun enableNotifications() {
    // Initialize FCM SDK
    FirebaseMessaging.getInstance().isAutoInitEnabled = true
    // Additional setup if needed
    Toast.makeText(activity, "Notifications have been enabled.", Toast.LENGTH_LONG).show()
  }

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
