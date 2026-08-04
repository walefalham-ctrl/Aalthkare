package com.example.data

import androidx.room.Entity

@Entity(
    tableName = "zikr_progress",
    primaryKeys = ["id", "date"]
)
data class ZikrProgressEntity(
    val id: String, // e.g., "sabah_1"
    val date: String, // YYYY-MM-DD
    val count: Int
)
