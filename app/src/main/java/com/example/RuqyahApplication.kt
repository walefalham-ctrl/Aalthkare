package com.example

import android.app.Application
import com.example.utils.DiagnosticsLogger

class RuqyahApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Local Diagnostics & Health System
        DiagnosticsLogger.init(this)

        // 2. Global Uncaught Exception Handler (Crash Handling)
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                // Log and save crash details locally
                DiagnosticsLogger.logCrash(throwable)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Delegate to original handler or exit safely
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }

        DiagnosticsLogger.logInfo("RuqyahApplication", "تم تفعيل حماية التطبيق من الخروج المفاجئ (Global Crash Safeguard)")
    }
}
