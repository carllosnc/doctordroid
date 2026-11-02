package com.carlosnc.doctordroid.modules.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsInputAntenna
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem

data class SensorItemData(
    val type: Int,
    val name: String,
    val description: String,
    val isPresent: Boolean,
    val targetTab: SensorTab?
)

@Composable
fun SensorCheckList(
    onSensorClick: (SensorTab) -> Unit,
    sensorList: List<SensorItemData>? = null
) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    val sensors = sensorList ?: remember(sensorManager) {
        listOf(
            SensorItemData(
                type = Sensor.TYPE_ACCELEROMETER,
                name = "Accelerometer",
                description = "Measures 3-axis acceleration • Tap to test",
                isPresent = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null,
                targetTab = SensorTab.ACCELEROMETER
            ),
            SensorItemData(
                type = Sensor.TYPE_GYROSCOPE,
                name = "Gyroscope",
                description = "Measures device tilt & rotation • Tap to test",
                isPresent = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null,
                targetTab = SensorTab.ACCELEROMETER
            ),
            SensorItemData(
                type = Sensor.TYPE_PROXIMITY,
                name = "Proximity Sensor",
                description = "Detects objects near screen top • Tap to test",
                isPresent = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null,
                targetTab = SensorTab.PROXIMITY
            ),
            SensorItemData(
                type = Sensor.TYPE_LIGHT,
                name = "Ambient Light Sensor",
                description = "Measures surrounding light lux • Tap to test",
                isPresent = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null,
                targetTab = SensorTab.LIGHT
            ),
            SensorItemData(
                type = Sensor.TYPE_MAGNETIC_FIELD,
                name = "Magnetic Compass",
                description = "Detects Earth's magnetic north",
                isPresent = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null,
                targetTab = null
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        sensors.forEach { item ->
            val icon = when (item.type) {
                Sensor.TYPE_ACCELEROMETER -> Icons.AutoMirrored.Outlined.DirectionsRun
                Sensor.TYPE_GYROSCOPE -> Icons.Outlined.Sync
                Sensor.TYPE_PROXIMITY -> Icons.Outlined.SettingsInputAntenna
                Sensor.TYPE_LIGHT -> Icons.Outlined.LightMode
                else -> Icons.Outlined.Explore
            }

            val rightIcon = when {
                !item.isPresent -> Icons.Outlined.Error
                item.targetTab != null -> Icons.AutoMirrored.Outlined.KeyboardArrowRight
                else -> Icons.Outlined.CheckCircle
            }

            DashboardListItem(
                title = item.name,
                subtitle = if (item.isPresent) item.description else "Sensor not detected on this hardware",
                leftIcon = icon,
                rightIcon = rightIcon,
                onClick = {
                    item.targetTab?.let { onSensorClick(it) }
                }
            )
        }
    }
}