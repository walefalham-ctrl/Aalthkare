package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ObservationLogEntity
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber700
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ObservationLogCard(
    currentNote: String,
    selectedMood: String,
    observationLogs: List<ObservationLogEntity>,
    onNoteChange: (String) -> Unit,
    onMoodSelect: (String) -> Unit,
    onSaveLog: () -> Unit,
    onDeleteLog: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val moodTags = listOf(
        "سكينة وراحة 🌿",
        "انشراح صدر ✨",
        "زوال ألم 💚",
        "تأثر وخشوع 📖",
        "تحسن مستمر 🌟"
    )

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
                        imageVector = Icons.Default.NoteAlt,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "سجل ملاحظات وآثار الرقية اليومية",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Emerald100)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "محفوظ بـ Room 💾",
                        fontSize = 10.sp,
                        color = Emerald800,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "سجّل ما لاحظته أو شعرت به بعد جلسة الاستشفاء (ألم خف، طمأنينة، راحة، إلخ):",
                fontSize = 11.sp,
                color = Slate500,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mood / Effect Quick Tags
            Text(
                text = "اختر الحالة / الأثر المشاهد:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                moodTags.forEach { mood ->
                    val isSelected = mood == selectedMood
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Emerald800 else Emerald50)
                            .clickable { onMoodSelect(mood) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = mood,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Emerald800
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text Input
            OutlinedTextField(
                value = currentNote,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("observation_note_input"),
                placeholder = {
                    Text(
                        text = "اكتب تفاصيل الملاحظة أو الشعور هنا...",
                        fontSize = 12.sp,
                        color = Slate500
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Emerald50.copy(alpha = 0.3f),
                    unfocusedContainerColor = Emerald50.copy(alpha = 0.15f),
                    focusedBorderColor = Emerald700,
                    unfocusedBorderColor = Slate200
                ),
                maxLines = 3,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save Button
            Button(
                onClick = onSaveLog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("save_observation_log_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "حفظ الملاحظة في قاعدة البيانات",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Timeline List of Stored Observations
            if (observationLogs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الملاحظات المسجلة سابقاً (${observationLogs.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    observationLogs.take(5).forEach { log ->
                        val timeFormatted = remember(log.timestamp) {
                            val sdf = SimpleDateFormat("HH:mm", Locale.US)
                            sdf.format(Date(log.timestamp))
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = BorderStroke(1.dp, Slate200)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Emerald100)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = log.moodTag,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Emerald800
                                            )
                                        }

                                        Text(
                                            text = "${log.date} • $timeFormatted",
                                            fontSize = 10.sp,
                                            color = Slate500
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = log.notes,
                                        fontSize = 12.sp,
                                        color = Slate800,
                                        lineHeight = 17.sp
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteLog(log.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف الملاحظة",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
