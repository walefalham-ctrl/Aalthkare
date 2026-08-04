package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_records")
data class DailyRecordEntity(
    @PrimaryKey val date: String, // format YYYY-MM-DD
    val azkarDone: Boolean = false,
    val baqarahDone: Boolean = false,
    val ruqyahDone: Boolean = false,
    val sadakahDone: Boolean = false,
    val wirdDone: Boolean = false,
    val namesDone: Boolean = false,
    val effectNote: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
