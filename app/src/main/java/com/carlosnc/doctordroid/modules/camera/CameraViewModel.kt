package com.carlosnc.doctordroid.modules.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val _cameras = MutableStateFlow(getCameraDetails(application))
    val cameras: StateFlow<List<CameraInfo>> = _cameras.asStateFlow()

    fun refresh() {
        _cameras.value = getCameraDetails(getApplication())
    }
}
