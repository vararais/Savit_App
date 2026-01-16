package com.example.savitapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savitapp.model.EntryUiState
import com.example.savitapp.model.InsertUiEvent
import com.example.savitapp.model.Stuff
import com.example.savitapp.model.toStuff
import com.example.savitapp.repository.StuffRepository // <-- IMPORT WAJIB
import kotlinx.coroutines.launch

class EntryViewModel(private val repository: StuffRepository) : ViewModel() {
    var uiState by mutableStateOf(EntryUiState())
        private set

    fun updateUiState(event: InsertUiEvent) {
        uiState = EntryUiState(insertUiEvent = event, isEntryValid = validateInput(event))
    }

    private fun validateInput(event: InsertUiEvent = uiState.insertUiEvent): Boolean {
        return event.namaBarang.isNotBlank() && event.hargaBarang.isNotBlank() && event.rencanaHari.isNotBlank()
    }

    fun saveStuff(userId: Int) {
        viewModelScope.launch {
            if (validateInput()) {
                val stuff = uiState.insertUiEvent.toStuff().copy(userId = userId)
                try {
                    val response = repository.insertStuff(stuff)
                    if (response.isSuccessful) {
                        // Reset form atau navigasi
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}