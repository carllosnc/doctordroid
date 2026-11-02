package com.carlosnc.doctordroid.modules.network

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    private val _networkInfo = MutableStateFlow(getNetworkDetails(application))
    val networkInfo: StateFlow<NetworkDetails> = _networkInfo.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch(Dispatchers.IO) {
            val app = getApplication<Application>()
            while (isActive) {
                _networkInfo.value = getNetworkDetails(app)
                delay(3000)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _networkInfo.value = getNetworkDetails(getApplication())
        }
    }
}
