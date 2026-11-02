package com.carlosnc.doctordroid.modules.battery

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlosnc.doctordroid.ui.components.PageTitle
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BatteryViewModel = viewModel()
) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle(text = "Battery Status") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BatteryUsageCard(batteryInfo)

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time Power & Wattage Card
            LivePowerCard(batteryInfo)

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            BatteryDetailsList(batteryInfo)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BatteryUsageCard(batteryInfo: BatteryInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BatteryDonutChart(
                percentage = batteryInfo.level.toFloat() / 100f,
                isCharging = batteryInfo.status == "Charging",
                modifier = Modifier.size(170.dp)
            )
        }
    }
}

@Composable
fun BatteryDonutChart(percentage: Float, isCharging: Boolean, modifier: Modifier = Modifier) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "BatteryDonutAnimation"
    )

    val color = when {
        isCharging -> Color(0xFF0284C7)
        percentage > 0.6f -> Color(0xFF10B981)
        percentage > 0.2f -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }
    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedPercentage,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isCharging) {
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = "Charging",
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "${(animatedPercentage * 100).toInt()}%",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isCharging) "Charging" else "Remaining",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LivePowerCard(batteryInfo: BatteryInfo) {
    val isCharging = batteryInfo.status == "Charging"
    val powerFormatted = String.format(Locale.getDefault(), "%.1f W", batteryInfo.powerWatts)
    val currentFormatted = "${if (batteryInfo.currentNowMa > 0) "+" else ""}${batteryInfo.currentNowMa} mA"

    val rateLabel = when {
        isCharging && batteryInfo.powerWatts >= 25f -> "Ultra Turbo"
        isCharging && batteryInfo.powerWatts >= 15f -> "Fast Charging"
        isCharging -> "Charging"
        abs(batteryInfo.currentNowMa) > 600 -> "Active Drain"
        else -> "Standby"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCharging) Icons.Outlined.Bolt else Icons.Outlined.EnergySavingsLeaf,
                        contentDescription = null,
                        tint = if (isCharging) Color(0xFF0284C7) else Color(0xFF10B981),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Real-Time Power",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                Surface(
                    color = if (isCharging) Color(0xFF0284C7).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    shape = CircleShape,
                    border = BorderStroke(
                        1.dp,
                        if (isCharging) Color(0xFF0284C7).copy(alpha = 0.25f) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Text(
                        text = rateLabel,
                        color = if (isCharging) Color(0xFF0284C7) else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PowerStatItem(label = "Power Rate", value = powerFormatted, highlight = isCharging)
                PowerStatItem(label = "Current", value = currentFormatted, highlight = false)
                PowerStatItem(
                    label = "Voltage",
                    value = String.format(Locale.getDefault(), "%.2f V", batteryInfo.voltage / 1000f),
                    highlight = false
                )
            }
        }
    }
}

@Composable
fun PowerStatItem(label: String, value: String, highlight: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (highlight) Color(0xFF0284C7) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BatteryDetailsList(batteryInfo: BatteryInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = "SPECIFICATIONS & HEALTH",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        BatteryDetailItem(label = "Health Condition", value = batteryInfo.health)
        BatteryDetailItem(label = "Status", value = batteryInfo.status)
        BatteryDetailItem(label = "Power Source", value = batteryInfo.plugged)
        BatteryDetailItem(label = "Estimated Capacity", value = "${batteryInfo.capacity} mAh")
        BatteryDetailItem(label = "Battery Chemistry", value = batteryInfo.technology)
        BatteryDetailItem(
            label = "Charge Cycles",
            value = if (batteryInfo.cycleCount >= 0) "${batteryInfo.cycleCount} full cycles" else "Available on Android 14+"
        )
        BatteryDetailItem(
            label = "Temperature",
            value = String.format(Locale.getDefault(), "%.1f °C", batteryInfo.temperature / 10f)
        )
    }
}

@Composable
fun BatteryDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}