package com.example.pos.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "call_log")
data class CallEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String?,
    val callType: String, // "Incoming" or "Outgoing"
    val timestamp: Long, // When the call was detected
    val contactName: String,
    val isSynchronized: Boolean,
    val isSynchronizedDate: Long,
    val messageType: String,
    val messagePriority: String
)
