package com.example.parcial2_permisos_gabrielcampos.ui.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PermissionsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PermissionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PermissionsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}