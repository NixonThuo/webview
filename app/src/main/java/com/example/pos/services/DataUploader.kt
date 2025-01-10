package com.example.pos.services

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.pos.database.CallDao
import com.example.pos.database.SmsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DataUploader(private val callDao: CallDao, private val smsDao: SmsDao, private val preferences: SharedPreferences) {


        private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

        private val  smsURL:String = buildBaseUrl();

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(smsURL) // Dynamically constructed base URL from SharedPreferences
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val apiService: ApiService = retrofit.create(ApiService::class.java)

        private fun buildBaseUrl(): String {
            val protocol = preferences.getString("protocol2", "http") ?: "http"
            val domain = preferences.getString("domain_ip2", "195.35.11.199") ?: "195.35.11.199"
            val port = preferences.getString("port2", "8001") ?: "8001"
            val pageReference = preferences.getString("page_reference2", "") ?: ""
            return if (protocol == "http" && domain == "195.35.11.199" && port == "8001" && pageReference.isEmpty()) {
                "http://195.35.11.199:8001/"
            } else {
                "$protocol://$domain:$port/$pageReference/"
            }
        }

        fun uploadData() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Step 1: Read call data from Room
                    val callEntities = callDao.getUnsynchronizedCalls()

                    callEntities.forEach { call ->
                        val phoneNumber = RequestBody.create(
                            "text/plain".toMediaTypeOrNull(),
                            call.phoneNumber ?: ""
                        )
                        val callType =                            RequestBody.create("text/plain".toMediaTypeOrNull(), call.callType)
                        val timestamp = RequestBody.create(
                            "text/plain".toMediaTypeOrNull(),
                            call.timestamp.toString()
                        )
                        val contactName =                            RequestBody.create("text/plain".toMediaTypeOrNull(), call.contactName)
                        val callPriority =                            RequestBody.create("text/plain".toMediaTypeOrNull(), call.callPriority)
                        val id =                            RequestBody.create("text/plain".toMediaTypeOrNull(), call.id.toString())

                        withContext(Dispatchers.IO) {
                            try {
                                val response = apiService.uploadCall(
                                    phoneNumber, callType, timestamp, contactName, callPriority, id
                                )
                                if (response.isSuccessful) {
                                    Log.d("Upload", "Call data uploaded successfully.")
                                    val currentTime = System.currentTimeMillis()
                                    callDao.updateSynchronizationStatus(call.id, true, currentTime)
                                } else {
                                    Log.e(
                                        "Upload",
                                        "Failed to upload call data: ${
                                            response.errorBody()?.string()
                                        }"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("Upload", "Error uploading call data", e)
                            }
                        }
                    }

                    // Step 2: Read SMS data from Room
                    val smsEntities = smsDao.getUnsynchronizedSms()

                    smsEntities.forEach { sms ->
                        val sender =   RequestBody.create("text/plain".toMediaTypeOrNull(), sms.sender)
                        val messageBody =  RequestBody.create("text/plain".toMediaTypeOrNull(), sms.messageBody)
                        val timestamp = RequestBody.create("text/plain".toMediaTypeOrNull(),         sms.timestamp.toString()                        )
                        val messageType = RequestBody.create("text/plain".toMediaTypeOrNull(), sms.messageType)
                        val messagePriority = RequestBody.create(                            "text/plain".toMediaTypeOrNull(),                            sms.messagePriority                        )
                        val id = RequestBody.create("text/plain".toMediaTypeOrNull(), sms.id.toString())

                        withContext(Dispatchers.IO) {
                            try {
                                val response = apiService.uploadSms(
                                    sender, messageBody, timestamp, messageType, messagePriority, id
                                )
                                if (response.isSuccessful) {
                                    Log.d("Upload", "SMS data uploaded successfully.")
                                    val currentTime = System.currentTimeMillis()
                                    smsDao.updateSynchronizationStatus(sms.id, true, currentTime)
                                } else {
                                    Log.d("Upload Ref = ", smsURL)
                                    Log.e(
                                        "Upload",
                                        "Failed to upload SMS data: ${
                                            response.errorBody()?.string()
                                        }"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("Upload", "Error uploading SMS data", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Upload", "Error uploading data", e)
                }
            }
        }

}
