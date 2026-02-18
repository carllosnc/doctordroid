package com.carlosnc.doctordroid.ui.components.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCheckScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Health Check") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoSection(title = "Sensor Health")
            SensorCheckList()

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            InfoSection(title = "Hardware Diagnostics")
            HardwareTestList()
            
            Box(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SensorCheckList() {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    val sensors = listOf(
        Sensor.TYPE_ACCELEROMETER to "Accelerometer",
        Sensor.TYPE_GYROSCOPE to "Gyroscope",
        Sensor.TYPE_LIGHT to "Light Sensor",
        Sensor.TYPE_PROXIMITY to "Proximity Sensor",
        Sensor.TYPE_MAGNETIC_FIELD to "Compass"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        sensors.forEach { (type, name) ->
            val sensor = sensorManager.getDefaultSensor(type)
            val isPresent = sensor != null
            
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
    ListItem(
        headlineContent = { Text(name, fontWeight = FontWeight.SemiBold) },
        supportingContent = { 
            Text(
                if (isPresent) "Working Correctly" else "Not Detected",
                color = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            ) 
        },
        leadingContent = {
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
        },
        trailingContent = {
            Icon(
                imageVector = if (isPresent) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    )
}

@Composable
fun HardwareTestList() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TestItem(title = "Touch Screen Test", description = "Check multi-touch and dead zones", icon = Icons.Default.TouchApp)
        TestItem(title = "Audio Output", description = "Verify speakers and frequencies", icon = Icons.AutoMirrored.Filled.VolumeUp)
        TestItem(title = "Camera Focus", description = "Test lens and focus mechanism", icon = Icons.Default.Camera)
        TestItem(title = "Vibration Motor", description = "Verify haptic feedback engine", icon = Icons.Default.Vibration)
    }
}

@Composable
fun TestItem(title: String, description: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = { /* TODO: Launch Test */ },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Test", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun InfoSection(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
