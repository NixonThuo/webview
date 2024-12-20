package com.example.pos

import android.annotation.SuppressLint
import android.content.Context
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.example.pos.database.CallEntity
import com.example.pos.database.SmsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyCallScreeningService : CallScreeningService() {

    @SuppressLint("NewApi")
    override fun onScreenCall(callDetails: Call.Details) {
        val db = SmsDatabase.getDatabase(applicationContext)

        val phoneNumber = callDetails.handle?.schemeSpecificPart
        val timestamp = System.currentTimeMillis()

        when (callDetails.callDirection) {
            Call.Details.DIRECTION_INCOMING -> {
                Log.d("CallScreeningService", "Incoming call from: $phoneNumber")
                if (!phoneNumber.isNullOrEmpty()) {
                    val call = CallEntity(
                        phoneNumber = phoneNumber,
                        callType = "Incoming",
                        timestamp = timestamp,
                        contactName = getContactName(applicationContext, phoneNumber),
                        isSynchronized = false,
                        isSynchronizedDate = timestamp,
                        callPriority = "High"
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.callDao().insert(call)
                    }
                }
            }

            Call.Details.DIRECTION_OUTGOING -> {
                Log.d("CallScreeningService", "Outgoing call to: $phoneNumber")
                if (!phoneNumber.isNullOrEmpty()) {
                    val call = CallEntity(
                        phoneNumber = phoneNumber,
                        callType = "Outgoing",
                        timestamp = timestamp,
                        contactName = getContactName(applicationContext, phoneNumber),
                        isSynchronized = false,
                        isSynchronizedDate = timestamp,
                        callPriority = "Normal"
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.callDao().insert(call)
                    }
                }
            }

            else -> Log.d("CallScreeningService", "Unknown call direction")
        }

        // Let the call proceed without modification
        respondToCall(callDetails, CallResponse.Builder().setDisallowCall(false).build())
    }

    private fun getContactName(context: Context, phoneNumber: String): String {
        val contentResolver = context.contentResolver
        val uri = android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
            .appendPath(phoneNumber)
            .build()
        val projection = arrayOf(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName = "Unknown"

        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                contactName = it.getString(it.getColumnIndexOrThrow(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
        }

        return contactName
    }
}
