package com.example.pos.models

import android.content.Context
import android.net.Uri

data class SmsMessageEntry(
    val address: String,   // Sender's number
    val body: String,       // Message content
    val date: String        // Date received
)

fun fetchSmsMessages(context: Context): List<SmsMessageEntry> {
    val smsList = mutableListOf<SmsMessageEntry>()
    val uri = Uri.parse("content://sms/inbox")
    val cursor = context.contentResolver.query(
        uri,
        null,
        null,
        null,
        "date DESC" // Sort by most recent
    )

    cursor?.use {
        val addressIndex = it.getColumnIndex("address")
        val bodyIndex = it.getColumnIndex("body")
        val dateIndex = it.getColumnIndex("date")

        while (it.moveToNext()) {
            val address = it.getString(addressIndex)
            val body = it.getString(bodyIndex)
            val date = it.getString(dateIndex)

            smsList.add(SmsMessageEntry(address, body, date))
        }
    }
    return smsList
}
