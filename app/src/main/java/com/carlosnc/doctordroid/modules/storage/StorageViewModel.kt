package com.carlosnc.doctordroid.modules.storage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StorageUiState(
    val storageInfo: StorageInfo,
    val fileTypeUsage: List<FileTypeInfo>,
    val isLoading: Boolean = false
)

class StorageViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        StorageUiState(
            storageInfo = getStorageInfo(),
            fileTypeUsage = getFileTypeUsage()
        )
    )
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = StorageUiState(
                storageInfo = getStorageInfo(),
                fileTypeUsage = getFileTypeUsage(),
                isLoading = false
            )
        }
    }
}
