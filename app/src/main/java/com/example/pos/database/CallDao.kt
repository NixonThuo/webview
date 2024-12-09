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
}
