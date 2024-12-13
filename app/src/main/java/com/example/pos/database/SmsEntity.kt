package com.example.pos.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sms_table")
data class SmsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Auto-generated primary key
    val sender: String,                               // Sender's phone number
    val messageBody: String,                          // SMS content
    val timestamp: Long,                               // Time the SMS was received
    val isSynchronized: Boolean,
    val isSynchronizedDate: Long,
    val messageType: String,
    val messagePriority: String
)
