package com.carlosnc.doctordroid.modules.battery

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun BatteryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "Battery Details",
        subtitle = "Health, level & status",
        leftIcon = Icons.Default.BatteryFull,
        onClick = onClick,
        modifier = modifier
    )
}
