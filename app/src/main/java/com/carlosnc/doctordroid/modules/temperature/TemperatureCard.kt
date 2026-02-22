package com.carlosnc.doctordroid.modules.temperature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun TemperatureCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "Temperature",
        subtitle = "CPU, Battery & Sensors",
        leftIcon = Icons.Default.Thermostat,
        onClick = onClick,
        modifier = modifier
    )
}
