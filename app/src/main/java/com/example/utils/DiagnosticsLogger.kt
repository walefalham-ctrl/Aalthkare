package com.example.utils

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DiagnosticEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val stackTrace: String? = null
)

enum class LogLevel {
    INFO, WARNING, ERROR, CRASH
}

data class AppHealthSummary(
    val status: HealthStatus,
    val message: String,
    val usedMemoryMb: Long,
    val maxMemoryMb: Long,
    val totalLogsCount: Int,
    val crashCount: Int,
    val lastCrashTime: String? = null
)

enum class HealthStatus {
    EXCELLENT, ATTENTION, CRITICAL
}

object DiagnosticsLogger {

    private const val TAG = "DiagnosticsLogger"
    private const val LOGS_FILE_NAME = "app_diagnostics_logs.json"
    private const val CRASH_FILE_NAME = "last_uncaught_crash.json"
    private const val MAX_LOG_ENTRIES = 100

    private var appContext: Context? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    fun init(context: Context) {
        appContext = context.applicationContext
        logInfo("System", "تم تهيئة نظام فحص حالة التطبيق وتسجيل التشخيصات بنجاح")
    }

    fun logInfo(tag: String, message: String) {
        addLog(LogLevel.INFO, tag, message, null)
    }

    fun logWarning(tag: String, message: String) {
        addLog(LogLevel.WARNING, tag, message, null)
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        addLog(LogLevel.ERROR, tag, message, throwable?.stackTraceToString())
    }

    fun logCrash(throwable: Throwable) {
        val stackTrace = throwable.stackTraceToString()
        val entry = DiagnosticEntry(
            timestamp = dateFormat.format(Date()),
            level = LogLevel.CRASH,
            tag = "CRASH_HANDLER",
            message = throwable.localizedMessage ?: throwable.message ?: "Uncaught Exception",
            stackTrace = stackTrace
        )
        Log.e(TAG, "Uncaught Crash Captured: ${entry.message}", throwable)
        
        // Save to immediate crash file synchronously for reliability
        appContext?.let { ctx ->
            try {
                val file = File(ctx.filesDir, CRASH_FILE_NAME)
                val json = JSONObject().apply {
                    put("timestamp", entry.timestamp)
                    put("message", entry.message)
                    put("stackTrace", stackTrace)
                    put("device", "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})")
                }
                file.writeText(json.toString(2), Charsets.UTF_8)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        addLog(LogLevel.CRASH, "CRASH_HANDLER", entry.message, stackTrace)
    }

    @Synchronized
    private fun addLog(level: LogLevel, tag: String, message: String, stackTrace: String?) {
        val entry = DiagnosticEntry(
            timestamp = dateFormat.format(Date()),
            level = level,
            tag = tag,
            message = message,
            stackTrace = stackTrace
        )

        when (level) {
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARNING -> Log.w(tag, message)
            LogLevel.ERROR, LogLevel.CRASH -> Log.e(tag, "$message\n$stackTrace")
        }

        appContext?.let { ctx ->
            try {
                val file = File(ctx.filesDir, LOGS_FILE_NAME)
                val currentLogs = loadLogsFromFile(file).toMutableList()
                currentLogs.add(0, entry) // Newest first

                // Trim excess logs
                val trimmed = if (currentLogs.size > MAX_LOG_ENTRIES) {
                    currentLogs.take(MAX_LOG_ENTRIES)
                } else currentLogs

                saveLogsToFile(file, trimmed)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getLogs(): List<DiagnosticEntry> {
        val ctx = appContext ?: return emptyList()
        val file = File(ctx.filesDir, LOGS_FILE_NAME)
        return loadLogsFromFile(file)
    }

    fun getLastCrashReport(): String? {
        val ctx = appContext ?: return null
        val file = File(ctx.filesDir, CRASH_FILE_NAME)
        return if (file.exists()) {
            try {
                file.readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun clearLogs() {
        val ctx = appContext ?: return
        try {
            val file = File(ctx.filesDir, LOGS_FILE_NAME)
            if (file.exists()) file.delete()
            logInfo("System", "تم مسح سجلات الأخطاء والتشخيصات")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearLastCrash() {
        val ctx = appContext ?: return
        try {
            val file = File(ctx.filesDir, CRASH_FILE_NAME)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAppHealthSummary(): AppHealthSummary {
        val runtime = Runtime.getRuntime()
        val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMem = runtime.maxMemory() / (1024 * 1024)

        val logs = getLogs()
        val crashCount = logs.count { it.level == LogLevel.CRASH }
        val errorCount = logs.count { it.level == LogLevel.ERROR }

        val lastCrash = getLastCrashReport()

        val status = when {
            crashCount > 0 || lastCrash != null -> HealthStatus.CRITICAL
            errorCount > 3 -> HealthStatus.ATTENTION
            else -> HealthStatus.EXCELLENT
        }

        val msg = when (status) {
            HealthStatus.EXCELLENT -> "التطبيق يعمل بسلامة استثنائية واستجابة سريعة بدون أخطاء 🟢"
            HealthStatus.ATTENTION -> "تنبيه: تم رصد بعض الأخطاء الخفيفة، التطبيق مستقر 🟡"
            HealthStatus.CRITICAL -> "تحذير: تم رصد خطأ سابق، تم معالجته واحتوائه بنجاح 🔴"
        }

        return AppHealthSummary(
            status = status,
            message = msg,
            usedMemoryMb = usedMem,
            maxMemoryMb = maxMem,
            totalLogsCount = logs.size,
            crashCount = crashCount,
            lastCrashTime = if (lastCrash != null) "مسجل سابقاً" else null
        )
    }

    private fun loadLogsFromFile(file: File): List<DiagnosticEntry> {
        if (!file.exists()) return emptyList()
        return try {
            val jsonStr = file.readText(Charsets.UTF_8)
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<DiagnosticEntry>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    DiagnosticEntry(
                        id = obj.optLong("id", System.currentTimeMillis()),
                        timestamp = obj.optString("timestamp", ""),
                        level = LogLevel.valueOf(obj.optString("level", "INFO")),
                        tag = obj.optString("tag", ""),
                        message = obj.optString("message", ""),
                        stackTrace = obj.optString("stackTrace", null)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveLogsToFile(file: File, logs: List<DiagnosticEntry>) {
        try {
            val jsonArray = JSONArray()
            logs.forEach { entry ->
                val obj = JSONObject().apply {
                    put("id", entry.id)
                    put("timestamp", entry.timestamp)
                    put("level", entry.level.name)
                    put("tag", entry.tag)
                    put("message", entry.message)
                    put("stackTrace", entry.stackTrace)
                }
                jsonArray.put(obj)
            }
            file.writeText(jsonArray.toString(2), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
