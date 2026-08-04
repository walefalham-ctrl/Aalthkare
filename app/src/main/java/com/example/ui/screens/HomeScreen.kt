package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskType
import com.example.ui.RuqyahUiState
import com.example.ui.components.AppHealthDiagnosticsCard
import com.example.ui.components.DarkModeSettingsCard
import com.example.ui.components.ExportObservationLogsCard
import com.example.ui.components.FamilyDuaaCard
import com.example.ui.components.HourlyNotificationSettingsCard
import com.example.ui.components.ObservationLogCard
import com.example.ui.components.SunReminderSettingsCard
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800

@Composable
fun HomeScreen(
    uiState: RuqyahUiState,
    onToggleTask: (TaskType) -> Unit,
    onUpdateEffectNote: (String) -> Unit,
    onSaveRecord: () -> Unit,
    onUpdateObservationNote: (String) -> Unit = {},
    onSelectObservationMood: (String) -> Unit = {},
    onAddObservationLog: () -> Unit = {},
    onDeleteObservationLog: (Int) -> Unit = {},
    onShowToast: (String) -> Unit = {},
    onToggleFamilyDuaa: (Boolean) -> Unit = {},
    onToggleDarkMode: (Boolean) -> Unit = {},
    onExportLogs: () -> Unit = {},
    onDismissExportDialog: () -> Unit = {},
    onImportLogsFromJson: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val tasks = TaskType.entries
    val record = uiState.dailyRecord

    val completedCount = listOf(
        record.azkarDone,
        record.baqarahDone,
        record.ruqyahDone,
        record.sadakahDone,
        record.wirdDone,
        record.namesDone
    ).count { it }

    val totalCount = tasks.size
    val progressPercent = if (totalCount > 0) (completedCount * 100) / totalCount else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Emerald800, Emerald700)
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "مرحباً بك يا محمد 🌿",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "«وَإِذَا مَرِضْتُ فَهُوَ يَشْفِينِ» - حافظ على وردك بثبات، والأثر يأتي بالاستمرار واليقين.",
                        color = Emerald100,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نسبة الإنجاز اليومي",
                            color = Emerald100,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$progressPercent%",
                            color = Amber300,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Progress Bar Track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Emerald900.copy(alpha = 0.5f))
                            .border(0.5.dp, Emerald600.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progressPercent / 100f)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(Amber500)
                        )
                    }
                }
            }
        }

        // Checklist Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "قائمة المهام اليومية",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }

                    Text(
                        text = "$completedCount / $totalCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500
                    )
                }

                tasks.forEach { task ->
                    val isChecked = when (task) {
                        TaskType.AZKAR -> record.azkarDone
                        TaskType.BAQARAH -> record.baqarahDone
                        TaskType.RUQYAH -> record.ruqyahDone
                        TaskType.SADAKAH -> record.sadakahDone
                        TaskType.WIRD -> record.wirdDone
                        TaskType.NAMES -> record.namesDone
                    }

                    val subtitleText = if (task == TaskType.BAQARAH) {
                        "اليوم: ${uiState.baqarahPageRange}"
                    } else task.subtitle

                    TaskItemRow(
                        title = task.title,
                        subtitle = subtitleText,
                        isChecked = isChecked,
                        onToggle = { onToggleTask(task) }
                    )
                }
            }
        }

        // Family Duaa Protection Card (دعاء حفظ الزوجة نوره والابن نهار)
        FamilyDuaaCard(
            isCompleted = uiState.isFamilyDuaaDone,
            onToggleCompleted = onToggleFamilyDuaa
        )

        // Dark Mode Settings Card (الوضع الليلي لحماية العين)
        DarkModeSettingsCard(
            isDarkMode = uiState.isDarkMode,
            onToggleDarkMode = onToggleDarkMode
        )

        // Hourly Notification Settings Card (التنبيهات الساعية النغمة والمواعيد)
        HourlyNotificationSettingsCard(
            onShowToast = onShowToast
        )

        // Smart Sunrise & Sunset Reminder Settings Card (التذكير الذكي بالشروق والغروب)
        SunReminderSettingsCard(
            onShowToast = onShowToast
        )

        // App Health & Error Handling Diagnostics Card (فحص حالة التطبيق ومعالجة الأخطاء)
        AppHealthDiagnosticsCard(
            onShowToast = onShowToast
        )

        // Observation Log Card (سجل الملاحظات والآثار اليومية)
        ObservationLogCard(
            currentNote = uiState.currentObservationNote,
            selectedMood = uiState.selectedObservationMood,
            observationLogs = uiState.observationLogs,
            onNoteChange = onUpdateObservationNote,
            onMoodSelect = onSelectObservationMood,
            onSaveLog = onAddObservationLog,
            onDeleteLog = onDeleteObservationLog
        )

        // Export & Import Observation Logs Card (تصدير واسترجاع نسخة احتياطية من الملاحظات)
        ExportObservationLogsCard(
            logsCount = uiState.observationLogs.size,
            exportedFilePath = uiState.exportedBackupPath,
            exportedFileContent = uiState.exportedBackupContent,
            showExportDialog = uiState.showExportDialog,
            onExportLogs = onExportLogs,
            onDismissExportDialog = onDismissExportDialog,
            onImportLogsFromJson = onImportLogsFromJson,
            onShowToast = onShowToast
        )
    }
}

@Composable
private fun TaskItemRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isChecked) Emerald50 else Color(0xFFF8FAFC),
        label = "taskBg"
    )

    val checkBgColor by animateColorAsState(
        targetValue = if (isChecked) Emerald700 else Color.White,
        label = "checkBg"
    )

    val borderColor = if (isChecked) Emerald600 else Slate200

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onToggle() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(checkBgColor)
                    .border(
                        1.1.dp,
                        if (isChecked) Emerald700 else Slate500,
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = if (title.contains("البقرة")) Emerald700 else Slate500,
                    fontWeight = if (title.contains("البقرة")) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = Slate500,
            modifier = Modifier.size(16.dp)
        )
    }
}
