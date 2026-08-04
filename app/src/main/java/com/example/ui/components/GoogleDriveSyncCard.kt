package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sync.GoogleDriveSyncState
import com.example.sync.SyncStatus
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800

@Composable
fun GoogleDriveSyncCard(
    syncState: GoogleDriveSyncState,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onUploadBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }

    val statusBgColor = when (syncState.status) {
        SyncStatus.SUCCESS -> Emerald100
        SyncStatus.SYNCING, SyncStatus.RESTORING, SyncStatus.SIGNING_IN -> Color(0xFFE0F2FE)
        SyncStatus.ERROR -> Color(0xFFFEE2E2)
        SyncStatus.IDLE -> if (syncState.isSignedIn) Emerald100 else Color(0xFFF1F5F9)
    }

    val statusTextColor = when (syncState.status) {
        SyncStatus.SUCCESS -> Emerald800
        SyncStatus.SYNCING, SyncStatus.RESTORING, SyncStatus.SIGNING_IN -> Color(0xFF0369A1)
        SyncStatus.ERROR -> Color(0xFF991B1B)
        SyncStatus.IDLE -> if (syncState.isSignedIn) Emerald800 else Slate500
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "المزامنة السحابية (Google Drive Sync)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "حفظ واسترجاع السجلات في Google AppData ☁️",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (syncState.status == SyncStatus.SYNCING || syncState.status == SyncStatus.RESTORING || syncState.status == SyncStatus.SIGNING_IN) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(10.dp),
                                strokeWidth = 1.5.dp,
                                color = statusTextColor
                            )
                        } else {
                            Icon(
                                imageVector = if (syncState.isSignedIn) Icons.Default.CheckCircle else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = statusTextColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (syncState.status) {
                                SyncStatus.SIGNING_IN -> "جاري الربط..."
                                SyncStatus.SYNCING -> "جاري الرفع..."
                                SyncStatus.RESTORING -> "جاري الاسترجاع..."
                                SyncStatus.SUCCESS -> "مكتملة 🟢"
                                SyncStatus.ERROR -> "فشلت 🔴"
                                SyncStatus.IDLE -> if (syncState.isSignedIn) "متصل 🟢" else "غير مرتبط ☁️"
                            },
                            fontSize = 10.sp,
                            color = statusTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Account Info Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (syncState.isSignedIn) Emerald100 else Slate200),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = if (syncState.isSignedIn) Emerald700 else Slate500,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (syncState.isSignedIn) (syncState.displayName ?: "المستخدم المحصّن") else "غير مرتبط بحساب Google",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (syncState.isSignedIn) (syncState.userEmail ?: "naharnoonmeem@gmail.com") else "انقر أدناه للربط والمزامنة تلقائياً",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (syncState.isSignedIn) {
                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "فصل",
                                fontSize = 10.sp,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status Message Bar
            Text(
                text = syncState.statusMessage,
                fontSize = 11.sp,
                color = statusTextColor,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp
            )

            if (syncState.lastSyncTime != null && syncState.lastSyncTime != "لم تتم المزامنة بعد") {
                Text(
                    text = "آخر مزامنة ناجحة: ${syncState.lastSyncTime}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            if (!syncState.isSignedIn) {
                Button(
                    onClick = onSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("google_drive_signin_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تسجيل الدخول والربط بحساب Google Drive 🔐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onUploadBackup,
                        enabled = syncState.status != SyncStatus.SYNCING && syncState.status != SyncStatus.RESTORING,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("drive_upload_backup_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "رفع النسخة (Backup) ☁️",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { showRestoreConfirmDialog = true },
                        enabled = syncState.status != SyncStatus.SYNCING && syncState.status != SyncStatus.RESTORING,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("drive_restore_backup_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Emerald700)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "استرجاع النسخة 🔄",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Privacy Security Note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Emerald700,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "نطاق المزامنة (AppData Folder Scope): تحفظ نسخة التطبيق مشفرة وخاصة في مجلد التطبيق المعزول بدرايف.",
                    fontSize = 9.sp,
                    color = Slate500,
                    lineHeight = 13.sp
                )
            }
        }
    }

    // Restore Backup Confirmation Dialog
    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Emerald700,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "استرجاع النسخة السحابية من Google Drive 🔄",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "هل ترغب باسترجاع وتحديث بيانات التطبيق (سجلات التحصين اليومية، الملاحظات، والأذكار) بأحدث نسخة محفوظة على Google Drive؟",
                    fontSize = 12.sp,
                    color = Slate800,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreConfirmDialog = false
                        onRestoreBackup()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("تأكيد الاسترجاع 🔄")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
