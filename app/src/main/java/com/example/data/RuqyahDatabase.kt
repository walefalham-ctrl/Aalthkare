package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DailyRecordEntity::class, ZikrProgressEntity::class, ObservationLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class RuqyahDatabase : RoomDatabase() {

    abstract fun ruqyahDao(): RuqyahDao

    companion object {
        @Volatile
        private var INSTANCE: RuqyahDatabase? = null

        fun getInstance(context: Context): RuqyahDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RuqyahDatabase::class.java,
                    "ruqyah_app_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
