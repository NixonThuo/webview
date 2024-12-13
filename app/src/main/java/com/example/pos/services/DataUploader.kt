package com.example.pos.services

import android.os.Handler
import android.os.Looper
import com.example.pos.database.CallDao
import com.example.pos.database.SmsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DataUploader(private val callDao: CallDao, private val smsDao: SmsDao) {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/api/") // Replace with your base URL
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    fun uploadData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Step 1: Read call data from Room
                val callEntities = callDao.getAllCalls()

                callEntities.forEach { call ->
                    val phoneNumber = RequestBody.create("text/plain".toMediaTypeOrNull(), call.phoneNumber ?: "")
                    val callType = RequestBody.create("text/plain".toMediaTypeOrNull(), call.callType)
                    val timestamp = RequestBody.create("text/plain".toMediaTypeOrNull(), call.timestamp.toString())
                    val contactName = RequestBody.create("text/plain".toMediaTypeOrNull(), call.contactName)
                    val messageType = RequestBody.create("text/plain".toMediaTypeOrNull(), call.messageType)
                    val messagePriority = RequestBody.create("text/plain".toMediaTypeOrNull(), call.messagePriority)

                    val callRequest = apiService.uploadCall(phoneNumber, callType, timestamp, contactName, messageType, messagePriority)
                    callRequest.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Handler(Looper.getMainLooper()).post {
                                    // Update UI or perform success actions
                                }
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                }

                // Step 2: Read SMS data from Room
                val smsEntities = smsDao.getAllSms()

                smsEntities.forEach { sms ->
                    val sender = RequestBody.create("text/plain".toMediaTypeOrNull(), sms.sender)
                    val messageBody = RequestBody.create("text/plain".toMediaTypeOrNull(), sms.messageBody)
                    val timestamp = RequestBody.create("text/plain".toMediaTypeOrNull(), sms.timestamp.toString())
                    val messageType = RequestBody.create("text/plain".toMediaTypeOrNull(), sms.messageType)
                    val messagePriority = RequestBody.create("text/plain".toMediaTypeOrNull(), sms.messagePriority)

                    val smsRequest = apiService.uploadSms(sender, messageBody, timestamp, messageType, messagePriority)
                    smsRequest.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Handler(Looper.getMainLooper()).post {
                                    // Update UI or perform success actions
                                }
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

