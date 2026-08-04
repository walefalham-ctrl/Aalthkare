package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

object NotificationHelper {

    private const val PREFS_NAME = "hourly_ruqyah_prefs"
    private const val KEY_RINGTONE_URI = "ringtone_uri"
    private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    private const val KEY_SUN_REMINDER_ENABLED = "sun_reminder_enabled"
    private const val KEY_CITY_NAME = "city_name"
    private const val KEY_LATITUDE = "latitude"
    private const val KEY_LONGITUDE = "longitude"
    private const val KEY_IS_GPS_AUTO = "is_gps_auto"

    private const val BASE_CHANNEL_ID = "hourly_ruqyah_channel"
    const val ALARM_REQUEST_CODE = 1001
    const val SUNRISE_ALARM_REQUEST_CODE = 2001
    const val SUNSET_ALARM_REQUEST_CODE = 2002
    const val NOTIFICATION_ID = 3003

    fun getSelectedRingtoneUri(context: Context): Uri {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedUriStr = prefs.getString(KEY_RINGTONE_URI, null)
        return if (!savedUriStr.isNull_or_Empty()) {
            Uri.parse(savedUriStr)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        }
    }

    private fun String?.isNull_or_Empty(): Boolean = this == null || this.isEmpty()

    fun saveSelectedRingtone(context: Context, uri: Uri?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_RINGTONE_URI, uri?.toString()).apply()
        // Re-create notification channel with the new sound URI
        createNotificationChannel(context)
    }

    fun getRingtoneTitle(context: Context, uri: Uri? = null): String {
        val targetUri = uri ?: getSelectedRingtoneUri(context)
        return try {
            val ringtone = RingtoneManager.getRingtone(context, targetUri)
            val title = ringtone?.getTitle(context)
            if (!title.isNullOrEmpty()) title else "النغمة الافتراضية للجيهاز"
        } catch (e: Exception) {
            "النغمة الافتراضية"
        }
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_NOTIFICATION_ENABLED, false)
    }

    fun setNotificationEnabledState(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }

    fun getChannelId(context: Context): String {
        val uri = getSelectedRingtoneUri(context)
        return "${BASE_CHANNEL_ID}_${uri.hashCode()}"
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val soundUri = getSelectedRingtoneUri(context)
            val channelId = getChannelId(context)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channelName = "تنبيهات الرقية والأذكار الساعية"
            val channelDescription = "إشعارات تذكيرية عائمة عالية الأولوية كل 60 دقيقة للذكر والاستشفاء"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                enableLights(true)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleHourlyNotification(context: Context) {
        createNotificationChannel(context)
        setNotificationEnabledState(context, true)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HourlyNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule next alarm in 60 minutes (60 * 60 * 1000 ms)
        val triggerAtMillis = System.currentTimeMillis() + (60 * 60 * 1000L)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelHourlyNotification(context: Context) {
        setNotificationEnabledState(context, false)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HourlyNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun triggerNotificationNow(
        context: Context,
        title: String = "🌿 حان موعد ذِكْر وِرْدِك الساعي",
        message: String = "جدّد نيتك واذكر الله مع آيات الرقية والسكينة والاستشفاء."
    ) {
        createNotificationChannel(context)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = getChannelId(context)
        val soundUri = getSelectedRingtoneUri(context)

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setContentIntent(contentPendingIntent)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun isSunReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SUN_REMINDER_ENABLED, false)
    }

    fun setSunReminderEnabledState(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SUN_REMINDER_ENABLED, enabled).apply()
    }

    fun saveSavedLocation(
        context: Context,
        cityName: String,
        latitude: Double,
        longitude: Double,
        isGpsAuto: Boolean = false
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_CITY_NAME, cityName)
            .putFloat(KEY_LATITUDE, latitude.toFloat())
            .putFloat(KEY_LONGITUDE, longitude.toFloat())
            .putBoolean(KEY_IS_GPS_AUTO, isGpsAuto)
            .apply()
    }

    fun getSavedLocation(context: Context): com.example.utils.CityLocation {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val defaultCity = com.example.utils.SunCalculator.PRESET_CITIES.first()
        val name = prefs.getString(KEY_CITY_NAME, defaultCity.name) ?: defaultCity.name
        val lat = prefs.getFloat(KEY_LATITUDE, defaultCity.latitude.toFloat()).toDouble()
        val lng = prefs.getFloat(KEY_LONGITUDE, defaultCity.longitude.toFloat()).toDouble()
        return com.example.utils.CityLocation(name, lat, lng)
    }

    fun isGpsLocationAuto(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_GPS_AUTO, false)
    }

    fun scheduleSunReminders(context: Context, latitude: Double, longitude: Double) {
        createNotificationChannel(context)
        setSunReminderEnabledState(context, true)

        val sunTimes = com.example.utils.SunCalculator.calculateSunTimes(latitude, longitude)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule Sunrise Alarm (Morning Azkar)
        val sunriseCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, sunTimes.sunriseHour)
            set(java.util.Calendar.MINUTE, sunTimes.sunriseMinute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        val sunriseIntent = Intent(context, SunNotificationReceiver::class.java).apply {
            putExtra(SunNotificationReceiver.EXTRA_TYPE, SunNotificationReceiver.TYPE_MORNING)
        }
        val sunrisePendingIntent = PendingIntent.getBroadcast(
            context,
            SUNRISE_ALARM_REQUEST_CODE,
            sunriseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setExactAlarm(alarmManager, sunriseCal.timeInMillis, sunrisePendingIntent)

        // Schedule Sunset Alarm (Evening Azkar)
        val sunsetCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, sunTimes.sunsetHour)
            set(java.util.Calendar.MINUTE, sunTimes.sunsetMinute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        val sunsetIntent = Intent(context, SunNotificationReceiver::class.java).apply {
            putExtra(SunNotificationReceiver.EXTRA_TYPE, SunNotificationReceiver.TYPE_EVENING)
        }
        val sunsetPendingIntent = PendingIntent.getBroadcast(
            context,
            SUNSET_ALARM_REQUEST_CODE,
            sunsetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setExactAlarm(alarmManager, sunsetCal.timeInMillis, sunsetPendingIntent)
    }

    private fun setExactAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelSunReminders(context: Context) {
        setSunReminderEnabledState(context, false)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val sunriseIntent = Intent(context, SunNotificationReceiver::class.java)
        val sunrisePendingIntent = PendingIntent.getBroadcast(
            context,
            SUNRISE_ALARM_REQUEST_CODE,
            sunriseIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        sunrisePendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }

        val sunsetIntent = Intent(context, SunNotificationReceiver::class.java)
        val sunsetPendingIntent = PendingIntent.getBroadcast(
            context,
            SUNSET_ALARM_REQUEST_CODE,
            sunsetIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        sunsetPendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
