package com.example.savitapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savitapp.model.Stuff
import com.example.savitapp.repository.StuffRepository
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data class Success(val stuff: Stuff) : DetailUiState
    data class Error(val message: String) : DetailUiState
    object Loading : DetailUiState
}

class DetailViewModel(private val repository: StuffRepository) : ViewModel() {
    var detailUiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    fun getStuffDetail(userId: Int, stuffId: Int) {
        viewModelScope.launch {
            detailUiState = DetailUiState.Loading
            try {
                // TRIK: Panggil getAllStuff (Jalur Dashboard yang Aman)
                val response = repository.getAllStuff(userId)

                if (response.isSuccessful) {
                    val allStuff = response.body()
                    // Cari barang yang ID-nya sama dengan stuffId
                    val targetStuff = allStuff?.find { it.stuffId == stuffId }

                    if (targetStuff != null) {
                        detailUiState = DetailUiState.Success(targetStuff)
                    } else {
                        detailUiState = DetailUiState.Error("Barang tidak ditemukan")
                    }
                } else {
                    detailUiState = DetailUiState.Error("Gagal memuat: ${response.message()}")
                }
            } catch (e: Exception) {
                detailUiState = DetailUiState.Error("Error: ${e.message}")
            }
        }
    }

    // Fungsi Delete (Biarkan tetap ada)
    fun deleteStuff(stuffId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try { repository.deleteStuff(stuffId); onSuccess() } catch (e: Exception) {}
        }
    }
}