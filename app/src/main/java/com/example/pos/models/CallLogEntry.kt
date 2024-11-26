package com.example.pos.models

import android.provider.CallLog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf

data class CallLogEntry(
    val number: String,
    val date: String,
    val duration: String,
    val type: String
)

fun fetchCallLogs(context: Context): List<CallLogEntry> {
    val callLogs = mutableListOf<CallLogEntry>()
    val cursor = context.contentResolver.query(
        CallLog.Calls.CONTENT_URI,
        null,
        null,
        null,
        CallLog.Calls.DATE + " DESC" // Sort by recent calls
    )

    cursor?.use {
        val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
        val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
        val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)

        while (it.moveToNext()) {
            val number = it.getString(numberIndex)
            val date = it.getString(dateIndex)
            val duration = it.getString(durationIndex)
            val type = when (it.getInt(typeIndex)) {
                CallLog.Calls.INCOMING_TYPE -> "Incoming"
                CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                CallLog.Calls.MISSED_TYPE -> "Missed"
                else -> "Other"
            }

            callLogs.add(CallLogEntry(number, date, duration, type))
        }
    }
    return callLogs
}
