package com.carlosnc.doctordroid.modules.health

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun HealthCheckCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "Health Check",
        subtitle = "Sensor & Hardware diagnostics",
        leftIcon = Icons.Default.HealthAndSafety,
        onClick = onClick,
        modifier = modifier
    )
}
