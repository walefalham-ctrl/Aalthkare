package com.example.ui.components

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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SaveAlt
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

@Composable
fun ExportObservationLogsCard(
    logsCount: Int,
    exportedFilePath: String?,
    exportedFileContent: String?,
    showExportDialog: Boolean,
    onExportLogs: () -> Unit,
    onDismissExportDialog: () -> Unit,
    onImportLogsFromJson: (String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showImportDialog by remember { mutableStateOf(false) }
    var importInputText by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderZip,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "تصدير النسخة الاحتياطية للسجل",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "حفظ الملاحظات في ملف محلي (JSON) لاسترجاعها مستقبلاً",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Emerald100)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$logsCount ملاحظة 💾",
                        fontSize = 10.sp,
                        color = Emerald800,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Export Button
                Button(
                    onClick = onExportLogs,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("export_logs_file_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Icon(
                        imageVector = Icons.Default.SaveAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تصدير لملف محلي 📁",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Import / Restore Button
                OutlinedButton(
                    onClick = { showImportDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("import_logs_file_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Emerald700),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald700)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "استرجاع من ملف 🔄",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (exportedFilePath != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "مسار الملف المحفوظ: $exportedFilePath",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }

    // Export Confirmation & Details Dialog
    if (showExportDialog && exportedFilePath != null) {
        AlertDialog(
            onDismissRequest = onDismissExportDialog,
            icon = {
                Icon(
                    imageVector = Icons.Default.DriveFileMove,
                    contentDescription = null,
                    tint = Emerald700,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "تم تصدير الملاحظات بنجاح 📁",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800
                )
            },
            text = {
                Column {
                    Text(
                        text = "تم حفظ كافة ملاحظات الرقية وآثار الجلسات المسجلة في ملف JSON محلي في ذاكرة الهاتف:",
                        fontSize = 12.sp,
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = exportedFilePath,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Slate800
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "يمكنك نقل هذا الملف أو نسخ النص كنسخة احتياطية آمنة.",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        exportedFileContent?.let { content ->
                            clipboardManager.setText(AnnotatedString(content))
                            onShowToast("تم نسخ نص ملف النسخة الاحتياطية للجمال 📋")
                        }
                        onDismissExportDialog()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("نسخ النص للحافظة 📋")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissExportDialog) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Import Backup Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Emerald700,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "استرجاع سجل الملاحظات 🔄",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "ألصق نص ملف النسخة الاحتياطية (JSON) المصدّر سابقاً لاسترجاع الملاحظات إلى قاعدة البيانات:",
                        fontSize = 12.sp,
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    androidx.compose.material3.OutlinedTextField(
                        value = importInputText,
                        onValueChange = { importInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("import_json_input"),
                        placeholder = {
                            Text(
                                text = "ألصق نص JSON هنا...",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 6
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importInputText.isNotBlank()) {
                            onImportLogsFromJson(importInputText)
                            importInputText = ""
                            showImportDialog = false
                        } else {
                            onShowToast("الرجاء لصق نص JSON أولاً")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("استرجاع البيانات 📥")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
