package com.example.pos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CallDao {
    @Insert
    suspend fun insert(call: CallEntity)

    @Query("SELECT * FROM call_log ORDER BY timestamp DESC")
    suspend fun getAllCalls(): List<CallEntity>

    @Query("UPDATE call_log SET isSynchronized = :isSynchronized, isSynchronizedDate = :isSynchronizedDate WHERE id = :id")
    suspend fun updateSynchronizationStatus(
        id: Long,
        isSynchronized: Boolean,
        isSynchronizedDate: Long
    )

    @Query("SELECT * FROM call_log WHERE isSynchronized = 0 ORDER BY timestamp DESC")
    suspend fun getUnsynchronizedCalls(): List<CallEntity>
}
