package com.example.pos.services

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("call_data")
    suspend fun uploadCall(
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("callType") callType: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("contactName") contactName: RequestBody,
        @Part("messageType") messageType: RequestBody,
        @Part("messagePriority") messagePriority: RequestBody,
        @Part("android_id") id: RequestBody
    ): Response<Void>

    @Multipart
    @POST("sms_data")
    suspend fun uploadSms(
        @Part("sender") sender: RequestBody,
        @Part("content") messageBody: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("messageType") messageType: RequestBody,
        @Part("messagePriority") messagePriority: RequestBody,
        @Part("android_id") id: RequestBody
    ): Response<Void>
}

