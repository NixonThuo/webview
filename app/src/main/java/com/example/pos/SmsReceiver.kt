package com.example.pos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import androidx.annotation.RequiresApi

class SmsReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val sender = sms.displayOriginatingAddress
                val messageBody = sms.messageBody
                val timestamp = System.currentTimeMillis()

                // Start the foreground service and pass SMS data
                val serviceIntent = Intent(context, SmsService::class.java).apply {
                    putExtra("sender", sender)
                    putExtra("messageBody", messageBody)
                    putExtra("timestamp", timestamp)
                }
                context.startForegroundService(serviceIntent)
            }
        }
    }
}


