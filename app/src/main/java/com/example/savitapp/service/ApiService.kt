package com.example.savitapp.service

import com.example.savitapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // KEMBALIKAN KE INI (Hapus 'user/'):
    @GET("stuff/{userId}")
    suspend fun getAllStuff(@Path("userId") userId: Int): Response<List<Stuff>>

    // Biarkan ini (Walaupun tidak akan kita pakai lagi di ViewModel):
    @GET("stuff/{id}")
    suspend fun getStuffDetail(@Path("id") id: Int): Response<List<Stuff>>

    @POST("stuff")
    suspend fun createStuff(@Body request: Stuff): Response<GeneralResponse>

    @POST("transaction")
    suspend fun addTransaction(@Body request: TransactionRequest): Response<GeneralResponse>

    @PUT("stuff/{id}")
    suspend fun updateStuff(@Path("id") id: Int, @Body request: Stuff): Response<GeneralResponse>

    @DELETE("stuff/{id}")
    suspend fun deleteStuff(@Path("id") id: Int): Response<GeneralResponse>

    @GET("transaction/{stuffId}")
    suspend fun getHistory(@Path("stuffId") stuffId: Int): Response<List<History>>

    @GET("transaction/history/{userId}")
    suspend fun getUserHistory(@Path("userId") userId: Int): Response<List<History>>
}