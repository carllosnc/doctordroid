package com.carlosnc.doctordroid.modules.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.carlosnc.doctordroid.ui.components.PageTitle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.abs

enum class SensorTab {
    ACCELEROMETER,
    PROXIMITY,
    LIGHT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveSensorTestOverlay(
    initialTab: SensorTab = SensorTab.ACCELEROMETER,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    var selectedTab by remember { mutableStateOf(initialTab) }

    // Intercept hardware/gesture back
    BackHandler {
        onDismiss()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle("Sensor Diagnostics") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sensor Switcher Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SensorTabChip(
                    title = "Tilt",
                    icon = Icons.Outlined.ScreenRotation,
                    isSelected = selectedTab == SensorTab.ACCELEROMETER,
                    onClick = { selectedTab = SensorTab.ACCELEROMETER }
                )
                SensorTabChip(
                    title = "Proximity",
                    icon = Icons.Outlined.NearMe,
                    isSelected = selectedTab == SensorTab.PROXIMITY,
                    onClick = { selectedTab = SensorTab.PROXIMITY }
                )
                SensorTabChip(
                    title = "Light",
                    icon = Icons.Outlined.LightMode,
                    isSelected = selectedTab == SensorTab.LIGHT,
                    onClick = { selectedTab = SensorTab.LIGHT }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                SensorTab.ACCELEROMETER -> AccelerometerTestView(sensorManager)
                SensorTab.PROXIMITY -> ProximityTestView(sensorManager, context)
                SensorTab.LIGHT -> LightTestView(sensorManager)
            }
        }
    }
}

@Composable
private fun SensorTabChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected
        ),
        modifier = modifier
    )
}

@Composable
private fun AccelerometerTestView(sensorManager: SensorManager) {
    var ax by remember { mutableFloatStateOf(0f) }
    var ay by remember { mutableFloatStateOf(0f) }
    var az by remember { mutableFloatStateOf(9.8f) }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    DisposableEffect(sensorManager) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    ax = event.values[0]
                    ay = event.values[1]
                    az = event.values[2]
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    if (accelerometer == null) {
        SensorUnavailableMessage("Accelerometer not detected on this device.")
        return
    }

    val isLevel = abs(ax) < 0.6f && abs(ay) < 0.6f
    val bubbleColor by animateColorAsState(
        targetValue = if (isLevel) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
        label = "bubbleColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLevel) "PERFECTLY LEVEL (0°)" else "Tilt device to move bubble",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isLevel) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bubble Level Canvas
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Target rings
                    drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = radius * 0.7f, style = Stroke(width = 1.5.dp.toPx()))
                    drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = radius * 0.4f, style = Stroke(width = 1.5.dp.toPx()))
                    drawCircle(color = if (isLevel) Color(0xFF10B981) else Color.Gray.copy(alpha = 0.6f), radius = 24.dp.toPx(), style = Stroke(width = 2.dp.toPx()))

                    // Crosshair lines
                    drawLine(color = Color.Gray.copy(alpha = 0.3f), start = Offset(center.x, center.y - radius), end = Offset(center.x, center.y + radius), strokeWidth = 1.dp.toPx())
                    drawLine(color = Color.Gray.copy(alpha = 0.3f), start = Offset(center.x - radius, center.y), end = Offset(center.x + radius, center.y), strokeWidth = 1.dp.toPx())

                    // Bubble position (Inverted X because tilt left tilts ball left)
                    val maxOffset = radius - 20.dp.toPx()
                    val bubbleX = center.x - (ax / 9.8f).coerceIn(-1f, 1f) * maxOffset
                    val bubbleY = center.y + (ay / 9.8f).coerceIn(-1f, 1f) * maxOffset

                    drawCircle(
                        color = bubbleColor,
                        radius = 18.dp.toPx(),
                        center = Offset(bubbleX, bubbleY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Live Values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SensorMetric("X Axis", String.format(Locale.getDefault(), "%.2f", ax) + " m/s²")
                SensorMetric("Y Axis", String.format(Locale.getDefault(), "%.2f", ay) + " m/s²")
                SensorMetric("Z Axis", String.format(Locale.getDefault(), "%.2f", az) + " m/s²")
            }
        }
    }
}

@Composable
private fun ProximityTestView(sensorManager: SensorManager, context: Context) {
    var distance by remember { mutableFloatStateOf(10f) }
    var maxRange by remember { mutableFloatStateOf(5f) }
    val proximitySensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) }

    DisposableEffect(sensorManager) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_PROXIMITY) {
                    distance = event.values[0]
                    maxRange = event.sensor.maximumRange
                    // Give light haptic feedback when covered
                    if (distance < maxRange) {
                        triggerVibration(context)
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (proximitySensor != null) {
            sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    if (proximitySensor == null) {
        SensorUnavailableMessage("Proximity sensor not detected on this device.")
        return
    }

    val isCovered = distance < maxRange
    val cardColor by animateColorAsState(
        targetValue = if (isCovered) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        label = "cardColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.5.dp, if (isCovered) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.NearMe,
                contentDescription = null,
                tint = if (isCovered) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isCovered) "OBJECT DETECTED NEARBY" else "NO OBJECT NEARBY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCovered) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Wave your hand over the top speaker to test the sensor.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isCovered) "Status: NEAR (Covered)" else "Status: FAR (Open)",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    fontWeight = FontWeight.Bold,
                    color = if (isCovered) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun LightTestView(sensorManager: SensorManager) {
    var lux by remember { mutableFloatStateOf(0f) }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }

    DisposableEffect(sensorManager) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_LIGHT) {
                    lux = event.values[0]
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (lightSensor != null) {
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    if (lightSensor == null) {
        SensorUnavailableMessage("Ambient Light sensor not detected on this device.")
        return
    }

    val progress = (lux / 1000f).coerceIn(0f, 1f)
    val lightCategory = when {
        lux < 10 -> "Dark Room"
        lux < 50 -> "Dim Light"
        lux < 400 -> "Normal Indoors"
        lux < 1000 -> "Bright Office"
        else -> "Outdoor Daylight / Sunlight"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.LightMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${lux.toInt()} LUX",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = lightCategory,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cover the screen with your hand or expose to light to see the meter change.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SensorMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SensorUnavailableMessage(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

private fun triggerVibration(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(50)
        }
    } catch (_: Exception) {}
}
