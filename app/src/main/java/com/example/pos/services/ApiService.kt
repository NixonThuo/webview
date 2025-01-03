package com.example.pos.services

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("call_data.jsp?action=incoming")
    suspend fun uploadCall(
        @Part("call_number") phoneNumber: RequestBody,
        @Part("call_type") callType: RequestBody,
        @Part("call_date") timestamp: RequestBody,
        @Part("caller_name") contactName: RequestBody,
        @Part("call_priority") callPriority: RequestBody,
        @Part("android_id") id: RequestBody
    ): Response<Void>

    @Multipart
    @POST("sms_data.jsp?action=incoming")
    suspend fun uploadSms(
        @Part("sender") sender: RequestBody,
        @Part("content") messageBody: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("message_type") messageType: RequestBody,
        @Part("message_priority") messagePriority: RequestBody,
        @Part("android_id") id: RequestBody
    ): Response<Void>
}

