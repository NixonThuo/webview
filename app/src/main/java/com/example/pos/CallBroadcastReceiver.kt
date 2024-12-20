package com.example.pos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.pos.database.CallEntity
import com.example.pos.database.SmsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val db = SmsDatabase.getDatabase(context) // Get the database instance

        val action = intent.action
        if (action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) // May be null for outgoing

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    // Incoming call ringing
                    if (!phoneNumber.isNullOrEmpty()) {
                        Log.d("CallReceiver", "Incoming call from: $phoneNumber")
                        val call = CallEntity(
                            phoneNumber = phoneNumber,
                            callType = "Incoming",
                            timestamp = System.currentTimeMillis(),
                            contactName = getContactName(context, phoneNumber),
                            isSynchronized = false,
                            isSynchronizedDate = System.currentTimeMillis(),
                            callPriority = "High"
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            db.callDao().insert(call)
                        }
                    }
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    // Call answered or outgoing initiated

                    if (intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) == null) {
                        // Incoming call answered
                        Log.d("CallReceiver", "Outgoing call answered")
                    } else {
                        // Outgoing call initiated
                        val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                        Log.d("CallReceiver", "Outgoing call to: $phoneNumber")
                        val call = CallEntity(
                            phoneNumber = phoneNumber ?: "Unknown",
                            callType = "Outgoing",
                            timestamp = System.currentTimeMillis(),
                            contactName = "",
                            isSynchronized = false,
                            isSynchronizedDate = System.currentTimeMillis(),
                            callPriority = "Normal"
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            db.callDao().insert(call)
                        }
                    }
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    // Call ended
                    Log.d("CallReceiver", "Call ended")
                }
            }
        } else if (action == Intent.ACTION_NEW_OUTGOING_CALL) {
            val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) // Outgoing number
            if (!phoneNumber.isNullOrEmpty()) {
                Log.d("CallReceiver", "Outgoing call to: $phoneNumber")
                val call = CallEntity(
                    phoneNumber = phoneNumber,
                    callType = "Outgoing",
                    timestamp = System.currentTimeMillis(),
                    contactName = getContactName(context, phoneNumber),
                    isSynchronized = false,
                    isSynchronizedDate = System.currentTimeMillis(),
                    callPriority = "Normal"
                )
                CoroutineScope(Dispatchers.IO).launch {
                    db.callDao().insert(call)
                }
            }
        }
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

