package com.example.pos

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.pos.database.SmsDatabase
import com.example.pos.services.DataUploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Pos : Application() {

    private val handler = Handler(Looper.getMainLooper())
    private val interval: Long = 1 * 60 * 1000 // 15 minutes in milliseconds

    override fun onCreate() {
        super.onCreate()

        // Initialize dependencies (CallDao and SmsDao)
        val callDao = SmsDatabase.getDatabase(this).callDao()
        val smsDao = SmsDatabase.getDatabase(this).smsDao()

        // Create an instance of DataUploader
        val dataUploader = DataUploader(callDao, smsDao)

        // Schedule periodic uploads
        handler.post(object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("MyApplication", "Starting data upload")
                    dataUploader.uploadData()
                }
                handler.postDelayed(this, interval)
            }
        })
    }
}
