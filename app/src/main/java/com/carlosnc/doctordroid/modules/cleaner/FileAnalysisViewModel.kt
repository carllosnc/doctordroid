package com.carlosnc.doctordroid.modules.cleaner

import android.app.Application
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FileAnalysisUiState {
    data object Loading : FileAnalysisUiState
    data object PermissionRequired : FileAnalysisUiState
    data class Success(
        val totalUsedBytes: Long,
        val categories: List<FileCategory>
    ) : FileAnalysisUiState
    data class Error(val message: String) : FileAnalysisUiState
}

class FileAnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<FileAnalysisUiState>(FileAnalysisUiState.Loading)
    val uiState: StateFlow<FileAnalysisUiState> = _uiState.asStateFlow()

    private var hasLoaded = false

    fun onPermissionGranted() {
        if (!hasLoaded) {
            scanFiles()
        }
    }

    fun onPermissionDenied() {
        _uiState.value = FileAnalysisUiState.PermissionRequired
    }

    fun scanFiles() {
        _uiState.value = FileAnalysisUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val categories = scanFileCategories(context)
                val stat = StatFs(Environment.getDataDirectory().path)
                val totalUsed = stat.totalBytes - stat.availableBytes
                hasLoaded = true
                _uiState.value = FileAnalysisUiState.Success(
                    totalUsedBytes = totalUsed,
                    categories = categories
                )
            } catch (e: Exception) {
                _uiState.value = FileAnalysisUiState.Error(e.localizedMessage ?: "Falha ao analisar armazenamento")
            }
        }
    }
}
