package com.example.pos.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.pos.SmsEntity

@Database(entities = [SmsEntity::class], version = 1)
abstract class SmsDatabase : RoomDatabase() {
    abstract fun smsDao(): SmsDao

    companion object {
        @Volatile
        private var INSTANCE: SmsDatabase? = null

        fun getDatabase(context: Context): SmsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmsDatabase::class.java,
                    "sms_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
