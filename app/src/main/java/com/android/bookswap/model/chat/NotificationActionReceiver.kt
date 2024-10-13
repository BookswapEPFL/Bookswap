package com.android.bookswap.model.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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
      }
      "ACTION_DECLINE" -> {
        // Handle decline action
      }
    }
  }
}
