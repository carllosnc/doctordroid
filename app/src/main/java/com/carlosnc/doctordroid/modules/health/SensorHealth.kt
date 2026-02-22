package com.carlosnc.doctordroid.modules.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun SensorCheckList() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    
    val sensorsWithStatus = remember(sensorManager) {
        listOf(
            Sensor.TYPE_ACCELEROMETER to "Accelerometer",
            Sensor.TYPE_GYROSCOPE to "Gyroscope",
            Sensor.TYPE_LIGHT to "Light Sensor",
            Sensor.TYPE_PROXIMITY to "Proximity Sensor",
            Sensor.TYPE_MAGNETIC_FIELD to "Compass"
        ).map { (type, name) ->
            val isPresent = sensorManager.getDefaultSensor(type) != null
            Triple(type, name, isPresent)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        sensorsWithStatus.forEach { (type, name, isPresent) ->
            val icon = when(type) {
                Sensor.TYPE_ACCELEROMETER -> Icons.AutoMirrored.Filled.DirectionsRun
                Sensor.TYPE_GYROSCOPE -> Icons.Default.Sync
                Sensor.TYPE_LIGHT -> Icons.Default.LightMode
                Sensor.TYPE_PROXIMITY -> Icons.Default.SettingsInputAntenna
                else -> Icons.Default.Explore
            }
            
            DashboardListItem(
                title = name,
                subtitle = if (isPresent) "Working Correctly" else "Not Detected",
                leftIcon = icon,
                rightIcon = if (isPresent) Icons.Default.CheckCircle else Icons.Default.Error,
                onClick = { /* No-op for status items */ }
            )
        }
    }
}
