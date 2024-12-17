package com.android.bookswap.model.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.android.bookswap.R

/** BroadcastReceiver to handle notification actions. */
class NotificationActionReceiver : BroadcastReceiver() {
  /**
   * Called when the BroadcastReceiver is receiving an Intent broadcast.
   *
   * @param context the Context in which the receiver is running.
   * @param intent the Intent being received.
   */
  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      "ACTION_ACCEPT" -> {
        // Handle accept action
        requestCode(context, "Accepted")
      }
      "ACTION_DECLINE" -> {
        // Handle decline action
        requestCode(context, "Declined")
      }
    }
  }
  /**
   * Handles the request code based on the action.
   *
   * @param context the Context in which the receiver is running.
   * @param action the action string received from the notification.
   */
  private fun requestCode(context: Context, action: String) {
    // Implement the logic to handle the request code based on the action
    Toast.makeText(
            context,
            context.getString(R.string.notification_toast_request_action) + action,
            Toast.LENGTH_SHORT)
        .show()
  }
}
