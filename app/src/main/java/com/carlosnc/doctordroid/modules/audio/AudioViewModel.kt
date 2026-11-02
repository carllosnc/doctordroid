package com.carlosnc.doctordroid.modules.audio

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

class AudioViewModel(application: Application) : AndroidViewModel(application) {

    private val _audioInfo = MutableStateFlow(getAudioInfo(application))
    val audioInfo: StateFlow<AudioInfo> = _audioInfo.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch(Dispatchers.IO) {
            val app = getApplication<Application>()
            while (isActive) {
                _audioInfo.value = getAudioInfo(app)
                delay(2000)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _audioInfo.value = getAudioInfo(getApplication())
        }
    }
}
