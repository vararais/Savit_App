package com.example.savitapp.repository

import com.example.savitapp.model.GeneralResponse
import com.example.savitapp.model.History
import com.example.savitapp.model.Stuff
import com.example.savitapp.model.TransactionRequest
import com.example.savitapp.service.ApiService
import retrofit2.Response

interface StuffRepository {
    suspend fun getAllStuff(userId: Int): Response<List<Stuff>>
    suspend fun insertStuff(stuff: Stuff): Response<GeneralResponse>
    suspend fun addTransaction(stuffId: Int, nominal: Long): Response<GeneralResponse>
    suspend fun updateStuff(id: Int, stuff: Stuff): Response<GeneralResponse>
    suspend fun deleteStuff(id: Int): Response<GeneralResponse>
    suspend fun getHistory(stuffId: Int): Response<List<History>>
    suspend fun getUserHistory(userId: Int): Response<List<History>>
    suspend fun getStuffDetail(id: Int): Response<List<Stuff>> // <-- KITA TAMBAHKAN LAGI
}

class NetworkStuffRepository(private val apiService: ApiService) : StuffRepository {
    override suspend fun getAllStuff(userId: Int) = apiService.getAllStuff(userId)
    override suspend fun insertStuff(stuff: Stuff) = apiService.createStuff(stuff)
    override suspend fun addTransaction(stuffId: Int, nominal: Long) = apiService.addTransaction(TransactionRequest(stuffId, nominal))
    override suspend fun updateStuff(id: Int, stuff: Stuff) = apiService.updateStuff(id, stuff)
    override suspend fun deleteStuff(id: Int) = apiService.deleteStuff(id)
    override suspend fun getHistory(stuffId: Int) = apiService.getHistory(stuffId)
    override suspend fun getUserHistory(userId: Int) = apiService.getUserHistory(userId)
    override suspend fun getStuffDetail(id: Int): Response<List<Stuff>> {
        return apiService.getStuffDetail(id)
    }
}