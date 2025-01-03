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
import androidx.annotation.RequiresApi
import com.example.pos.database.SmsDatabase
import com.example.pos.database.SmsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsService : Service() {

    companion object {
        const val CHANNEL_ID = "SmsServiceChannel"
        const val CHANNEL_NAME = "SMS Processing Service"
        const val NOTIFICATION_ID = 1
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Waiting for SMS..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra("sender") && intent.hasExtra("messageBody")) {
            val sender = intent.getStringExtra("sender")
            val messageBody = intent.getStringExtra("messageBody")
            val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())

            // Update the notification to show processing
            updateNotification("Processing SMS from $sender")

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

                // Restore notification state
                updateNotification("Waiting for SMS...")
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
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(contentText: String): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("SMS Service")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.sym_action_chat)
            .setOngoing(true) // Prevent users from swiping away the notification
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNotification(contentText: String) {
        val notification = createNotification(contentText)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SmsService", "Foreground Service destroyed")
    }
}
