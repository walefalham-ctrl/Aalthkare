package com.example.data

import kotlinx.coroutines.flow.Flow

class RuqyahRepository(private val dao: RuqyahDao) {

    fun getDailyRecord(date: String): Flow<DailyRecordEntity?> {
        return dao.getDailyRecord(date)
    }

    fun getAllHistoryRecords(): Flow<List<DailyRecordEntity>> {
        return dao.getAllHistoryRecords()
    }

    suspend fun saveDailyRecord(record: DailyRecordEntity) {
        dao.insertOrUpdateDailyRecord(record)
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }

    fun getZikrProgressForDate(date: String): Flow<List<ZikrProgressEntity>> {
        return dao.getZikrProgressForDate(date)
    }

    suspend fun saveZikrProgress(progress: ZikrProgressEntity) {
        dao.saveZikrProgress(progress)
    }

    suspend fun resetZikrProgress(date: String, id: String) {
        dao.resetZikrProgress(date, id)
    }

    suspend fun clearZikrProgressForDate(date: String) {
        dao.clearZikrProgressForDate(date)
    }

    fun getAllObservationLogs(): Flow<List<ObservationLogEntity>> {
        return dao.getAllObservationLogs()
    }

    fun getObservationLogsForDate(date: String): Flow<List<ObservationLogEntity>> {
        return dao.getObservationLogsForDate(date)
    }

    suspend fun insertObservationLog(log: ObservationLogEntity) {
        dao.insertObservationLog(log)
    }

    suspend fun deleteObservationLogById(id: Int) {
        dao.deleteObservationLogById(id)
    }

    suspend fun clearAllObservationLogs() {
        dao.clearAllObservationLogs()
    }
}
