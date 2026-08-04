package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            if (NotificationHelper.isNotificationEnabled(context)) {
                NotificationHelper.scheduleHourlyNotification(context)
            }
            if (NotificationHelper.isSunReminderEnabled(context)) {
                val location = NotificationHelper.getSavedLocation(context)
                NotificationHelper.scheduleSunReminders(context, location.latitude, location.longitude)
            }
        }
    }
}
