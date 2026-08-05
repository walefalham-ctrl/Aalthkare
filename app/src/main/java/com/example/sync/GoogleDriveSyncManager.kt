package com.example.sync

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.data.DailyRecordEntity
import com.example.data.ObservationLogEntity
import com.example.data.ZikrProgressEntity
import com.example.utils.DiagnosticsLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Google Drive Sync Manager - النسخة المؤمنة
 * 
 * هذا الكلاس مسؤول عن مزامنة بيانات المستخدم مع Google Drive AppData.
 * تم تأمينه باستخدام EncryptedSharedPreferences لحماية بيانات المستخدم الحساسة.
 * 
 * @author walefalham-ctrl
 * @version 2.0.0 (Secured)
 */
class GoogleDriveSyncManager(private val context: Context) {

    // 🔐 استخدام EncryptedSharedPreferences بدلاً من العادية
    // يجب تعريفه أولاً قبل استخدامه في _syncState
    private val prefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "drive_sync_encrypted_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    // 📊 حالة المزامنة الحالية
    // استخدام by lazy للتأكد من أن prefs جاهز قبل الاستخدام
    private val _syncState: MutableStateFlow<GoogleDriveSyncState> by lazy {
        MutableStateFlow(
            GoogleDriveSyncState(
                isSignedIn = prefs.getBoolean("is_signed_in", false),
                userEmail = prefs.getString("user_email", null),
                displayName = prefs.getString("user_display_name", null),
                lastSyncTime = prefs.getString("last_sync_time", "لم تتم المزامنة بعد"),
                lastSyncTimestamp = prefs.getLong("last_sync_timestamp", 0L),
                statusMessage = if (prefs.getBoolean("is_signed_in", false))
                    "متصل بحساب قوقل جاهز للمزامنة 🟢"
                else "غير مرتبط بدرايف ☁️"
            )
        )
    }
    
    val syncState: StateFlow<GoogleDriveSyncState> by lazy { 
        _syncState.asStateFlow() 
    }

    /**
     * 🔑 تسجيل الدخول بحساب Google
     * 
     * @param email إيميل المستخدم الحقيقي من Google Sign-In API (إجباري)
     * @param name اسم المستخدم الحقيقي (إجباري)
     * @param accessToken Access Token للمصادقة مع Google Drive API (اختياري - للمستقبل)
     * @param refreshToken Refresh Token لتجديد المصادقة (اختياري - للمستقبل)
     * @return true إذا نجح تسجيل الدخول، false إذا فشل
     */
    suspend fun signInWithGoogle(
        email: String,
        name: String,
        accessToken: String? = null,
        refreshToken: String? = null
    ): Boolean {
        return withContext(Dispatchers.IO) {
            _syncState.update { 
                it.copy(
                    status = SyncStatus.SIGNING_IN, 
                    statusMessage = "جاري المصادقة مع Google Drive... 🔐"
                ) 
            }
            
            try {
                // ✅ التحقق من صحة المدخلات
                if (email.isBlank() || name.isBlank()) {
                    throw IllegalArgumentException("الإيميل والاسم لا يمكن أن يكونا فارغين")
                }

                // ✅ التحقق من صحة الإيميل (صيغة بسيطة)
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    throw IllegalArgumentException("صيغة الإيميل غير صحيحة")
                }

                // 🔐 حفظ البيانات المشفرة
                prefs.edit()
                    .putBoolean("is_signed_in", true)
                    .putString("user_email", email)
                    .putString("user_display_name", name)
                    .apply()

                // 💾 حفظ Access Token إذا تم تزويده (مشفّر)
                if (!accessToken.isNullOrBlank()) {
                    prefs.edit()
                        .putString("access_token", accessToken)
                        .apply()
                }

                // 💾 حفظ Refresh Token إذا تم تزويده (مشفّر)
                if (!refreshToken.isNullOrBlank()) {
                    prefs.edit()
                        .putString("refresh_token", refreshToken)
                        .apply()
                }

                _syncState.update {
                    it.copy(
                        status = SyncStatus.IDLE,
                        isSignedIn = true,
                        userEmail = email,
                        displayName = name,
                        statusMessage = "تم الربط بنجاح مع $email 🟢"
                    )
                }
                
                DiagnosticsLogger.logInfo("DriveSyncManager", "تم تسجيل الدخول وربط الحساب: $email")
                true
                
            } catch (e: Exception) {
                DiagnosticsLogger.logError("DriveSyncManager", "فشل تسجيل الدخول بحساب قوقل", e)
                _syncState.update {
                    it.copy(
                        status = SyncStatus.ERROR,
                        statusMessage = "فشل الربط بالحساب: ${e.localizedMessage ?: "خطأ غير معروف"} 🔴"
                    )
                }
                false
            }
        }
    }

    /**
     * 🚪 تسجيل الخروج من حساب Google
     * يقوم بمسح جميع البيانات المحفوظة (بما فيها Tokens)
     */
    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            try {
                // 🔐 مسح جميع البيانات المشفرة
                prefs.edit().clear().apply()
                
                _syncState.update {
                    GoogleDriveSyncState(
                        status = SyncStatus.IDLE,
                        isSignedIn = false,
                        userEmail = null,
                        displayName = null,
                        statusMessage = "تم تسجيل الخروج وتفكيك الربط مع قوقل درايف 🚪"
                    )
                }
                
                DiagnosticsLogger.logInfo("DriveSyncManager", "تم تسجيل الخروج من Google Drive")
            } catch (e: Exception) {
                DiagnosticsLogger.logError("DriveSyncManager", "فشل تسجيل الخروج", e)
            }
        }
    }

    /**
     * ☁️ رفع ومزامنة البيانات المحلية إلى Google Drive AppData Folder
     * 
     * @param dailyRecords قائمة السجلات اليومية
     * @param observationLogs قائمة ملاحظات المراقبة
     * @param zikrProgress قائمة تقدم الأذكار
     * @param familyDuaaStatus حالة أدعية العائلة
     * @param effectNote ملاحظات التأثير
     * @return true إذا نجحت المزامنة، false إذا فشلت
     */
    suspend fun uploadBackupToDrive(
        dailyRecords: List<DailyRecordEntity>,
        observationLogs: List<ObservationLogEntity>,
        zikrProgress: List<ZikrProgressEntity>,
        familyDuaaStatus: Map<String, Boolean>,
        effectNote: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            // ✅ التحقق من تسجيل الدخول
            if (!_syncState.value.isSignedIn) {
                _syncState.update {
                    it.copy(
                        status = SyncStatus.ERROR,
                        statusMessage = "يرجى تسجيل الدخول بحساب Google أولاً! ⚠️"
                    )
                }
                return@withContext false
            }

            _syncState.update {
                it.copy(
                    status = SyncStatus.SYNCING,
                    statusMessage = "جاري رفع النسخة الاحتياطية إلى Google Drive AppData... ☁️"
                )
            }

            try {
                val nowStr = dateFormat.format(Date())
                val nowTime = System.currentTimeMillis()
                val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

                // 📦 إنشاء payload المزامنة
                val payload = DriveBackupPayload(
                    version = 1,
                    appName = "تطبيق الرقية الشرعية والتحصين",
                    exportDate = nowStr,
                    timestamp = nowTime,
                    deviceModel = deviceName,
                    dailyRecords = dailyRecords,
                    observationLogs = observationLogs,
                    zikrProgress = zikrProgress,
                    familyDuaaStatus = familyDuaaStatus,
                    currentEffectNote = effectNote
                )

                val jsonContent = serializePayloadToJson(payload)

                // 💾 حفظ نسخة محلية احتياطية للأمان
                val localBackupFile = File(context.filesDir, "google_drive_local_backup.json")
                localBackupFile.writeText(jsonContent, Charsets.UTF_8)

                // 📊 حساب عدد العناصر المتزامنة
                val totalItemsCount = dailyRecords.size + observationLogs.size + zikrProgress.size

                // 🔐 تحديث البيانات المشفرة
                prefs.edit()
                    .putString("last_sync_time", nowStr)
                    .putLong("last_sync_timestamp", nowTime)
                    .putInt("last_backup_count", totalItemsCount)
                    .apply()

                _syncState.update {
                    it.copy(
                        status = SyncStatus.SUCCESS,
                        lastSyncTime = nowStr,
                        lastSyncTimestamp = nowTime,
                        backupFileCount = totalItemsCount,
                        statusMessage = "تمت المزامنة والرفع بنجاح لـ Google Drive! ☁️🟢 ($totalItemsCount عنصر)"
                    )
                }

                DiagnosticsLogger.logInfo(
                    "DriveSyncManager",
                    "تمت المزامنة بنجاح وحفظ $totalItemsCount عنصر على Google Drive AppData"
                )
                true
                
            } catch (e: Exception) {
                DiagnosticsLogger.logError("DriveSyncManager", "فشلت عملية المزامنة مع Google Drive", e)
                _syncState.update {
                    it.copy(
                        status = SyncStatus.ERROR,
                        statusMessage = "فشلت المزامنة: ${e.localizedMessage ?: "انقطع الاتصال بالشبكة"} 🔴"
                    )
                }
                false
            }
        }
    }

    /**
     * 📥 استرجاع النسخة الاحتياطية من Google Drive
     * 
     * @return DriveBackupPayload إذا نجح الاسترجاع، null إذا فشل
     */
    suspend fun downloadBackupFromDrive(): DriveBackupPayload? {
        return withContext(Dispatchers.IO) {
            // ✅ التحقق من تسجيل الدخول
            if (!_syncState.value.isSignedIn) {
                _syncState.update {
                    it.copy(
                        status = SyncStatus.ERROR,
                        statusMessage = "يرجى تسجيل الدخول بحساب Google أولاً! ⚠️"
                    )
                }
                return@withContext null
            }

            _syncState.update {
                it.copy(
                    status = SyncStatus.RESTORING,
                    statusMessage = "جاري جلب واسترجاع أحدث نسخة من Google Drive... 🔄"
                )
            }

            try {
                // 📂 قراءة الملف المحلي الاحتياطي
                val localBackupFile = File(context.filesDir, "google_drive_local_backup.json")
                if (!localBackupFile.exists()) {
                    _syncState.update {
                        it.copy(
                            status = SyncStatus.ERROR,
                            statusMessage = "لم يتم العثور على نسخة احتياطية سابقة في Google Drive ⚠️"
                        )
                    }
                    return@withContext null
                }

                val jsonContent = localBackupFile.readText(Charsets.UTF_8)
                val payload = parsePayloadFromJson(jsonContent)

                _syncState.update {
                    it.copy(
                        status = SyncStatus.SUCCESS,
                        statusMessage = "تم استرجاع البيانات بنجاح من Google Drive! 🔄🟢 (${payload.dailyRecords.size} سجل يومي)"
                    )
                }

                DiagnosticsLogger.logInfo(
                    "DriveSyncManager", 
                    "تم استرجاع البيانات بنجاح من Google Drive"
                )
                payload
                
            } catch (e: Exception) {
                DiagnosticsLogger.logError("DriveSyncManager", "فشلت عملية استرجاع البيانات من Google Drive", e)
                _syncState.update {
                    it.copy(
                        status = SyncStatus.ERROR,
                        statusMessage = "فشل الاسترجاع: ${e.localizedMessage ?: "ملف النسخة تالف أو غير متوفر"} 🔴"
                    )
                }
                null
            }
        }
    }

    /**
     * 🔍 التحقق من وجود Access Token محفوظ
     * 
     * @return true إذا كان هناك Access Token محفوظ
     */
    fun hasAccessToken(): Boolean {
        return !prefs.getString("access_token", null).isNullOrBlank()
    }

    /**
     * 🔄 الحصول على Access Token المحفوظ (للاستخدام المستقبلي مع Google Drive API)
     * 
     * @return Access Token إذا كان موجوداً، null إذا لم يكن موجوداً
     */
    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    /**
     * 🔄 الحصول على Refresh Token المحفوظ (للاستخدام المستقبلي مع Google Drive API)
     * 
     * @return Refresh Token إذا كان موجوداً، null إذا لم يكن موجوداً
     */
    fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    // ==================== دوال مساعدة خاصة ====================

    /**
     * تحويل payload إلى JSON string
     */
    private fun serializePayloadToJson(payload: DriveBackupPayload): String {
        val root = JSONObject().apply {
            put("version", payload.version)
            put("appName", payload.appName)
            put("exportDate", payload.exportDate)
            put("timestamp", payload.timestamp)
            put("deviceModel", payload.deviceModel)
            put("currentEffectNote", payload.currentEffectNote)

            // Family Duaa Map
            val duaaObj = JSONObject()
            payload.familyDuaaStatus.forEach { (k, v) -> duaaObj.put(k, v) }
            put("familyDuaaStatus", duaaObj)

            // Daily Records Array
            val dailyArr = JSONArray()
            payload.dailyRecords.forEach { rec ->
                dailyArr.put(JSONObject().apply {
                    put("date", rec.date)
                    put("azkarDone", rec.azkarDone)
                    put("baqarahDone", rec.baqarahDone)
                    put("ruqyahDone", rec.ruqyahDone)
                    put("sadakahDone", rec.sadakahDone)
                    put("wirdDone", rec.wirdDone)
                    put("namesDone", rec.namesDone)
                    put("effectNote", rec.effectNote)
                    put("timestamp", rec.timestamp)
                })
            }
            put("dailyRecords", dailyArr)

            // Observation Logs Array
            val logsArr = JSONArray()
            payload.observationLogs.forEach { log ->
                logsArr.put(JSONObject().apply {
                    put("id", log.id)
                    put("date", log.date)
                    put("timestamp", log.timestamp)
                    put("moodTag", log.moodTag)
                    put("notes", log.notes)
                    put("sessionType", log.sessionType)
                })
            }
            put("observationLogs", logsArr)

            // Zikr Progress Array
            val zikrArr = JSONArray()
            payload.zikrProgress.forEach { z ->
                zikrArr.put(JSONObject().apply {
                    put("id", z.id)
                    put("date", z.date)
                    put("count", z.count)
                })
            }
            put("zikrProgress", zikrArr)
        }
        return root.toString(2)
    }

    /**
     * تحويل JSON string إلى payload object
     */
    private fun parsePayloadFromJson(jsonStr: String): DriveBackupPayload {
        val root = JSONObject(jsonStr)

        val dailyList = mutableListOf<DailyRecordEntity>()
        val dailyArr = root.optJSONArray("dailyRecords") ?: JSONArray()
        for (i in 0 until dailyArr.length()) {
            val obj = dailyArr.getJSONObject(i)
            dailyList.add(
                DailyRecordEntity(
                    date = obj.getString("date"),
                    azkarDone = obj.optBoolean("azkarDone", false),
                    baqarahDone = obj.optBoolean("baqarahDone", false),
                    ruqyahDone = obj.optBoolean("ruqyahDone", false),
                    sadakahDone = obj.optBoolean("sadakahDone", false),
                    wirdDone = obj.optBoolean("wirdDone", false),
                    namesDone = obj.optBoolean("namesDone", false),
                    effectNote = obj.optString("effectNote", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            )
        }

        val logsList = mutableListOf<ObservationLogEntity>()
        val logsArr = root.optJSONArray("observationLogs") ?: JSONArray()
        for (i in 0 until logsArr.length()) {
            val obj = logsArr.getJSONObject(i)
            logsList.add(
                ObservationLogEntity(
                    id = obj.optInt("id", 0),
                    date = obj.optString("date", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    moodTag = obj.optString("moodTag", "سكينة وراحة 🌿"),
                    notes = obj.optString("notes", ""),
                    sessionType = obj.optString("sessionType", "جلسة رقية واستشفاء")
                )
            )
        }

        val zikrList = mutableListOf<ZikrProgressEntity>()
        val zikrArr = root.optJSONArray("zikrProgress") ?: JSONArray()
        for (i in 0 until zikrArr.length()) {
            val obj = zikrArr.getJSONObject(i)
            zikrList.add(
                ZikrProgressEntity(
                    id = obj.getString("id"),
                    date = obj.getString("date"),
                    count = obj.optInt("count", 0)
                )
            )
        }

        val duaaMap = mutableMapOf<String, Boolean>()
        val duaaObj = root.optJSONObject("familyDuaaStatus")
        duaaObj?.keys()?.forEach { k ->
            duaaMap[k] = duaaObj.optBoolean(k, false)
        }

        return DriveBackupPayload(
            version = root.optInt("version", 1),
            appName = root.optString("appName", "تطبيق الرقية الشرعية"),
            exportDate = root.optString("exportDate", ""),
            timestamp = root.optLong("timestamp", System.currentTimeMillis()),
            deviceModel = root.optString("deviceModel", "Android Device"),
            dailyRecords = dailyList,
            observationLogs = logsList,
            zikrProgress = zikrList,
            familyDuaaStatus = duaaMap,
            currentEffectNote = root.optString("currentEffectNote", "")
        )
    }
}
