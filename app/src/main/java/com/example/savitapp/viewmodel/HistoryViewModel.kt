package com.example.savitapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savitapp.model.History
import com.example.savitapp.repository.StuffRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface HistoryUiState {
    data class Success(val historyList: List<History>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
    object Loading : HistoryUiState
}

// GANTI constructor parameter jadi StuffRepository
class HistoryViewModel(private val repository: StuffRepository) : ViewModel() {

    var historyUiState: HistoryUiState by mutableStateOf(HistoryUiState.Loading)
        private set

    fun getHistoryList(userId: Int) {
        viewModelScope.launch {
            historyUiState = HistoryUiState.Loading
            try {
                // Ubah baris ini:
                val response = repository.getUserHistory(userId) // Panggil getUserHistory
                if (response.isSuccessful) {
                    historyUiState = HistoryUiState.Success(response.body() ?: emptyList())
                } else {
                    historyUiState = HistoryUiState.Error("Gagal memuat: ${response.message()}")
                }
            } catch (e: IOException) {
                historyUiState = HistoryUiState.Error("Gagal memuat riwayat: Koneksi Error")
            } catch (e: Exception) {
                historyUiState = HistoryUiState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}