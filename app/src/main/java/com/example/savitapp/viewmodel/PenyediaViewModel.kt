package com.example.savitapp.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.savitapp.SavitApplication

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(aplikasiSavit().container.authRepository)
        }
        initializer {
            RegisterViewModel(aplikasiSavit().container.authRepository)
        }

        initializer {
            HomeViewModel(aplikasiSavit().container.stuffRepository)
        }

        initializer {
            EntryViewModel(aplikasiSavit().container.stuffRepository)
        }

        initializer {
            QuickAddViewModel(aplikasiSavit().container.stuffRepository)
        }

        initializer {
            DetailViewModel(aplikasiSavit().container.stuffRepository)
        }

        initializer {
            EditViewModel(aplikasiSavit().container.stuffRepository)
        }

        initializer {
            HistoryViewModel(aplikasiSavit().container.stuffRepository)
        }
    }
}

// Extension function untuk akses Application Container
fun CreationExtras.aplikasiSavit(): SavitApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SavitApplication)