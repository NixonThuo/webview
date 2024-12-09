package com.example.pos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.example.pos.database.SmsDatabase
import com.example.pos.database.SmsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val bundle = intent.extras
            if (bundle != null) {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                for (sms in messages) {
                    val sender = sms.displayOriginatingAddress
                    val messageBody = sms.messageBody
                    val timestamp = System.currentTimeMillis() // Current timestamp

                    // Display SMS data using Log or Toast
                    Log.d("SmsReceiver", "SMS from $sender: $messageBody")
                    Toast.makeText(context, "SMS from $sender: $messageBody", Toast.LENGTH_LONG).show()

                    // Save SMS to Room Database
                    val smsEntity = SmsEntity(
                        sender = sender,
                        messageBody = messageBody,
                        timestamp = timestamp
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val db = SmsDatabase.getDatabase(context)
                        db.smsDao().insert(smsEntity)
                    }
                }
            }
        }
    }
}

