package com.example.pos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val sender = sms.displayOriginatingAddress
                val messageBody = sms.messageBody
                val timestamp = System.currentTimeMillis()

                // Start the service and pass SMS data
                val serviceIntent = Intent(context, SmsService::class.java).apply {
                    putExtra("sender", sender)
                    putExtra("messageBody", messageBody)
                    putExtra("timestamp", timestamp)
                }
                context.startService(serviceIntent)
            }
        }
    }
}




