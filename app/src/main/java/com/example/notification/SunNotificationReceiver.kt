package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SunNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE) ?: TYPE_MORNING

        val (title, message) = if (type == TYPE_MORNING) {
            Pair(
                "🌅 حان وقت أذكار الصباح (شروق الشمس)",
                "أشرق يومك بذكر الله تعالى! اقرأ أذكار الصباح الآن للتحصين والانشراح والأجر العظيم."
            )
        } else {
            Pair(
                "🌇 حان وقت أذكار المساء (غروب الشمس)",
                "اقترب غروب الشمس! حصّن نفسك وأهلك وقوي إيمانك بأذكار المساء المأثورة."
            )
        }

        NotificationHelper.triggerNotificationNow(
            context = context,
            title = title,
            message = message
        )

        // Automatically reschedule next sunrise / sunset reminders for the following day
        if (NotificationHelper.isSunReminderEnabled(context)) {
            val location = NotificationHelper.getSavedLocation(context)
            NotificationHelper.scheduleSunReminders(context, location.latitude, location.longitude)
        }
    }

    companion object {
        const val EXTRA_TYPE = "EXTRA_SUN_REMINDER_TYPE"
        const val TYPE_MORNING = "MORNING"
        const val TYPE_EVENING = "EVENING"
    }
}
