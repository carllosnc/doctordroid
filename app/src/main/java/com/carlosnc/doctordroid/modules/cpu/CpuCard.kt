package com.carlosnc.doctordroid.modules.cpu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnc.doctordroid.ui.components.DashboardListItem

@Composable
fun CpuCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DashboardListItem(
        title = "CPU & Performance",
        subtitle = "Processor & system speed",
        leftIcon = Icons.Default.Speed,
        onClick = onClick,
        modifier = modifier
    )
}
