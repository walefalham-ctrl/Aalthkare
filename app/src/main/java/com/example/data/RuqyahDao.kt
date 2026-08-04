package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RuqyahDao {

    @Query("SELECT * FROM daily_records WHERE date = :date LIMIT 1")
    fun getDailyRecord(date: String): Flow<DailyRecordEntity?>

    @Query("SELECT * FROM daily_records ORDER BY date DESC")
    fun getAllHistoryRecords(): Flow<List<DailyRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyRecord(record: DailyRecordEntity)

    @Query("DELETE FROM daily_records")
    suspend fun clearAllHistory()

    @Query("SELECT * FROM zikr_progress WHERE date = :date")
    fun getZikrProgressForDate(date: String): Flow<List<ZikrProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveZikrProgress(progress: ZikrProgressEntity)

    @Query("DELETE FROM zikr_progress WHERE date = :date AND id = :id")
    suspend fun resetZikrProgress(date: String, id: String)

    @Query("DELETE FROM zikr_progress WHERE date = :date")
    suspend fun clearZikrProgressForDate(date: String)

    @Query("SELECT * FROM observation_logs ORDER BY timestamp DESC")
    fun getAllObservationLogs(): Flow<List<ObservationLogEntity>>

    @Query("SELECT * FROM observation_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getObservationLogsForDate(date: String): Flow<List<ObservationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservationLog(log: ObservationLogEntity)

    @Query("DELETE FROM observation_logs WHERE id = :id")
    suspend fun deleteObservationLogById(id: Int)

    @Query("DELETE FROM observation_logs")
    suspend fun clearAllObservationLogs()
}
