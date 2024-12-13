package com.example.pos.services

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("upload/call")
    fun uploadCall(
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("callType") callType: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("contactName") contactName: RequestBody,
        @Part("messageType") messageType: RequestBody,
        @Part("messagePriority") messagePriority: RequestBody
    ): Call<Void>

    @Multipart
    @POST("upload/sms")
    fun uploadSms(
        @Part("sender") sender: RequestBody,
        @Part("messageBody") messageBody: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("messageType") messageType: RequestBody,
        @Part("messagePriority") messagePriority: RequestBody
    ): Call<Void>
}
