package com.carlosnc.doctordroid.modules.device

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DeviceUiState(
    val deviceInfo: DeviceInfo = getDeviceInfo(),
    val hardwareInfo: HardwareInfo,
    val systemInfo: SystemInfo = getSystemInfo()
)

class DeviceViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        DeviceUiState(
            hardwareInfo = getHardwareInfo(application)
        )
    )
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()

    fun refresh() {
        _uiState.value = DeviceUiState(
            hardwareInfo = getHardwareInfo(getApplication())
        )
    }
}
