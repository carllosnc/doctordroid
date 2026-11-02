package com.carlosnc.doctordroid.modules.cpu

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class CoreFrequency(
    val coreIndex: Int,
    val frequencyMhz: Long,
    val isOnline: Boolean
)

data class CpuUiState(
    val processorName: String = Build.HARDWARE,
    val coreCount: Int = Runtime.getRuntime().availableProcessors(),
    val architecture: String = System.getProperty("os.arch") ?: "Unknown",
    val supportedAbis: String = Build.SUPPORTED_ABIS.joinToString(", "),
    val coreFrequencies: List<CoreFrequency> = emptyList(),
    val isLoading: Boolean = true
)

class CpuViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CpuUiState())
    val uiState: StateFlow<CpuUiState> = _uiState.asStateFlow()

    init {
        startFrequencyMonitoring()
    }

    private fun startFrequencyMonitoring() {
        viewModelScope.launch(Dispatchers.IO) {
            val cores = _uiState.value.coreCount
            while (isActive) {
                val freqs = (0 until cores).map { index ->
                    val freq = getCoreFrequency(index)
                    CoreFrequency(
                        coreIndex = index,
                        frequencyMhz = freq,
                        isOnline = freq > 0
                    )
                }
                _uiState.update {
                    it.copy(
                        coreFrequencies = freqs,
                        isLoading = false
                    )
                }
                delay(1000)
            }
        }
    }
}
