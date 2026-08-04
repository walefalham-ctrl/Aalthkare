package com.example.utils

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class CityLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String = ""
)

data class SunTimes(
    val sunriseHour: Int,
    val sunriseMinute: Int,
    val sunsetHour: Int,
    val sunsetMinute: Int,
    val sunriseFormatted: String,
    val sunsetFormatted: String
)

object SunCalculator {

    val PRESET_CITIES = listOf(
        CityLocation("مكة المكرمة", 21.3891, 39.8579, "السعودية"),
        CityLocation("المدينة المنورة", 24.5247, 39.5692, "السعودية"),
        CityLocation("الرياض", 24.7136, 46.6753, "السعودية"),
        CityLocation("جدة", 21.5433, 39.1728, "السعودية"),
        CityLocation("القاهرة", 30.0444, 31.2357, "مصر"),
        CityLocation("القدس الشريف", 31.7683, 35.2137, "فلسطين"),
        CityLocation("دبي", 25.2048, 55.2708, "الإمارات"),
        CityLocation("أبوظبي", 24.4539, 54.3773, "الإمارات"),
        CityLocation("الكويت", 29.3759, 47.9774, "الكويت"),
        CityLocation("الدوحة", 25.2854, 51.5310, "قطر"),
        CityLocation("مسقط", 23.5880, 58.3829, "عمان"),
        CityLocation("المنامة", 26.2285, 50.5860, "البحرين"),
        CityLocation("عمان", 31.9454, 35.9284, "الأردن"),
        CityLocation("بيروت", 33.8938, 35.5018, "لبنان"),
        CityLocation("دمشق", 33.5138, 36.2765, "سوريا"),
        CityLocation("بغداد", 33.3152, 44.3661, "العراق"),
        CityLocation("الرباط", 34.0209, -6.8416, "المغرب"),
        CityLocation("تونس", 36.8065, 10.1815, "تونس"),
        CityLocation("الجزائر", 36.7538, 3.0588, "الجزائر"),
        CityLocation("الخرطوم", 15.5007, 32.5599, "السودان")
    )

    private const val ZENITH_OFFICIAL = 90.8333 // 90° 50' for official sunrise/sunset

    private fun degToRad(deg: Double): Double = deg * Math.PI / 180.0
    private fun radToDeg(rad: Double): Double = rad * 180.0 / Math.PI

    fun calculateSunTimes(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): SunTimes {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Day of the year
        val N1 = floor(275.0 * month / 9.0)
        val N2 = floor((month + 9.0) / 12.0)
        val N3 = (1.0 + floor((year - 4.0 * floor(year / 4.0) + 2.0) / 3.0))
        val dayOfYear = N1 - (N2 * N3) + day - 30.0

        val timeZoneOffsetHours = timeZone.getOffset(calendar.timeInMillis) / 3600000.0

        // Calculate Sunrise
        val sunriseUtcHours = calculateSunTimeUtc(dayOfYear, latitude, longitude, isSunrise = true)
        val sunsetUtcHours = calculateSunTimeUtc(dayOfYear, latitude, longitude, isSunrise = false)

        val localSunriseHours = normalizeHours(sunriseUtcHours + timeZoneOffsetHours)
        val localSunsetHours = normalizeHours(sunsetUtcHours + timeZoneOffsetHours)

        val srHour = floor(localSunriseHours).toInt()
        val srMin = floor((localSunriseHours - srHour) * 60).toInt()

        val ssHour = floor(localSunsetHours).toInt()
        val ssMin = floor((localSunsetHours - ssHour) * 60).toInt()

        return SunTimes(
            sunriseHour = srHour,
            sunriseMinute = srMin,
            sunsetHour = ssHour,
            sunsetMinute = ssMin,
            sunriseFormatted = formatTime12h(srHour, srMin),
            sunsetFormatted = formatTime12h(ssHour, ssMin)
        )
    }

    private fun calculateSunTimeUtc(
        dayOfYear: Double,
        lat: Double,
        lng: Double,
        isSunrise: Boolean
    ): Double {
        val lngHour = lng / 15.0
        val t = if (isSunrise) {
            dayOfYear + ((6.0 - lngHour) / 24.0)
        } else {
            dayOfYear + ((18.0 - lngHour) / 24.0)
        }

        // Sun's mean anomaly
        val M = (0.9856 * t) - 3.289

        // Sun's true longitude
        var L = M + (1.916 * sin(degToRad(M))) + (0.020 * sin(degToRad(2 * M))) + 282.634
        L = normalizeDeg(L)

        // Sun's right ascension
        var RA = radToDeg(atan(0.91764 * tan(degToRad(L))))
        RA = normalizeDeg(RA)

        // Right ascension value needs to be in the same quadrant as L
        val Lquadrant = floor(L / 90.0) * 90.0
        val RAquadrant = floor(RA / 90.0) * 90.0
        RA += (Lquadrant - RAquadrant)

        // Convert RA to hours
        RA /= 15.0

        // Sun's declination
        val sinDec = 0.39782 * sin(degToRad(L))
        val cosDec = cos(asin(sinDec))

        // Sun's local hour angle
        val cosH = (cos(degToRad(ZENITH_OFFICIAL)) - (sinDec * sin(degToRad(lat)))) / (cosDec * cos(degToRad(lat)))

        val H = if (cosH > 1.0) {
            0.0 // Sun never rises
        } else if (cosH < -1.0) {
            360.0 // Sun never sets
        } else {
            if (isSunrise) {
                360.0 - radToDeg(acos(cosH))
            } else {
                radToDeg(acos(cosH))
            }
        }

        val Hhours = H / 15.0

        // Local Mean Time of transition
        val T = Hhours + RA - (0.06571 * t) - 6.622

        // Universal Time (UTC)
        val UT = T - lngHour
        return normalizeHours(UT)
    }

    private fun normalizeDeg(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    private fun normalizeHours(hours: Double): Double {
        var h = hours % 24.0
        if (h < 0) h += 24.0
        return h
    }

    private fun formatTime12h(hour24: Int, minute: Int): String {
        val period = if (hour24 >= 12) "م" else "ص"
        var h12 = hour24 % 12
        if (h12 == 0) h12 = 12
        val minStr = if (minute < 10) "0$minute" else "$minute"
        return "$h12:$minStr $period"
    }
}
