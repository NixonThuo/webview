package com.example.pos.models

import android.provider.CallLog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import java.net.InetAddress

data class CallLogEntry(
    val address: String,
    val number: String,
    val date: String,
    val duration: String,
    val type: String,
    val name: String
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
        val addressIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
        val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
        val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
        val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
        val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
        val  nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)

        while (it.moveToNext()) {
            val address = it.getString(addressIndex)
            val number = it.getString(numberIndex)
            val date = it.getString(dateIndex)
            val duration = it.getString(durationIndex)
            val type = when (it.getInt(typeIndex)) {
                CallLog.Calls.INCOMING_TYPE -> "Incoming"
                CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                CallLog.Calls.MISSED_TYPE -> "Missed"
                else -> "Other"
            }
            val name = it.getString(nameIndex)

            callLogs.add(CallLogEntry(address, number, date, duration, type, name))
        }
    }
    return callLogs
}
