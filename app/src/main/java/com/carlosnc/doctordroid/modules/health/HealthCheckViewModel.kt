package com.carlosnc.doctordroid.modules.health

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HealthCheckUiState(
    val sensors: List<SensorItemData> = emptyList(),
    val showTouchTest: Boolean = false,
    val showDeadPixelTest: Boolean = false,
    val showSensorLab: Boolean = false,
    val sensorLabInitialTab: SensorTab = SensorTab.ACCELEROMETER,
    val showSuccessDialog: Boolean = false
)

class HealthCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HealthCheckUiState())
    val uiState: StateFlow<HealthCheckUiState> = _uiState.asStateFlow()

    init {
        loadSensors()
    }

    private fun loadSensors() {
        val sensorManager = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensorList = listOf(
            SensorItemData(
                type = Sensor.TYPE_ACCELEROMETER,
                name = "Accelerometer",
                description = "Measures 3-axis acceleration • Tap to test",
                isPresent = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null,
                targetTab = SensorTab.ACCELEROMETER
            ),
            SensorItemData(
                type = Sensor.TYPE_GYROSCOPE,
                name = "Gyroscope",
                description = "Measures device tilt & rotation • Tap to test",
                isPresent = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null,
                targetTab = SensorTab.ACCELEROMETER
            ),
            SensorItemData(
                type = Sensor.TYPE_PROXIMITY,
                name = "Proximity Sensor",
                description = "Detects objects near screen top • Tap to test",
                isPresent = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null,
                targetTab = SensorTab.PROXIMITY
            ),
            SensorItemData(
                type = Sensor.TYPE_LIGHT,
                name = "Light Sensor",
                description = "Measures ambient illuminance in Lux • Tap to test",
                isPresent = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT) != null,
                targetTab = SensorTab.LIGHT
            ),
            SensorItemData(
                type = Sensor.TYPE_MAGNETIC_FIELD,
                name = "Magnetometer (Compass)",
                description = "Measures ambient magnetic field",
                isPresent = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null,
                targetTab = null
            ),
            SensorItemData(
                type = Sensor.TYPE_STEP_COUNTER,
                name = "Step Counter",
                description = "Hardware pedometer step detection",
                isPresent = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null,
                targetTab = null
            )
        )
        _uiState.update { it.copy(sensors = sensorList) }
    }

    fun openSensorLab(tab: SensorTab) {
        _uiState.update { it.copy(showSensorLab = true, sensorLabInitialTab = tab) }
    }

    fun closeSensorLab() {
        _uiState.update { it.copy(showSensorLab = false) }
    }

    fun setTouchTestVisible(visible: Boolean) {
        _uiState.update { it.copy(showTouchTest = visible) }
    }

    fun setDeadPixelTestVisible(visible: Boolean) {
        _uiState.update { it.copy(showDeadPixelTest = visible) }
    }

    fun setSuccessDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showSuccessDialog = visible) }
    }
}
