package com.example.pos

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pos.database.SmsDatabase
import com.example.pos.database.SmsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsService : Service() {

    companion object {
        private const val CHANNEL_ID = "SmsServiceChannel"
        private const val NOTIFICATION_ID = 1001
    }

    @SuppressLint("ForegroundServiceType", "NewApi")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createPersistentNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra("sender") && intent.hasExtra("messageBody")) {
            val sender = intent.getStringExtra("sender")
            val messageBody = intent.getStringExtra("messageBody")
            val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())

            Log.d("SmsService", "Processing SMS from $sender: $messageBody")

            // Save SMS to Room Database
            val smsEntity = SmsEntity(
                sender = sender ?: "",
                messageBody = messageBody ?: "",
                timestamp = timestamp,
                isSynchronized = false,
                isSynchronizedDate = timestamp,
                messageType = "",
                messagePriority = ""
            )

            CoroutineScope(Dispatchers.IO).launch {
                val db = SmsDatabase.getDatabase(applicationContext)
                db.smsDao().insert(smsEntity)
                Log.d("SmsService", "SMS saved to database")
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SMS Service Notifications",
                NotificationManager.IMPORTANCE_LOW // Ensure it's not set to NONE
            ).apply {
                description = "Notifications for the SMS Service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }


    private fun createPersistentNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS Service Running")
                .setContentText("Listening for incoming SMS messages...")
                .setSmallIcon(android.R.drawable.sym_def_app_icon) // Replace with your app's icon
                .setOngoing(true)
                .build()
        } else {
            NotificationCompat.Builder(this)
                .setContentTitle("SMS Service Running")
                .setContentText("Listening for incoming SMS messages...")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setOngoing(true)
                .build()
        }
    }

}
