package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.notification.NotificationHelper
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800

@Composable
fun HourlyNotificationSettingsCard(
    modifier: Modifier = Modifier,
    onShowToast: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var isEnabled by remember {
        mutableStateOf(NotificationHelper.isNotificationEnabled(context))
    }

    var selectedRingtoneTitle by remember {
        mutableStateOf(NotificationHelper.getRingtoneTitle(context))
    }

    // Launcher for System Ringtone Picker Intent
    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                    Uri::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }

            NotificationHelper.saveSelectedRingtone(context, uri)
            selectedRingtoneTitle = NotificationHelper.getRingtoneTitle(context, uri)
            onShowToast("تم حفظ وتحديث نغمة الإشعارات بنجاح 🎵")
        }
    }

    // Permission launcher for Android 13+ (POST_NOTIFICATIONS)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.scheduleHourlyNotification(context)
            isEnabled = true
            onShowToast("تم تفعيل التنبيهات الساعية في الخلفية 🔔")
        } else {
            onShowToast("يتطلب تفعيل التنبيهات منح إذن الإشعارات")
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Emerald100)
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
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التنبيهات الساعية التكرارية",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isEnabled) Emerald100 else Slate200)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isEnabled) "نشط كل 60 دقيقة ⚡" else "متوقف ⏸️",
                        fontSize = 10.sp,
                        color = if (isEnabled) Emerald800 else Slate500,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "يصُدر إشعار عائم بـ High Priority كل ساعة لتذكيرك بورد الرقية والاستغفار والذكر في الخلفية.",
                fontSize = 11.sp,
                color = Slate500,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Enable Toggle Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Emerald50)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "تفعيل التنبيه التلقائي كل ساعة",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Text(
                        text = if (isEnabled) "يعمل في الخلفية تلقائياً ويستعيد نفسه عند إعادة تشغيل الهاتف" else "اضغط لتفعيل التذكير كل 60 دقيقة",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        if (checked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                NotificationHelper.scheduleHourlyNotification(context)
                                isEnabled = true
                                onShowToast("تم تفعيل التنبيهات الساعية بنجاح 🔔")
                            }
                        } else {
                            NotificationHelper.cancelHourlyNotification(context)
                            isEnabled = false
                            onShowToast("تم إيقاف التنبيهات الساعية ⏸️")
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Emerald700,
                        uncheckedThumbColor = Slate500,
                        uncheckedTrackColor = Slate200
                    ),
                    modifier = Modifier.testTag("hourly_notification_switch")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ringtone Picker Section
            Text(
                text = "نغمة التنبيه المميزة (Ringtone):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = selectedRingtoneTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "نغمة النظام المعينة لقناة الإشعارات High Importance",
                            fontSize = 9.sp,
                            color = Slate500
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        val currentUri = NotificationHelper.getSelectedRingtoneUri(context)
                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                            putExtra(
                                RingtoneManager.EXTRA_RINGTONE_TYPE,
                                RingtoneManager.TYPE_NOTIFICATION or RingtoneManager.TYPE_RINGTONE
                            )
                            putExtra(
                                RingtoneManager.EXTRA_RINGTONE_TITLE,
                                "اختر نغمة التنبيه المميزة"
                            )
                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentUri)
                        }
                        ringtonePickerLauncher.launch(intent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Emerald700),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald700),
                    modifier = Modifier.testTag("select_ringtone_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "اختر نغمة 🎵",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Test Button to fire Heads-Up Notification instantly
            Button(
                onClick = {
                    NotificationHelper.triggerNotificationNow(
                        context = context,
                        title = "⚡ اختبار الإشعار العائم (High Priority)",
                        message = "هذا اختبار للإشعار العائم التكراري بالنغمة المختارة: $selectedRingtoneTitle 🌿"
                    )
                    onShowToast("تم إرسال إشعار تجريبي عائم عالي الأولوية 🚀")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("test_notification_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber500)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تجربة الإشعار العائم بالنغمة المختارة الآن 🚀",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
