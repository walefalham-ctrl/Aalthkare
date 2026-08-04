package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.HealingVersesData
import com.example.model.RuqyahVerse
import com.example.ui.RuqyahUiState
import com.example.ui.components.ObservationLogCard
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber700
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuranRuqyahScreen(
    uiState: RuqyahUiState,
    onNextVerse: () -> Unit,
    onPrevVerse: () -> Unit,
    onCompleteVerseAndNext: () -> Unit,
    onSelectVerseIndex: (Int) -> Unit,
    onResetVerseProgress: () -> Unit,
    onUpdateObservationNote: (String) -> Unit = {},
    onSelectObservationMood: (String) -> Unit = {},
    onAddObservationLog: () -> Unit = {},
    onDeleteObservationLog: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showGridDialog by remember { mutableStateOf(false) }

    val versesList = HealingVersesData.list
    val currentVerseIndex = uiState.currentRuqyahVerseIndex.coerceIn(0, versesList.size - 1)
    val currentVerse = versesList[currentVerseIndex]

    val completedCount = uiState.completedVerseIds.size
    val isCurrentCompleted = uiState.completedVerseIds.contains(currentVerse.id)
    val progressFraction = (currentVerseIndex + 1).toFloat() / versesList.size.toFloat()
    val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "verseProgress")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Banner for Ruqyah Database Info & Quick Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Emerald800),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "آيات الشفاء والسكينة (٦٤ آية)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald700)
                            .clickable { showGridDialog = true }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = null,
                                tint = Amber300,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "الفهرس الكامل",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "موسوعة آيات الشفاء والسكينة والفرج المكتوبة. يمكنك قراءتها آية آية وضغط علامة الصح (✓) للانتقال التلقائي للآية التالية.",
                    fontSize = 11.sp,
                    color = Emerald100,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar and Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "التقدم الحالي: الآية ${currentVerseIndex + 1} من ${versesList.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Amber300
                    )

                    Text(
                        text = "تم قراءة: $completedCount آية",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Amber500,
                    trackColor = Emerald900
                )
            }
        }

        // Verse-by-Verse Main Reader Card (آية آية مع علامة الصح)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, if (isCurrentCompleted) Emerald600 else Amber100),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Verse Metadata Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald50)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "سورة ${currentVerse.surah} • ${currentVerse.verseRange}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCurrentCompleted) Emerald100 else Amber100)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isCurrentCompleted) "تمت القراءة ✓" else currentVerse.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrentCompleted) Emerald800 else Amber700
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Basmala / Decorative Header if applicable
                Text(
                    text = "﴿ ${currentVerse.id} ﴾",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Amber700,
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(12.dp))

                // The Verse Text (Large Quranic Typography)
                Text(
                    text = currentVerse.text,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Slate800,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Action Button: Complete with Checkmark & Advance to Next Verse! (علامة صح)
                Button(
                    onClick = onCompleteVerseAndNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCurrentCompleted) Emerald800 else Emerald700
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "علامة صح",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (isCurrentCompleted) "تمت القراءة - اضغط للآية التالية ✓" else "قرأت الآية - الانتقال للآية التالية ✓",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Controls (Previous / Next / Index Jump)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevVerse,
                        enabled = currentVerseIndex > 0,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (currentVerseIndex > 0) Emerald700 else Slate200)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "الآية السابقة",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "السابقة", fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = "${currentVerseIndex + 1} / ${versesList.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        modifier = Modifier.clickable { showGridDialog = true }
                    )

                    OutlinedButton(
                        onClick = onNextVerse,
                        enabled = currentVerseIndex < versesList.size - 1,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (currentVerseIndex < versesList.size - 1) Emerald700 else Slate200)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "التالية", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "الآية التالية",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Category Filter Tags to Jump
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أقسام آيات الرقية والاستشفاء:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )

                    if (completedCount > 0) {
                        Text(
                            text = "إعادة ضبط التقدم 🔄",
                            fontSize = 11.sp,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onResetVerseProgress() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categoryAnchors = listOf(
                        "البداية" to 0,
                        "آيات الشفاء (الست)" to 1,
                        "آيات السكينة والطمأنينة" to 7,
                        "أنزل السكينة" to 22,
                        "دعاء واستجابة" to 25,
                        "شرح الصدر" to 44,
                        "الاستعاذة والمعوذات" to 61
                    )

                    categoryAnchors.forEach { (title, targetIdx) ->
                        val isSelected = currentVerseIndex >= targetIdx && (categoryAnchors.indexOfFirst { it.second == targetIdx } == categoryAnchors.size - 1 || currentVerseIndex < categoryAnchors[categoryAnchors.indexOfFirst { it.second == targetIdx } + 1].second)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Emerald800 else Emerald50)
                                .clickable { onSelectVerseIndex(targetIdx) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Emerald800
                            )
                        }
                    }
                }
            }
        }

        // Surah Baqarah Daily Portion Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سورة البقرة (الورد اليومي)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald100)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = uiState.baqarahPageRange,
                            color = Emerald800,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "جدول مقسم بمعدل 7 صفحات يومياً للختم في أسبوع دون مشقة.",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }
        }

        // Observation Log Card
        ObservationLogCard(
            currentNote = uiState.currentObservationNote,
            selectedMood = uiState.selectedObservationMood,
            observationLogs = uiState.observationLogs,
            onNoteChange = onUpdateObservationNote,
            onMoodSelect = onSelectObservationMood,
            onSaveLog = onAddObservationLog,
            onDeleteLog = onDeleteObservationLog
        )
    }

    // Modal Grid Dialog to Select Any of the 64 Verses
    if (showGridDialog) {
        Dialog(onDismissRequest = { showGridDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "فهرس آيات الشفاء والسكينة (٦٤ آية)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )

                        Text(
                            text = "إغلاق ✕",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            modifier = Modifier.clickable { showGridDialog = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.height(340.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(versesList) { idx, v ->
                            val isCompleted = uiState.completedVerseIds.contains(v.id)
                            val isCurrent = idx == currentVerseIndex

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isCurrent -> Amber500
                                            isCompleted -> Emerald600
                                            else -> Emerald50
                                        }
                                    )
                                    .clickable {
                                        onSelectVerseIndex(idx)
                                        showGridDialog = false
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${v.id}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent || isCompleted) Color.White else Emerald800
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
