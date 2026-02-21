package com.carlosnc.doctordroid.ui.components.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        sensorsWithStatus.forEach { (type, name, isPresent) ->
            SensorItem(
                name = name,
                isPresent = isPresent,
                icon = when(type) {
                    Sensor.TYPE_ACCELEROMETER -> Icons.AutoMirrored.Filled.DirectionsRun
                    Sensor.TYPE_GYROSCOPE -> Icons.Default.Sync
                    Sensor.TYPE_LIGHT -> Icons.Default.LightMode
                    Sensor.TYPE_PROXIMITY -> Icons.Default.SettingsInputAntenna
                    else -> Icons.Default.Explore
                }
            )
        }
    }
}

@Composable
fun SensorItem(name: String, isPresent: Boolean, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold)
            Text(
                if (isPresent) "Working Correctly" else "Not Detected",
                color = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Icon(
            imageVector = if (isPresent) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}
