package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DailyRecordEntity
import com.example.data.ObservationLogEntity
import com.example.data.RuqyahDatabase
import com.example.data.RuqyahRepository
import com.example.data.ZikrProgressEntity
import com.example.model.AzkarData
import com.example.model.GodName
import com.example.model.GodNamesData
import com.example.model.HealingVersesData
import com.example.model.TaskType
import com.example.model.ZikrCategory
import com.example.model.ZikrItem
import com.example.utils.DiagnosticsLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppTab(val route: String, val title: String) {
    HOME("home", "الرئيسية"),
    AZKAR("azkar", "الأذكار"),
    QURAN("quran", "البقرة والرقية"),
    NAMES("names", "أسماء الله"),
    HISTORY("history", "السجل")
}

data class RuqyahUiState(
    val activeTab: AppTab = AppTab.HOME,
    val todayDate: String = "",
    val formattedDateArabic: String = "",
    val baqarahPageRange: String = "",
    val dailyRecord: DailyRecordEntity = DailyRecordEntity(date = ""),
    val historyList: List<DailyRecordEntity> = emptyList(),
    val observationLogs: List<ObservationLogEntity> = emptyList(),
    val currentObservationNote: String = "",
    val selectedObservationMood: String = "سكينة وراحة 🌿",
    val selectedCategory: ZikrCategory = ZikrCategory.SABAH,
    val currentZikrIndex: Int = 0,
    val zikrProgressMap: Map<String, Int> = emptyMap(),
    val dailyGodNames: Pair<GodName, GodName> = Pair(GodNamesData.list[0], GodNamesData.list[1]),
    val selectedGodName: GodName = GodNamesData.list.first(),
    val currentRuqyahVerseIndex: Int = 0,
    val completedVerseIds: Set<Int> = emptySet(),
    val isFamilyDuaaDone: Boolean = false,
    val isDarkMode: Boolean = false,
    val exportedBackupPath: String? = null,
    val exportedBackupContent: String? = null,
    val showExportDialog: Boolean = false,
    val toastMessage: String? = null
)

class RuqyahViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RuqyahRepository
    private val _uiState = MutableStateFlow(RuqyahUiState())
    val uiState: StateFlow<RuqyahUiState> = _uiState.asStateFlow()

    init {
        val dao = RuqyahDatabase.getInstance(application).ruqyahDao()
        repository = RuqyahRepository(dao)

        val prefs = application.getSharedPreferences("ruqyah_app_prefs", android.content.Context.MODE_PRIVATE)
        val savedDarkMode = prefs.getBoolean("is_dark_mode", false)

        _uiState.update { it.copy(isDarkMode = savedDarkMode) }

        setupDatesAndBaqarah()
        observeData()
    }

    private fun setupDatesAndBaqarah() {
        val calendar = Calendar.getInstance()
        val dateFormatKey = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = dateFormatKey.format(calendar.time)

        // Arabic Date string
        val arabicFormat = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
        val formattedDate = arabicFormat.format(calendar.time)

        // Calculate Surah Baqarah Pages assignment
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val totalBaqarahPages = 48
        val pagesPerDay = 7
        val startPage = ((dayOfYear - 1) * pagesPerDay) % totalBaqarahPages + 2
        val endPage = minOf(startPage + pagesPerDay - 1, 49)
        val baqarahStr = "من صفحة $startPage إلى صفحة $endPage"

        // Calculate 2 daily God Names from Mukhtasar Fiqh Al-Asma Al-Husna database
        val totalNames = GodNamesData.list.size
        val firstIndex = ((dayOfYear - 1) * 2) % totalNames
        val secondIndex = (firstIndex + 1) % totalNames
        val name1 = GodNamesData.list[firstIndex]
        val name2 = GodNamesData.list[secondIndex]

        _uiState.update {
            it.copy(
                todayDate = todayStr,
                formattedDateArabic = formattedDate,
                baqarahPageRange = baqarahStr,
                dailyRecord = DailyRecordEntity(date = todayStr),
                dailyGodNames = Pair(name1, name2),
                selectedGodName = name1
            )
        }
    }

    private fun observeData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val todayDate = _uiState.value.todayDate
                repository.getDailyRecord(todayDate).collectLatest { record ->
                    val current = record ?: DailyRecordEntity(date = todayDate)
                    _uiState.update { it.copy(dailyRecord = current) }
                }
            } catch (e: Exception) {
                DiagnosticsLogger.logError("RuqyahViewModel", "خطأ أثناء مراقبة السجل اليومي", e)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllHistoryRecords().collectLatest { history ->
                    _uiState.update { it.copy(historyList = history) }
                }
            } catch (e: Exception) {
                DiagnosticsLogger.logError("RuqyahViewModel", "خطأ أثناء مراقبة تاريخ السجلات", e)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val todayDate = _uiState.value.todayDate
                repository.getZikrProgressForDate(todayDate).collectLatest { progressList ->
                    val map = progressList.associate { it.id to it.count }
                    _uiState.update { it.copy(zikrProgressMap = map) }
                }
            } catch (e: Exception) {
                DiagnosticsLogger.logError("RuqyahViewModel", "خطأ أثناء مراقبة تقدم الأذكار", e)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllObservationLogs().collectLatest { logs ->
                    _uiState.update { it.copy(observationLogs = logs) }
                }
            } catch (e: Exception) {
                DiagnosticsLogger.logError("RuqyahViewModel", "خطأ أثناء مراقبة ملاحظات الاستشفاء", e)
            }
        }
    }

    fun selectTab(tab: AppTab) {
        if (_uiState.value.activeTab == tab) return // Prevent redundant tab recomposition
        _uiState.update { it.copy(activeTab = tab) }
        DiagnosticsLogger.logInfo("RuqyahViewModel", "التنقل السلس إلى تبويب: ${tab.title}")
    }

    fun toggleTask(taskType: TaskType) {
        val currentRecord = _uiState.value.dailyRecord
        val updatedRecord = when (taskType) {
            TaskType.AZKAR -> currentRecord.copy(azkarDone = !currentRecord.azkarDone)
            TaskType.BAQARAH -> currentRecord.copy(baqarahDone = !currentRecord.baqarahDone)
            TaskType.RUQYAH -> currentRecord.copy(ruqyahDone = !currentRecord.ruqyahDone)
            TaskType.SADAKAH -> currentRecord.copy(sadakahDone = !currentRecord.sadakahDone)
            TaskType.WIRD -> currentRecord.copy(wirdDone = !currentRecord.wirdDone)
            TaskType.NAMES -> currentRecord.copy(namesDone = !currentRecord.namesDone)
        }

        viewModelScope.launch {
            repository.saveDailyRecord(updatedRecord)
        }
    }

    fun updateEffectNote(note: String) {
        val currentRecord = _uiState.value.dailyRecord.copy(effectNote = note)
        _uiState.update { it.copy(dailyRecord = currentRecord) }
    }

    fun updateObservationNoteInput(note: String) {
        _uiState.update { it.copy(currentObservationNote = note) }
    }

    fun selectObservationMood(moodTag: String) {
        _uiState.update { it.copy(selectedObservationMood = moodTag) }
    }

    fun addObservationLog(
        noteText: String = _uiState.value.currentObservationNote,
        moodTagText: String = _uiState.value.selectedObservationMood
    ) {
        if (noteText.isBlank()) {
            showToast("الرجاء كتابة ملاحظتك أو أثر الجلسة أولاً")
            return
        }

        viewModelScope.launch {
            val todayDate = _uiState.value.todayDate
            val newLog = ObservationLogEntity(
                date = todayDate,
                moodTag = moodTagText,
                notes = noteText.trim()
            )
            repository.insertObservationLog(newLog)

            // Update daily record effect note as well
            val updatedRecord = _uiState.value.dailyRecord.copy(effectNote = noteText.trim())
            repository.saveDailyRecord(updatedRecord)

            _uiState.update { 
                it.copy(
                    currentObservationNote = "",
                    dailyRecord = updatedRecord
                ) 
            }
            showToast("تم حفظ ملاحظة الرقية اليومية في قاعدة البيانات بنجاح 🌿")
        }
    }

    fun deleteObservationLog(id: Int) {
        viewModelScope.launch {
            repository.deleteObservationLogById(id)
            showToast("تم حذف الملاحظة من السجل 🗑️")
        }
    }

    fun clearAllObservationLogs() {
        viewModelScope.launch {
            repository.clearAllObservationLogs()
            showToast("تم مسح كافة ملاحظات الرقية المسجلة")
        }
    }

    fun exportObservationLogsToFile() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            val logs = _uiState.value.observationLogs

            if (logs.isEmpty()) {
                showToast("لا توجد ملاحظات مسجلة لتصديرها")
                return@launch
            }

            try {
                val jsonArray = org.json.JSONArray()
                logs.forEach { log ->
                    val obj = org.json.JSONObject().apply {
                        put("id", log.id)
                        put("date", log.date)
                        put("timestamp", log.timestamp)
                        put("moodTag", log.moodTag)
                        put("notes", log.notes)
                        put("sessionType", log.sessionType)
                    }
                    jsonArray.put(obj)
                }

                val jsonContent = jsonArray.toString(2)
                val dir = app.getExternalFilesDir(null) ?: app.filesDir
                val backupFile = java.io.File(dir, "ruqyah_observation_logs_backup.json")
                backupFile.writeText(jsonContent, Charsets.UTF_8)

                _uiState.update {
                    it.copy(
                        exportedBackupPath = backupFile.absolutePath,
                        exportedBackupContent = jsonContent,
                        showExportDialog = true
                    )
                }
                showToast("تم تصدير ${logs.size} ملاحظة إلى الملف المحلي بنجاح 📁")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("حدث خطأ أثناء تصدير البيانات: ${e.localizedMessage}")
            }
        }
    }

    fun dismissExportDialog() {
        _uiState.update { it.copy(showExportDialog = false) }
    }

    fun importObservationLogsFromJson(jsonContent: String) {
        viewModelScope.launch {
            try {
                val jsonArray = org.json.JSONArray(jsonContent)
                var count = 0
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val log = ObservationLogEntity(
                        date = obj.optString("date", _uiState.value.todayDate),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        moodTag = obj.optString("moodTag", "سكينة وراحة 🌿"),
                        notes = obj.optString("notes", ""),
                        sessionType = obj.optString("sessionType", "جلسة رقية واستشفاء")
                    )
                    if (log.notes.isNotBlank()) {
                        repository.insertObservationLog(log)
                        count++
                    }
                }
                showToast("تم استرجاع $count ملاحظة بنجاح إلى قاعدة البيانات 🔄")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("تعذر استرجاع السجل، الرجاء التأكد من صحة نص الملف")
            }
        }
    }

    fun toggleFamilyDuaa(completed: Boolean) {
        _uiState.update { it.copy(isFamilyDuaaDone = completed) }
        if (completed) {
            showToast("تقبل الله دعاءك لحفظ الزوجة نوره والابن نهار 🤲💚")
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        val app = getApplication<Application>()
        val prefs = app.getSharedPreferences("ruqyah_app_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()

        _uiState.update { it.copy(isDarkMode = enabled) }
        showToast(if (enabled) "تم تفعيل الوضع الليلي لحماية العين 🌙" else "تم تفعيل الوضع النهاري ☀️")
    }

    fun saveTodayRecordAndNote() {
        viewModelScope.launch {
            val currentRecord = _uiState.value.dailyRecord
            repository.saveDailyRecord(currentRecord)
            showToast("تم حفظ الإنجاز اليومي وملاحظة الأثر بنجاح 🌿")
        }
    }

    fun selectZikrCategory(category: ZikrCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                currentZikrIndex = 0
            )
        }
    }

    fun getCurrentCategoryItems(): List<ZikrItem> {
        return AzkarData.allAzkar.filter { it.category == _uiState.value.selectedCategory }
    }

    fun getCurrentZikrItem(): ZikrItem? {
        val items = getCurrentCategoryItems()
        val index = _uiState.value.currentZikrIndex
        return items.getOrNull(index)
    }

    fun nextZikr() {
        val items = getCurrentCategoryItems()
        val currentIndex = _uiState.value.currentZikrIndex
        if (currentIndex < items.size - 1) {
            _uiState.update { it.copy(currentZikrIndex = currentIndex + 1) }
        } else {
            showToast("وصلت إلى آخر ذكر في هذه القائمة")
        }
    }

    fun prevZikr() {
        val currentIndex = _uiState.value.currentZikrIndex
        if (currentIndex > 0) {
            _uiState.update { it.copy(currentZikrIndex = currentIndex - 1) }
        }
    }

    fun incrementCurrentZikr() {
        val item = getCurrentZikrItem() ?: return
        val date = _uiState.value.todayDate
        val currentCount = _uiState.value.zikrProgressMap[item.id] ?: 0

        if (currentCount < item.targetCount) {
            val newCount = currentCount + 1
            viewModelScope.launch {
                repository.saveZikrProgress(ZikrProgressEntity(id = item.id, date = date, count = newCount))

                if (newCount >= item.targetCount) {
                    showToast("اكتمل الذكر (${item.title}) ✅")

                    // Check if all items in category are completed
                    val items = getCurrentCategoryItems()
                    val allCompleted = items.all {
                        val cnt = if (it.id == item.id) newCount else (_uiState.value.zikrProgressMap[it.id] ?: 0)
                        cnt >= it.targetCount
                    }

                    if (allCompleted) {
                        // Automatically mark Azkar task as done in daily record!
                        val updatedRecord = _uiState.value.dailyRecord.copy(azkarDone = true)
                        repository.saveDailyRecord(updatedRecord)
                        showToast("أبشر! أتممت هذه المجموعة بالأذكار بالكامل 🌿")
                    } else if (_uiState.value.currentZikrIndex < items.size - 1) {
                        // Auto advance to next item
                        nextZikr()
                    }
                }
            }
        } else {
            // Already completed, move to next
            nextZikr()
        }
    }

    fun incrementZikrItem(item: ZikrItem) {
        val date = _uiState.value.todayDate
        val currentCount = _uiState.value.zikrProgressMap[item.id] ?: 0

        if (currentCount < item.targetCount) {
            val newCount = currentCount + 1
            viewModelScope.launch {
                repository.saveZikrProgress(ZikrProgressEntity(id = item.id, date = date, count = newCount))

                if (newCount >= item.targetCount) {
                    showToast("اكتمل الذكر (${item.title}) ✅")

                    val items = AzkarData.allAzkar.filter { it.category == item.category }
                    val allCompleted = items.all {
                        val cnt = if (it.id == item.id) newCount else (_uiState.value.zikrProgressMap[it.id] ?: 0)
                        cnt >= it.targetCount
                    }

                    if (allCompleted) {
                        val updatedRecord = _uiState.value.dailyRecord.copy(azkarDone = true)
                        repository.saveDailyRecord(updatedRecord)
                        showToast("أبشر! أتممت هذه المجموعة بالأذكار بالكامل 🌿")
                    }
                }
            }
        }
    }

    fun resetZikrItem(item: ZikrItem) {
        val date = _uiState.value.todayDate
        viewModelScope.launch {
            repository.resetZikrProgress(date, item.id)
            showToast("تم إعادة عداد (${item.title})")
        }
    }

    fun resetCurrentZikr() {
        val item = getCurrentZikrItem() ?: return
        val date = _uiState.value.todayDate
        viewModelScope.launch {
            repository.resetZikrProgress(date, item.id)
            showToast("تم إعادة عداد هذا الذكر")
        }
    }

    fun startNewDayConfirm() {
        viewModelScope.launch {
            val date = _uiState.value.todayDate
            val resetRecord = DailyRecordEntity(date = date)
            repository.saveDailyRecord(resetRecord)
            repository.clearZikrProgressForDate(date)
            showToast("تم البدء بيوم جديد وتصفير المهام")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            showToast("تم مسح السجل بنجاح")
        }
    }

    fun selectGodName(godName: GodName) {
        _uiState.update { it.copy(selectedGodName = godName) }
    }

    fun selectRuqyahVerseIndex(index: Int) {
        if (index in HealingVersesData.list.indices) {
            _uiState.update { it.copy(currentRuqyahVerseIndex = index) }
        }
    }

    fun nextRuqyahVerse() {
        val currentIndex = _uiState.value.currentRuqyahVerseIndex
        if (currentIndex < HealingVersesData.list.size - 1) {
            _uiState.update { it.copy(currentRuqyahVerseIndex = currentIndex + 1) }
        } else {
            showToast("تم الانتهاء من قراءة كافة آيات الرقية والشفاء والسكينة 🌿")
        }
    }

    fun prevRuqyahVerse() {
        val currentIndex = _uiState.value.currentRuqyahVerseIndex
        if (currentIndex > 0) {
            _uiState.update { it.copy(currentRuqyahVerseIndex = currentIndex - 1) }
        }
    }

    fun completeCurrentVerseAndAdvance() {
        val currentVerse = HealingVersesData.list.getOrNull(_uiState.value.currentRuqyahVerseIndex) ?: return
        val updatedSet = _uiState.value.completedVerseIds + currentVerse.id
        
        _uiState.update { it.copy(completedVerseIds = updatedSet) }
        
        if (_uiState.value.currentRuqyahVerseIndex < HealingVersesData.list.size - 1) {
            nextRuqyahVerse()
        } else {
            showToast("ختمت قراءة آيات الشفاء والسكينة بنجاح، تقبل الله منكم ✨")
            // Mark Ruqyah task as done in daily record
            if (!_uiState.value.dailyRecord.ruqyahDone) {
                toggleTask(TaskType.RUQYAH)
            }
        }
    }

    fun resetRuqyahVerseProgress() {
        _uiState.update { 
            it.copy(
                completedVerseIds = emptySet(),
                currentRuqyahVerseIndex = 0
            ) 
        }
        showToast("تم إعادة ضبط التقدم في آيات الشفاء")
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
