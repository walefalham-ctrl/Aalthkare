package com.example.sync

import com.example.data.DailyRecordEntity
import com.example.data.ObservationLogEntity
import com.example.data.ZikrProgressEntity

/**
 * Sync status enum for UI representation
 */
enum class SyncStatus {
    IDLE,
    SIGNING_IN,
    SYNCING,
    RESTORING,
    SUCCESS,
    ERROR
}

/**
 * State representing Google Drive Cloud Sync
 */
data class GoogleDriveSyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val isSignedIn: Boolean = false,
    val userEmail: String? = null,
    val displayName: String? = null,
    val lastSyncTime: String? = null,
    val lastSyncTimestamp: Long = 0L,
    val statusMessage: String = "غير مرتبط بدرايف ☁️",
    val backupFileCount: Int = 0,
    val isAutoSyncEnabled: Boolean = true
)

/**
 * Complete Payload structure exported to / restored from Google Drive JSON
 */
data class DriveBackupPayload(
    val version: Int = 1,
    val appName: String = "تطبيق الرقية الشرعية والتحصين",
    val exportDate: String,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceModel: String,
    val dailyRecords: List<DailyRecordEntity> = emptyList(),
    val observationLogs: List<ObservationLogEntity> = emptyList(),
    val zikrProgress: List<ZikrProgressEntity> = emptyList(),
    val familyDuaaStatus: Map<String, Boolean> = emptyMap(),
    val currentEffectNote: String = ""
)
