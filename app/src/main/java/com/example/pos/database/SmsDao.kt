package com.example.pos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pos.SmsEntity

@Dao
interface SmsDao {
    @Insert
    suspend fun insert(sms: SmsEntity)  // Inserts an SMS into the database

    @Query("SELECT * FROM sms_table ORDER BY timestamp DESC")
    suspend fun getAllSms(): List<SmsEntity>  // Retrieves all SMS entries
}
