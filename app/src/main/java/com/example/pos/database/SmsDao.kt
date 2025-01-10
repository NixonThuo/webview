package com.example.pos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SmsDao {
    @Insert
    suspend fun insert(sms: SmsEntity)

    @Insert
    suspend fun insertAll(smsList: List<SmsEntity>) // Insert multiple SMS entities

    @Query("SELECT * FROM sms_table ORDER BY timestamp DESC")
    suspend fun getAllSms(): List<SmsEntity>

    @Query("UPDATE sms_table SET isSynchronized = :isSynchronized, isSynchronizedDate = :isSynchronizedDate WHERE id = :id")
    suspend fun updateSynchronizationStatus(
        id: Int,
        isSynchronized: Boolean,
        isSynchronizedDate: Long
    )

    @Query("SELECT * FROM sms_table WHERE isSynchronized = 0 ORDER BY timestamp DESC")
    suspend fun getUnsynchronizedSms(): List<SmsEntity>
}
