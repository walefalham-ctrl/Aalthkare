package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HourlyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Trigger High Priority Heads-Up Notification
        NotificationHelper.triggerNotificationNow(
            context = context,
            title = "🌿 تذكير الرقية والأذكار الساعي",
            message = "مضت ساعة.. جدد استشفاءك ووردك اليومي بآيات الرقية والأذكار العظيمة."
        )

        // Reschedule the exact alarm for the next hour (60 mins) if user still has notifications enabled
        if (NotificationHelper.isNotificationEnabled(context)) {
            NotificationHelper.scheduleHourlyNotification(context)
        }
    }
}
