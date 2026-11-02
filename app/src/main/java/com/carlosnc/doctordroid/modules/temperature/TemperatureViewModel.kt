package com.carlosnc.doctordroid.modules.temperature

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.carlosnc.doctordroid.modules.battery.getBatteryTemperature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TemperatureUiState(
    val batteryTemp: Float = 0f,
    val ambientTemp: Float? = null
)

class TemperatureViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        TemperatureUiState(
            batteryTemp = getBatteryTemperature(application)
        )
    )
    val uiState: StateFlow<TemperatureUiState> = _uiState.asStateFlow()

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val tempSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                _uiState.update { it.copy(ambientTemp = event.values[0]) }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    init {
        if (tempSensor != null && sensorManager != null) {
            sensorManager.registerListener(sensorEventListener, tempSensor, SensorManager.SENSOR_DELAY_UI)
        }
        startBatteryTempMonitoring()
    }

    private fun startBatteryTempMonitoring() {
        viewModelScope.launch(Dispatchers.IO) {
            val app = getApplication<Application>()
            while (isActive) {
                val temp = getBatteryTemperature(app)
                _uiState.update { it.copy(batteryTemp = temp) }
                delay(3000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager?.unregisterListener(sensorEventListener)
    }
}
