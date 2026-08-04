package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.utils.AppHealthSummary
import com.example.utils.DiagnosticEntry
import com.example.utils.DiagnosticsLogger
import com.example.utils.HealthStatus
import com.example.utils.LogLevel

@Composable
fun AppHealthDiagnosticsCard(
    modifier: Modifier = Modifier,
    onShowToast: (String) -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    var healthSummary by remember { mutableStateOf(DiagnosticsLogger.getAppHealthSummary()) }
    var logsList by remember { mutableStateOf(DiagnosticsLogger.getLogs()) }
    var showLogsDialog by remember { mutableStateOf(false) }

    val statusBgColor = when (healthSummary.status) {
        HealthStatus.EXCELLENT -> Emerald100
        HealthStatus.ATTENTION -> Color(0xFFFEF3C7)
        HealthStatus.CRITICAL -> Color(0xFFFEE2E2)
    }

    val statusTextColor = when (healthSummary.status) {
        HealthStatus.EXCELLENT -> Emerald800
        HealthStatus.ATTENTION -> Color(0xFF92400E)
        HealthStatus.CRITICAL -> Color(0xFF991B1B)
    }

    val statusIcon = when (healthSummary.status) {
        HealthStatus.EXCELLENT -> Icons.Default.CheckCircle
        HealthStatus.ATTENTION -> Icons.Default.Warning
        HealthStatus.CRITICAL -> Icons.Default.BugReport
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card Header
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
                            .background(Emerald100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "فحص حالة التطبيق ومعالجة الأخطاء",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "نظام الفحص الاستباقي للتعليق والخرج المفاجئ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (healthSummary.status) {
                                HealthStatus.EXCELLENT -> "ممتاز 🟢"
                                HealthStatus.ATTENTION -> "تنبيه 🟡"
                                HealthStatus.CRITICAL -> "تحذير 🔴"
                            },
                            fontSize = 10.sp,
                            color = statusTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // System Performance & Memory Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Memory Metric Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "الذاكرة المخصصة",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${healthSummary.usedMemoryMb} MB / ${healthSummary.maxMemoryMb} MB",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Performance & Coroutines Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "استجابة الخيوط (ANR)",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "خلفية Coroutines ⚡",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = healthSummary.message,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        logsList = DiagnosticsLogger.getLogs()
                        healthSummary = DiagnosticsLogger.getAppHealthSummary()
                        showLogsDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("view_diagnostics_logs_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "عرض سجل التشخيصات (${healthSummary.totalLogsCount})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        healthSummary = DiagnosticsLogger.getAppHealthSummary()
                        logsList = DiagnosticsLogger.getLogs()
                        onShowToast("تم تحديث وفحص حالة سلامة التطبيق بنجاح 🔄")
                    },
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("refresh_health_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Emerald700)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Diagnostics Logs Dialog
    if (showLogsDialog) {
        AlertDialog(
            onDismissRequest = { showLogsDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = Emerald700,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "سجل التتبع الفني والتشخيصات 🩺",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "سجل الأخطاء والعمليات الاستباقية لمنع الخروج المفاجئ وتجمد الواجهة:",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (logsList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لا توجد أخطاء أو أحداث تشخيصية مسجلة ✨",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(logsList) { entry ->
                                val levelColor = when (entry.level) {
                                    LogLevel.INFO -> Emerald800
                                    LogLevel.WARNING -> Color(0xFFD97706)
                                    LogLevel.ERROR -> Color(0xFFDC2626)
                                    LogLevel.CRASH -> Color(0xFF991B1B)
                                }

                                val levelBg = when (entry.level) {
                                    LogLevel.INFO -> Emerald100
                                    LogLevel.WARNING -> Color(0xFFFEF3C7)
                                    LogLevel.ERROR -> Color(0xFFFEE2E2)
                                    LogLevel.CRASH -> Color(0xFFFECDD3)
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(levelBg)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${entry.level.name} | ${entry.tag}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = levelColor
                                            )
                                        }

                                        Text(
                                            text = entry.timestamp,
                                            fontSize = 9.sp,
                                            color = Slate500,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = entry.message,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Slate800
                                    )

                                    if (!entry.stackTrace.isNull_or_empty_custom()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = entry.stackTrace ?: "",
                                            fontSize = 8.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.Red,
                                            maxLines = 3
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reportText = buildString {
                            append("--- تقرير حالة التطبيق والتشخيصات ---\n")
                            append("الحالة: ${healthSummary.status.name}\n")
                            append("الذاكرة: ${healthSummary.usedMemoryMb} MB / ${healthSummary.maxMemoryMb} MB\n\n")
                            logsList.forEach {
                                append("[${it.timestamp}] [${it.level}] [${it.tag}] ${it.message}\n")
                                if (!it.stackTrace.isNullOrEmpty()) {
                                    append("${it.stackTrace}\n")
                                }
                            }
                        }
                        clipboardManager.setText(AnnotatedString(reportText))
                        onShowToast("تم نسخ التقرير التشخيصي الكامل للحافظة 📋")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نسخ التقرير 📋")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            DiagnosticsLogger.clearLogs()
                            DiagnosticsLogger.clearLastCrash()
                            logsList = emptyList()
                            healthSummary = DiagnosticsLogger.getAppHealthSummary()
                            onShowToast("تم مسح كافة سجلات الأخطاء 🧹")
                        }
                    ) {
                        Text("مسح السجل 🧹", color = Color(0xFFDC2626))
                    }
                    TextButton(onClick = { showLogsDialog = false }) {
                        Text("إغلاق")
                    }
                }
            }
        )
    }
}

private fun String?.isNull_or_empty_custom(): Boolean = this == null || this.trim().isEmpty()
