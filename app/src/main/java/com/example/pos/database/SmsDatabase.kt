package com.example.pos.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [SmsEntity::class, CallEntity::class], version = 6, exportSchema = false)
abstract class SmsDatabase : RoomDatabase() {
    abstract fun smsDao(): SmsDao
    abstract fun callDao(): CallDao

    companion object {
        @Volatile
        private var INSTANCE: SmsDatabase? = null

        fun getDatabase(context: Context): SmsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmsDatabase::class.java,
                    "sms_database"
                )
                    .fallbackToDestructiveMigration() // Handle migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
