package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.notification.NotificationHelper
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber700
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.utils.CityLocation
import com.example.utils.SunCalculator

@Composable
fun SunReminderSettingsCard(
    modifier: Modifier = Modifier,
    onShowToast: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var isEnabled by remember {
        mutableStateOf(NotificationHelper.isSunReminderEnabled(context))
    }

    var selectedLocation by remember {
        mutableStateOf(NotificationHelper.getSavedLocation(context))
    }

    var isGpsAuto by remember {
        mutableStateOf(NotificationHelper.isGpsLocationAuto(context))
    }

    var showCityDialog by remember { mutableStateOf(false) }

    val sunTimes = remember(selectedLocation) {
        SunCalculator.calculateSunTimes(selectedLocation.latitude, selectedLocation.longitude)
    }

    // Permission launcher for Location (GPS)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            val loc = getCurrentDeviceLocation(context)
            if (loc != null) {
                val newLocation = CityLocation("موقعي الحالي (GPS)", loc.latitude, loc.longitude)
                selectedLocation = newLocation
                isGpsAuto = true
                NotificationHelper.saveSavedLocation(
                    context,
                    newLocation.name,
                    loc.latitude,
                    loc.longitude,
                    isGpsAuto = true
                )
                if (isEnabled) {
                    NotificationHelper.scheduleSunReminders(context, loc.latitude, loc.longitude)
                }
                onShowToast("تم تحديد الموقع الجغرافي وحساب مواقيت الشروق والغروب بدقة 📍")
            } else {
                onShowToast("تعذر جلب موقع GPS حالياً، تم الاحتفاظ بالمدينة المحددة")
            }
        } else {
            onShowToast("تم رفض إذن الموقع، يمكنك اختيار المدن المتاحة يدوياً")
        }
    }

    // Notification Permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.scheduleSunReminders(
                context,
                selectedLocation.latitude,
                selectedLocation.longitude
            )
            isEnabled = true
            onShowToast("تم تفعيل التذكير الذكي بالشروق والغروب 🌅🌇")
        } else {
            onShowToast("يتطلب التذكير منح إذن الإشعارات")
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Amber100)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Amber100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Amber700,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "التذكير الذكي (الشروق والغروب)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "تنبيهات أذكار الصباح والمساء حسب موقعك الجغرافي",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isEnabled) Emerald100 else Slate200)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isEnabled) "مُفَعّل ⚡" else "متوقف ⏸️",
                        fontSize = 10.sp,
                        color = if (isEnabled) Emerald800 else Slate500,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Switch Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFFBEB))
                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "تفعيل أذكار الصباح والمساء التلقائية",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Text(
                        text = if (isEnabled) "يُصدر إشعار عائم في وقت الشروق والغروب بالضبط" else "اضغط للتفعيل التلقائي حسب مواقيت الشمس",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        if (checked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                NotificationHelper.scheduleSunReminders(
                                    context,
                                    selectedLocation.latitude,
                                    selectedLocation.longitude
                                )
                                isEnabled = true
                                onShowToast("تم تفعيل التذكير الذكي بالشروق والغروب 🌅🌇")
                            }
                        } else {
                            NotificationHelper.cancelSunReminders(context)
                            isEnabled = false
                            onShowToast("تم إيقاف التذكير الذكي بالشروق والغروب ⏸️")
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Amber500,
                        uncheckedThumbColor = Slate500,
                        uncheckedTrackColor = Slate200
                    ),
                    modifier = Modifier.testTag("sun_reminder_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current Sun Times Display (Sunrise & Sunset Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sunrise Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = Amber700,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "شروق الشمس",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Amber700
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sunTimes.sunriseFormatted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "موعد أذكار الصباح 🌅",
                            fontSize = 9.sp,
                            color = Slate500
                        )
                    }
                }

                // Sunset Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF3E8FF))
                        .border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = Color(0xFF7E22CE),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "غروب الشمس",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7E22CE)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sunTimes.sunsetFormatted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "موعد أذكار المساء 🌇",
                            fontSize = 9.sp,
                            color = Slate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location Bar & Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = selectedLocation.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "خط عرض ${String.format("%.2f", selectedLocation.latitude)}° | طول ${String.format("%.2f", selectedLocation.longitude)}°",
                            fontSize = 9.sp,
                            color = Slate500
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Auto GPS Button
                    IconButton(
                        onClick = {
                            val hasCoarse = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            val hasFine = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasCoarse || hasFine) {
                                val loc = getCurrentDeviceLocation(context)
                                if (loc != null) {
                                    val newLocation = CityLocation("موقعي الحالي (GPS)", loc.latitude, loc.longitude)
                                    selectedLocation = newLocation
                                    isGpsAuto = true
                                    NotificationHelper.saveSavedLocation(
                                        context,
                                        newLocation.name,
                                        loc.latitude,
                                        loc.longitude,
                                        isGpsAuto = true
                                    )
                                    if (isEnabled) {
                                        NotificationHelper.scheduleSunReminders(context, loc.latitude, loc.longitude)
                                    }
                                    onShowToast("تم تحديث موقع GPS بنجاح 📍")
                                } else {
                                    onShowToast("جاري البحث عن موقع GPS...")
                                }
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Emerald100)
                            .testTag("gps_auto_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "موقعي الحالي GPS",
                            tint = Emerald800,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Change City Dialog Button
                    OutlinedButton(
                        onClick = { showCityDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("select_city_btn")
                    ) {
                        Text(
                            text = "تغيير المدينة 🏙️",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Test Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        NotificationHelper.triggerNotificationNow(
                            context = context,
                            title = "🌅 اختبار أذكار الصباح (شروق الشمس)",
                            message = "أشرق يومك بذكر الله! اقرأ أذكار الصباح للتحصين والانشراح."
                        )
                        onShowToast("تم إرسال تجربة إشعار أذكار الصباح 🌅")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("test_morning_reminder_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Amber500)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Amber700,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تجربة الصباح 🌅",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Amber700
                    )
                }

                OutlinedButton(
                    onClick = {
                        NotificationHelper.triggerNotificationNow(
                            context = context,
                            title = "<ctrl42> اختبار أذكار المساء (غروب الشمس)",
                            message = "اقترب غروب الشمس! حصّن نفسك وأهلك بأذكار المساء المأثورة."
                        )
                        onShowToast("تم إرسال تجربة إشعار أذكار المساء 🌇")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("test_evening_reminder_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF7E22CE))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF7E22CE),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تجربة المساء 🌇",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7E22CE)
                    )
                }
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Amber700,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "اختر مدينتك لمواقيت الشمس 🏙️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
            },
            text = {
                Column {
                    Text(
                        text = "اختر إحدى العواصم والمدن الإسلامية أدناه لحساب الشروق والغروب بدقة متناهية:",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SunCalculator.PRESET_CITIES.forEach { city ->
                            val isSelected = selectedLocation.name == city.name
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Amber100 else Color.White)
                                    .clickable {
                                        selectedLocation = city
                                        isGpsAuto = false
                                        NotificationHelper.saveSavedLocation(
                                            context,
                                            city.name,
                                            city.latitude,
                                            city.longitude,
                                            isGpsAuto = false
                                        )
                                        if (isEnabled) {
                                            NotificationHelper.scheduleSunReminders(
                                                context,
                                                city.latitude,
                                                city.longitude
                                            )
                                        }
                                        showCityDialog = false
                                        onShowToast("تم تحديد مدينة ${city.name} وتحديث المواقيت 🌅")
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = city.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate800
                                    )
                                    Text(
                                        text = city.country,
                                        fontSize = 10.sp,
                                        color = Slate500
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Amber700,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

private fun getCurrentDeviceLocation(context: Context): Location? {
    return try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        gpsLoc ?: netLoc
    } catch (e: Exception) {
        null
    }
}
